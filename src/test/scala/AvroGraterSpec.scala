package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._

object AvroGraterSpec extends SalatAvroSpec {
  import models._

  "a grater" should {
    "return whether or not it can support a specific instance" in {
      val g = grater[Basil]
      g.supports(ed) must beFalse
      g.supports(new Basil(None)) must beTrue
    }
  }
}
