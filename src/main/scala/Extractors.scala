package com.banno.salat.avro

import com.novus.salat.IsOption
import scala.tools.scalap.scalax.rules.scalasig.TypeRefType
import com.novus.salat.transformers.{ out, Transformer }
import out._
import com.novus.salat.Context

object Extractors {
  def select(t: TypeRefType, hint: Boolean = false)(implicit ctx: Context): Option[Transformer] = t match {

    case IsOption(t@TypeRefType(_, _, _)) => t match {
      case TypeRefType(_, symbol, _) if hint || ctx.lookup(symbol.path).isDefined =>
        Some(new Transformer(symbol.path, t)(ctx) with OptionExtractor)

      case _ => None
    }

    case TypeRefType(_, symbol, _) => t match {
      case TypeRefType(_, symbol, _) if hint || ctx.lookup(symbol.path).isDefined =>
        Some(new Transformer(symbol.path, t)(ctx) {})
      
      case _ => None
    }

    case _ => None
  }
}
