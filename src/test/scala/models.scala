package com.banno.salat.avro.test.models

import scala.math.{BigDecimal => ScalaBigDecimal}

// from salat's test models
// case class Alice(x: String, y: Option[String] = Some("default y"), z: Basil)
// case class Basil(p: Option[Int], q: Int = 1067, r: Clara)
// case class Clara(l: Seq[String] = Nil, m: List[Int], n: List[Desmond])
// case class Desmond(h: IMap[String, Alice], i: MMap[String, Int] = MMap.empty, j: Option[Basil])

case class Alice(x: String, y: Option[String] = Some("default y"), z: Basil)
case class Basil(p: Option[Int], q: Int = 1067)

case class Edward(a:          String,           b:        Int,           c:        ScalaBigDecimal,
                  aa:  Option[String] = None,   bb: Option[Int] = None,  cc: Option[ScalaBigDecimal] = None,
                  aaa:  Option[String] = None,  bbb: Option[Int] = None, ccc: Option[ScalaBigDecimal] = None)

object Frakked extends Enumeration {
  val JustALittle = Value("just a little")
  val QuiteABit = Value("quite a bit")
  val Majorly = Value("majorly")
  val BeyondRepair = Value("beyond repair")
}

case class Me(name: String, state: Frakked.Value = Frakked.BeyondRepair)
  
