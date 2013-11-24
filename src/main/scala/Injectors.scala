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


import org.apache.avro.generic.GenericData
import org.apache.avro.util.Utf8
import scala.tools.scalap.scalax.rules.scalasig.{SingleType, TypeRefType }
import com.novus.salat._
import impls._
import transformers._
import in._
import com.novus.salat.Context
import org.scala_tools.time.Imports._
import org.joda.time.format.ISODateTimeFormat

object Injectors {
println("an avro Injectors was made")
  def select(pt: TypeRefType, hint: Boolean = false)(implicit ctx: Context): Option[Transformer] = {
println("avro Injectors select")
    pt match {

      case IsOption(t@TypeRefType(_, _, _)) => println("avro Injectors matched an IsOption " + t);t match {
        case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) =>{println("avro injectors is option is BigDec")
          Some(new Transformer(symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector with BigDecimalInjector)}


        case TypeRefType(_, symbol, _) if isInt(symbol.path) =>{println("avro injectors is option is isInt")
          Some(new Transformer(symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector with LongToInt)}

        case TypeRefType(_, symbol, _) if isBigInt(symbol.path) =>{println("avro injectors is option is isBigInt")
          Some(new Transformer(symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector with BigIntInjector)}

        case TypeRefType(_, symbol, _) if isChar(symbol.path) =>{println("avro injectors is option is isChar")
          Some(new Transformer(symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector with StringToChar)}

        case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>{println("avro injectors is option is isJoda")
          Some(new Transformer(symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector with StringToJodaDateTime)}

        case TypeRefType(_, symbol, _) if IsTraversable.unapply(t).isDefined => {println("avro injectors is option is traversable")
          Some(new Transformer(symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector with TraversableInjector {
            val parentType = t
          })
        }


       // case t@TypeRefType(_, _, _) if IsEnum.unapply(t).isDefined => {
        //  Some(new Transformer(IsEnum.unapply(t).get.symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector  with EnumInflater)
       // }
        case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" => { println("avro injectors isoption if sym.path")
          Some(new Transformer(prefix.symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector with EnumInflater)
        }

        case TypeRefType(_, symbol, _) =>  {println("avro injectors match catch all");
          Some(new Transformer(symbol.path, t)(ctx) with NullToNoneInjector with OptionInjector )}

        case _ =>         {println("avro injectors match catch all");  Some(new Transformer("", t)(ctx) with NullToNoneInjector with OptionInjector )}


      }

      case IsTraversable(t@TypeRefType(_, _, _)) => println("avro matched an IsTraversable"); t match {
        case TypeRefType(_, symbol, _) =>
          Some(new Transformer(symbol.path, t)(ctx) with TraversableInjector {
            val parentType = pt
          })
      }


      case IsMap(_, t@TypeRefType(_, _, _)) => println("avro matched an IsMap"); t match {
        case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) =>
          Some(new Transformer(symbol.path, t)(ctx) with BigDecimalInjector with MapInjector {
            val parentType = pt
          })

        case TypeRefType(_, symbol, _) if isInt(symbol.path) =>
          Some(new Transformer(symbol.path, t)(ctx) with LongToInt with HashMapToMapInjector {
            val parentType = pt
          })

        case TypeRefType(_, symbol, _) if isBigInt(symbol.path) =>
          Some(new Transformer(symbol.path, t)(ctx) with BigIntInjector with MapInjector {
            val parentType = pt
          })

        case TypeRefType(_, symbol, _) if isChar(symbol.path) =>
          Some(new Transformer(symbol.path, t)(ctx) with StringToChar with HashMapToMapInjector {
            val parentType = pt
          })

        case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
          Some(new Transformer(symbol.path, t)(ctx) with DateToJodaDateTime with HashMapToMapInjector {
            val parentType = pt
          })

        case TypeRefType(_, symbol, _) => Some(new Transformer(symbol.path, t)(ctx) with HashMapToMapInjector {
          val parentType = pt
        })
      }

      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) => { println("avro matched a TypeRefType, gonna make a Transformer");
        Some(new Transformer(symbol.path, pt)(ctx) with StringToJodaDateTime) }

      case _ => println("avro found none in avro select: None");None
    }
  }
}

trait NullToNoneInjector extends Transformer {
  self: Transformer =>

  override def before(value: Any)(implicit ctx: Context): Option[Any] = value match {
    case null => None
    case v => Some(v)
  }
}

trait TraversableInjector extends Transformer {
  self: Transformer =>
  import scala.collection.JavaConverters._


  override def transform(value: Any)(implicit ctx: Context): Any = value match {
    case array: GenericData.Array[_] =>
      val traversable = array.asScala.toTraversable.map {
        case utf8: Utf8 => utf8.toString
        case record: GenericData.Record =>
          val grater: SingleAvroGrater[_] =
            ctx.asInstanceOf[AvroContext].lookp(record.getSchema.getFullName).get.asInstanceOf[SingleAvroGrater[_]]
        val reader  = grater.asGenericDatumReader
        reader.applyValues(record)
        case v => v
      }
      traversableImpl(parentType, traversable)
    case _ => value
  }
println("avro TraversableInjector transform")
  def parentType: TypeRefType
}

trait HashMapToMapInjector extends Transformer {
  self: Transformer =>
  import scala.collection.JavaConverters._

//println("avro transform")


  override def transform(value: Any)(implicit ctx: Context): Any = {println("avro HashMap transform"); value}

  override def after(value: Any)(implicit ctx: Context): Option[Any] = value match {
    case jhm: java.util.HashMap[_,_] =>
      val result = jhm.asScala.map {
        case (k: Utf8, v: Utf8) => k.toString -> v.toString
        case (k: Utf8, v) => k.toString -> super.transform(v)
      }
      Some(mapImpl(parentType, result))
  }
println("avro HashMapInjector after")
  val parentType: TypeRefType
}

trait StringToJodaDateTime extends Transformer {
  self: Transformer =>
  val format =  ISODateTimeFormat.dateTime()
  override def transform(value: Any)(implicit ctx: Context): Any = value match {
    case str: String => format.parseDateTime(str)
  }
}
