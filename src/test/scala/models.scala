package com.banno.salat.avro.test.models

import com.novus.salat.annotations._
import scala.math.{BigDecimal => ScalaBigDecimal}
import org.scala_tools.time.Imports._
import scala.collection.immutable.{Map => IMap}
import scala.collection.mutable.{Map => MMap}

// from salat's test models
// case class Alice(x: String, y: Option[String] = Some("default y"), z: Basil)
// case class Basil(p: Option[Int], q: Int = 1067, r: Clara)
// case class Clara(l: Seq[String] = Nil, m: List[Int], n: List[Desmond])
// case class Desmond(h: IMap[String, Alice], i: MMap[String, Int] = MMap.empty, j: Option[Basil])

case class Alice(x: String, y: Option[String] = Some("default y"), z: Basil)
case class Basil(p: Option[Int], q: Int = 1067)
case class Clara(l: Seq[String] = Nil, m: List[Int] = Nil, n: List[Desmond] = Nil)
case class Desmond(h: IMap[String, Int] = Map.empty, i: MMap[String, Int] = MMap.empty) //, j: Option[Basil])

case class Edward(a:          String,           b:        Int,           c:        ScalaBigDecimal,
                  aa:  Option[String] = None,   bb: Option[Int] = None,  cc: Option[ScalaBigDecimal] = None,
                  aaa:  Option[String] = None,  bbb: Option[Int] = None, ccc: Option[ScalaBigDecimal] = None)

case class Fred(a: Alice, c: Option[Clara])

object Frakked extends Enumeration {
  val JustALittle = Value("just a little")
  val QuiteABit = Value("quite a bit")
  val Majorly = Value("majorly")
  val BeyondRepair = Value("beyond repair")
}

case class Me(name: String, state: Frakked.Value = Frakked.BeyondRepair)

case class Neville(ennui: Boolean = true, asOf: DateTime = new DateTime)

case class Louis(id: Long)
case class Dirk(awesomeness: Double)

case class Department(minions: Option[List[String]])

case class Location(latitude: Double, longitude: Double)
case class User(username: String)
case class UserAt(user: User, location: Location)

case class HasASet(s: Set[Int])

@Salat
trait SomeCommonTrait
case class SomeSubclassExtendingSaidTrait(b: Int) extends SomeCommonTrait
case class AnotherSubclassExtendingSaidTrait(d: Double) extends SomeCommonTrait

case class SomeContainerClass(e: String, theListWhichNeedsToBeTested: List[SomeCommonTrait])


@Salat
trait Tree
case class Node(left: Tree, right: Tree) extends Tree
case class ManyTrees(value: Int, nodes: Seq[Tree]) extends Tree
case class End(value: Int) extends Tree
