/*
 * Copyright 2011 T8 Webware
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.banno.salat.avro

import com.novus.salat._
import org.apache.avro.Schema
import org.apache.avro.io.ResolvingDecoder
import scala.collection.JavaConverters._
import scala.collection.mutable.{ LinkedHashMap, ListBuffer }
import org.apache.avro.io.{ Decoder, DatumReader }
import org.apache.avro.generic.{ GenericData, GenericDatumReader }
import org.apache.avro.util.Utf8

trait AvroDatumReader[X] extends DatumReader[X] {
  def read(decoder: Decoder): X
}

class AvroGenericDatumReader[X](schema: Schema)(implicit ctx: Context)
  extends GenericDatumReader[X](schema) with AvroDatumReader[X] {

  def read(decoder: Decoder): X = {
    val collectingGenericData = new CollectingGenericData
    val colletingReader = new CollectingGenericDatumReader(schema, collectingGenericData)

    colletingReader.read(null, decoder)

    val rootRecord = collectingGenericData.rootRecord

    applyValues(rootRecord).asInstanceOf[X]
  }

  def applyValues(genericRecord: GenericData.Record): AnyRef = {
    // println("-------- apply values -------")
    // println("record = " + genericRecord)
    val values = genericRecord.getSchema.getFields.asScala.map(_.name).map(genericRecord.get(_))
    // println("values = " + values)
    // println("values classes = " + values.map(_.getClass))

    val grater: SingleAvroGrater[_] = ctx.lookup(genericRecord.getSchema.getFullName).get.asInstanceOf[SingleAvroGrater[_]]

    val arguments = grater._indexedFields.zip(values).map {
      case (field, record: GenericData.Record) => Some(applyValues(record))
      case (field, value) =>
        val inTransformer = Injectors.select(field.typeRefType).getOrElse(field.in)
        inTransformer.transform_!(value)
//      case (field, _) => grater.safeDefault(field)
    }.map(_.getOrElse(None).asInstanceOf[AnyRef])

    // println("arguments = " + arguments)
    // println("argument classes = " + arguments.map(_.getClass))

    grater._constructor.newInstance(arguments: _*).asInstanceOf[AnyRef]
  }

  protected class CollectingGenericData extends GenericData {
    var rootRecord: GenericData.Record = _
    
    override def setField(record: Any, name: String, pos: Int, obj: Object) {
      val genericRecord = record.asInstanceOf[GenericData.Record]
      // The last record to have a field set is the root record
      rootRecord = genericRecord
      // println("------- set field --------")
      // println("genericRecord = " + genericRecord)
      // println("genericRecord.class = " + genericRecord.getClass)
      // println("name = " + name)
      // println("pos = " + pos)
      // println("obj = " + obj)
      // if (obj != null) {
      //   println("obj.class = " + obj.getClass)
      // }
      val scalaObj = obj match {
        case utf8: Utf8 => utf8.toString
        case x => x
      }
      genericRecord.put(name, scalaObj)
    }
  }

  protected class CollectingGenericDatumReader(schema: Schema, collector: CollectingGenericData) extends GenericDatumReader[Object](schema, schema, collector)
}
