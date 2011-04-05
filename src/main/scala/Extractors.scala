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
      case TypeRefType(_, symbol, _) if hint || ctx.lookup(symbol.path).isDefined =>
        Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor)
      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
        Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor with JodaTimeToString)

      case _ => None
    }

    case TypeRefType(_, symbol, _) => t match {
      case TypeRefType(_, symbol, _) if hint || ctx.lookup(symbol.path).isDefined =>
        Some(new Transformer(symbol.path, t)(ctx) {})
      case TypeRefType(_, symbol, _) if isJodaDateTime(symbol.path) =>
        Some(new Transformer(symbol.path, t)(ctx) with JodaTimeToString)
      
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
