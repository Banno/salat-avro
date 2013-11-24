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
import scala.tools.scalap.scalax.rules.scalasig.{SingleType, TypeRefType }
import com.novus.salat._
import annotations._
import transformers._
import out._
import com.novus.salat.Context
import scala.collection.JavaConverters._



import org.scala_tools.time.Imports._
import org.joda.time.format.ISODateTimeFormat

object Extractors {

println("made an avro Extractors")
  def select(t: TypeRefType, hint: Boolean = false)(implicit ctx: Context): Option[Transformer] = {println("avro Extractors select"); t match {

    case IsOption(t@TypeRefType(_, _, _)) => {println("avro extractor: It's an Option " + t); t match {

        case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) =>
          Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor with BigDecimalExtractor)

        case TypeRefType(_, symbol, _) if isBigInt(symbol.path) =>
          Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor with BigIntExtractor)

      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
        Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor with JodaTimeToString)

   //   case TypeRefType(_, symbol, _) if hint || Option(ctx.asInstanceOf[AvroContext].lookp(symbol.path)).isDefined => {println("avro extractors isOpton hint || is defined")
     //   Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor)}

   
 case TypeRefType(_, symbol, _) if IsTraversable.unapply(t).isDefined => {println("avro extractors isption istraversable")
        Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor with TraversableExtractor)}



        case TypeRefType(_, symbol, _) if hint || ctx.asInstanceOf[AvroContext].lookp(symbol.path).isDefined => {println("avro extractors isption hint or lookp is defined")
//          Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor with InContextToDBObject {
          Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor  {
            val grater = ctx.asInstanceOf[AvroContext].lookp(symbol.path)
          })}

  
      //case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" => {
       //   new Transformer(prefix.symbol.path, t)(ctx) with OptionExtractor with EnumStringifier
       // }
        case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" => {
          Some(new Transformer(prefix.symbol.path, t)(ctx) with OptionExtractor with EnumStringifier)
        }


      case _ => {println("avor extractors is Option None");None}
    }
}
    case IsMap(_, t @ TypeRefType(_, _, _)) =>{ println("avro: It's a Map");t match {
      case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) =>
        Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor with BigDecimalExtractor)

      case TypeRefType(_, symbol, _) if isBigInt(symbol.path) =>
        Some(new Transformer(symbol.path, t)(ctx)  with OptionExtractor with BigIntExtractor)

      case TypeRefType(_, symbol, _) if isChar(symbol.path) =>
        Some(new Transformer(symbol.path, t)(ctx) with CharToString with MapExtractor)


      case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" =>
          Some(new Transformer(prefix.symbol.path, t)(ctx) with EnumStringifier with MapExtractor)

  //    case t @ TypeRefType(_, _, _) if Option(IsEnum.unapply(t)).isDefined =>{println("avro extractors isMap isEnum " + t)
//          Some(new Transformer(IsEnum.unapply(t).get.symbol.path, t)(ctx) with EnumStringifier with MapExtractor)}
       //   Some(new Transformer(t.symbol.path, t)(ctx) with EnumStringifier with MapExtractor)}

       case TypeRefType(_, symbol, _) if hint || ctx.asInstanceOf[AvroContext].lookp(symbol.path).isDefined => { println("avro extractor isMap single type")
         Some(new Transformer(symbol.path, t)(ctx) with InContextToDBObject with MapExtractor {
           val grater = ctx.asInstanceOf[AvroContext].lookp(symbol.path)
         })}

      // case t @ TypeRefType(_, symbol, _) if IsTraitLike.unapply(t).isDefined =>
      //   Some(new Transformer(symbol.path, t)(ctx) with InContextToDBObject with MapExtractor {
      //     val grater = ctx.lookup(symbol.path)
      //   })

      case TypeRefType(_, symbol, _) =>
        Some(new Transformer(symbol.path, t)(ctx) with MapExtractor)
    }
}

    case IsTraversable(t@TypeRefType(_, _, _)) => {println("avro matched: It's a IsTraversable");t match {
      case TypeRefType(_, symbol, _) =>
        Some(new Transformer(symbol.path, t)(ctx) with TraversableExtractor)

     // case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" => {
     //   new Transformer(prefix.symbol.path, t)(ctx) with EnumStringifier with TraversableExtractor
     // }
    }
}

    case TypeRefType(_, symbol, _) =>{ println("avro matched a TypeRefType");t match {

      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) => { println("avro extractor jodatime")
        Some(new Transformer(symbol.path, t)(ctx) with JodaTimeToString)}

        case TypeRefType(_, symbol, _) if isBigDecimal(symbol.path) => { println("avro extractor isBigDecimal")
          Some(new Transformer(symbol.path, t)(ctx) with BigDecimalExtractor)
        }


     case t @ TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" => {
        Some( new Transformer(prefix.symbol.path, t)(ctx) with EnumStringifier)
      }

      case TypeRefType(_, symbol, _) if hint || Option(ctx.asInstanceOf[AvroContext].lookp(symbol.path)).isDefined =>{println("avro extractor lookp'ed" + t)
        Some(new Transformer(symbol.path, t)(ctx) {})}


      case _ => None
    }}

    case _ => None
  }
}}

trait JodaTimeToString extends Transformer {
  self: Transformer =>
  val format =  ISODateTimeFormat.dateTime()
  override def transform(value: Any)(implicit ctx: Context) = value match {
    case dt: DateTime => dt.toString(format)
  }
}

trait TraversableExtractor extends Transformer {
  import scala.collection.JavaConverters._
  override def transform(value: Any)(implicit ctx: Context) =
    value.asInstanceOf[Traversable[_]].toList.asJava
}

trait MapToHashMapExtractor extends Transformer {
  import scala.collection.JavaConverters._

  override def transform(value: Any)(implicit ctx: Context): Any = {println("avro extractors transform"); value}
  override def after(value: Any)(implicit ctx: Context) = value match {
    case map: scala.collection.Map[String, _] =>
      println("avro extractors transform"); Some(map.asJava)
    case _ =>
      None
  }

}
