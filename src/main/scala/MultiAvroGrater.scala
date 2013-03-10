/*
 * Copyright 2011-2013 T8 Webware
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
import org.apache.avro.io.DatumReader
import org.apache.avro.io.DatumWriter
import org.apache.avro.io.Decoder
import org.apache.avro.io.Encoder
import scala.collection.JavaConversions._
import scala.collection.mutable.LinkedHashSet
import scala.collection.mutable.ListBuffer

class UnsupportedCaseClassMultiException(obj: Any)
extends RuntimeException("Cannot serialize " + obj)

class MultiAvroGrater(val graters: LinkedHashSet[SingleAvroGrater[_]])(implicit val ctx: Context)
extends AvroGrater[CaseClass] {
  
  def +(other: AvroGrater[_]) = other match {
    case mg: MultiAvroGrater => new MultiAvroGrater(graters ++ mg.graters)
    case sg: SingleAvroGrater[_] => new MultiAvroGrater(graters + sg)
  }
  
  lazy val asAvroSchema: Schema = asSingleAvroSchema(new ListBuffer[Schema])
  
  private[avro] def asSingleAvroSchema(knownSchemas: ListBuffer[Schema]) =
    Schema.createUnion(graters.toList.map(_.asSingleAvroSchema(knownSchemas)))
  
  def supports[X](x: X)(implicit manifest: Manifest[X]): Boolean =
    graters.exists(_.supports(x))
}