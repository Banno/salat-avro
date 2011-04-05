package com.banno.salat.avro

import com.novus.salat.IsOption
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
      
      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
        Some(new Transformer(symbol.path, pt)(ctx) with StringToJodaDateTime)

      case _ => None
    }
  }
}

trait StringToJodaDateTime extends Transformer {
  self: Transformer =>
  val format =  ISODateTimeFormat.dateTime()

  override def transform(value: Any)(implicit ctx: Context): Any = value match {
    case str: String => format.parseDateTime(str)
  }
}

