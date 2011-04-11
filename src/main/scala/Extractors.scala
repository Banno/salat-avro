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
import scala.tools.scalap.scalax.rules.scalasig.TypeRefType
import com.novus.salat.transformers._
import out._
import com.novus.salat.Context

import org.scala_tools.time.Imports._
import org.joda.time.format.ISODateTimeFormat

object Extractors {
  def select(t: TypeRefType, hint: Boolean = false)(implicit ctx: Context): Option[Transformer] = t match {

    case IsOption(t@TypeRefType(_, _, _)) => t match {
      
      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
        Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor with JodaTimeToString)
      
      case TypeRefType(_, symbol, _) if hint || ctx.lookup(symbol.path).isDefined =>
        Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor)

      case _ => None
    }

    case TypeRefType(_, symbol, _) => t match {
      
      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
        Some(new Transformer(symbol.path, t)(ctx) with JodaTimeToString)

      case TypeRefType(_, symbol, _) if hint || ctx.lookup(symbol.path).isDefined =>
        Some(new Transformer(symbol.path, t)(ctx) {})
      
      case _ => None
    }

    case _ => None
  }
}

trait JodaTimeToString extends Transformer {
  self: Transformer =>
  val format =  ISODateTimeFormat.dateTime()
  override def transform(value: Any)(implicit ctx: Context) = value match {
    case dt: DateTime => dt.toString(format)
  }
}