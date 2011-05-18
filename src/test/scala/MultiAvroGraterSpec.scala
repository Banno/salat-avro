package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._
import org.apache.avro.Schema
import scala.collection.JavaConversions._

object MultiGraterSpec extends SalatAvroSpec {
  import models._

  "a multi-grater" should {
    "make an avro schema that includes multiple records" in {
      val mg = multiGrater[Alice] + multiGrater[Basil]
      val schema = mg.asAvroSchema
      schema.getName must_== "union"
      val types: Iterable[Schema] = schema.getTypes
      types must have(_.getName == "Alice")
      types must have(_.getName == "Basil")
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
