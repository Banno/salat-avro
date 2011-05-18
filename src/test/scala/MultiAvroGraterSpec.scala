package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._

object MultiGraterSpec extends SalatAvroSpec {
  import models._

  "a multi-grater" should {
    "make an avro schema that includes multiple records" in {
      val mg = multiGrater[Alice] + multiGrater[Basil]
      val schema = mg.asAvroSchema
      schema.getName must_== "union"
      
      println(schema)
      println(schema.getName)
      pending
    }

    "should be able to be created via a combining regular graters" in {
      val mg = grater[Alice] + grater[Basil] + grater[Edward]
      val schema = mg.asAvroSchema
      schema.getName must_== "union"
    }

    // be able to serialize _any_ of graters that it contains 
    // be able to deserialize _any_ of graters that it contains 
  }
}
