package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._
import org.apache.avro.Schema
import scala.collection.JavaConversions._

object MultiGraterSpec extends SalatAvroSpec {
  import models._

  "a multi-grater" should {
    "make an avro schema that includes multiple records" in {
      val mg = grater[Alice] + (grater[Louis] + grater[Basil]) + grater[Edward]
      val schema = mg.asAvroSchema
      schema.getName must_== "union"
      val types: Iterable[Schema] = schema.getTypes
      types must have(_.getName == "Alice")
      types must have(_.getName == "Louis")
      types must have(_.getName == "Alice")
      types must have(_.getName == "Edward")
    }

    "adding the same grater should not add it to the union twice" in {
      val mg = grater[Alice] + grater[Edward] + grater[Alice]
      val schema = mg.asAvroSchema
      schema.getTypes.size must_== 2 
    }

    "be able to support any type if any of its graters support it" in {
      val mg = grater[Alice] + grater[Edward]
      mg.supports(ed) must beTrue
    }

    "be able to serialize _any_ of graters that it contains" in {
      val mg = grater[Alice] + grater[Edward]
      val json = serializeToJSON(ed, Some(mg))
      // println("json = " + json)
      json must /("com.banno.salat.avro.test.models.Edward") /("a" -> ed.a)
      json must /("com.banno.salat.avro.test.models.Edward") /("b" -> ed.b)
      json must /("com.banno.salat.avro.test.models.Edward") /("c" -> ed.c)
    }
    
    "be able to deserialize _any_ of graters that it contains" in {
      val oldGraph: Alice = graph
      val mg = grater[Alice] + grater[Edward]
      val newGraph = serializeAndDeserialize(oldGraph, Some(mg))
      newGraph must_== oldGraph
    }

    "be able to deserialize something that was serialized by one of its single avro graters" in {
      pending
      // maybe SingleAvroGrater should serialize into a union type with only 1 type
    }
  }
}
