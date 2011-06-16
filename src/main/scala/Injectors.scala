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

import com.novus.salat.IsOption
import com.novus.salat.IsSeq
import org.apache.avro.generic.GenericData
import org.apache.avro.util.Utf8
import scala.tools.scalap.scalax.rules.scalasig.TypeRefType
import com.novus.salat.transformers._
import in._
import com.novus.salat.Context
import org.scala_tools.time.Imports._
import org.joda.time.format.ISODateTimeFormat

object Injectors {
  def select(pt: TypeRefType, hint: Boolean = false)(implicit ctx: Context): Option[Transformer] = {
    pt match {
      
      case IsOption(t@TypeRefType(_, _, _)) => t match {
        
        case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
          Some(new Transformer(symbol.path, t)(ctx) with OptionInjector with StringToJodaDateTime)
        
        case _ => None
      }
      
      case IsSeq(t@TypeRefType(_, _, _)) => t match {
        case TypeRefType(_, symbol, _) =>
          Some(new Transformer(symbol.path, t)(ctx) with SeqInjector)
      }
      
      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
        Some(new Transformer(symbol.path, pt)(ctx) with StringToJodaDateTime)

      case _ => None
    }
  }
}

trait SeqInjector extends Transformer {
  self: Transformer =>
  import scala.collection.JavaConverters._

  override def transform(value: Any)(implicit ctx: Context): Any = value match {
    case array: GenericData.Array[_] => array.asScala.toList.map {
      case utf8: Utf8 => utf8.toString
      case record: GenericData.Record =>
        val grater: SingleAvroGrater[_] =
          ctx.lookup(record.getSchema.getFullName).get.asInstanceOf[SingleAvroGrater[_]]
        val reader  = grater.asGenericDatumReader
        reader.applyValues(record)
      case v => v
    }
    case _ => value
  }
}

trait StringToJodaDateTime extends Transformer {
  self: Transformer =>
  val format =  ISODateTimeFormat.dateTime()

  override def transform(value: Any)(implicit ctx: Context): Any = value match {
    case str: String => format.parseDateTime(str)
  }
}
