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
//Copied over from salat to get access
import scala.math.{ BigDecimal => SBigDecimal, BigInt }
import tools.scalap.scalax.rules.scalasig.{ SingleType, TypeRefType, Type, Symbol }



protected[avro] object Types {
  val Date = "java.util.Date"
  val DateTime = Set("org.joda.time.DateTime", "org.scala_tools.time.TypeImports.DateTime")
  val LocalDateTime = Set("org.joda.time.LocalDateTime", "org.scala_tools.time.TypeImports.LocalDateTime")
  val TimeZone = "java.util.TimeZone"
  val DateTimeZone = Set("org.joda.time.DateTimeZone", "org.scala_tools.time.TypeImports.DateTimeZone")
  val Oid = Set("org.bson.types.ObjectId", "com.mongodb.casbah.commons.TypeImports.ObjectId")
  val BsonTimestamp = "org.bson.types.BSONTimestamp"
  val SBigDecimal = Set("scala.math.BigDecimal", "scala.package.BigDecimal")
  val BigInt = Set("scala.math.BigInt", "scala.package.BigInt")
  val Option = "scala.Option"
  val Map = ".Map"
  val Traversables = Set(".Seq", ".List", ".Vector", ".Set", ".Buffer", ".ArrayBuffer", ".IndexedSeq", ".LinkedList", ".DoubleLinkedList")
  val BitSets = Set("scala.collection.BitSet", "scala.collection.immutable.BitSet", "scala.collection.mutable.BitSet")

  def isOption(sym: Symbol) = sym.path == Option

  def isMap(symbol: Symbol) = symbol.path.endsWith(Map)

  def isTraversable(symbol: Symbol) = Traversables.exists(symbol.path.endsWith(_))

  def isBitSet(symbol: Symbol) = BitSets.contains(symbol.path)

  def isBigDecimal(symbol: Symbol) = SBigDecimal.contains(symbol.path)

  def isBigInt(symbol: Symbol) = BigInt.contains(symbol.path)
}



protected[avro] object TypeMatchers {

  def matchesOneType(t: Type, name: String): Option[Type] = t match {
    case TypeRefType(_, symbol, List(arg)) if symbol.path == name => Some(arg)
    case _ => None
  }

  def matches(t: TypeRefType, name: String) = t.symbol.path == name

  def matches(t: TypeRefType, names: Traversable[String]) = names.exists(t.symbol.path == _)

  def matchesMap(t: Type) = t match {
    case TypeRefType(_, symbol, k :: v :: Nil) if Types.isMap(symbol) => Some(k -> v)
    case _ => None
  }

  def matchesTraversable(t: Type) = t match {
    case TypeRefType(_, symbol, List(arg)) if Types.isTraversable(symbol) => Some(arg)
    case _ => None
  }

  def matchesBitSet(t: Type) = t match {
    case TypeRefType(_, symbol, _) if Types.isBitSet(symbol) => Some(symbol)
    case _ => None
  }
}

import tools.scalap.scalax.rules.scalasig.{ TypeRefType, Type, Symbol }


protected[avro] object IsOption {
  def unapply(t: Type): Option[Type] = TypeMatchers.matchesOneType(t, Types.Option)
}

protected[avro] object IsMap {
  def unapply(t: Type): Option[(Type, Type)] = TypeMatchers.matchesMap(t)
}

protected[avro] object IsTraversable {
  def unapply(t: Type): Option[Type] = TypeMatchers.matchesTraversable(t)
}

object IsEnum   {
  def unapply(t: TypeRefType): Option[SingleType] = {
    t match {
      case TypeRefType(prefix @ SingleType(_, esym), sym, _) if sym.path == "scala.Enumeration.Value" =>
        Some(prefix)
      case _ => None
    }
  }
}