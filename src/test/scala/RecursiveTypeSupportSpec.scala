package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._
import org.apache.avro.Schema
import scala.collection.JavaConverters._

object RecursiveTypeSupportSpec extends SalatAvroSpec {
  import models._

  "a grater for a recursive type" should {
    "be able to produce a schema" in {
      grater[Node]
      grater[ManyTrees]
      grater[End]
      
      val schema = grater[Node].asAvroSchema
      schema.getName must_== "union"
      println(schema)
      val recordSchema = schema.getTypes().get(0)
      recordSchema.getName must_== "Node"
      recordSchema.getNamespace must_== "com.banno.salat.avro.test.models"
      
      val recursiveUnion1 = recordSchema.getField("left").schema
      recursiveUnion1.getName must_== "union"
      recursiveUnion1.getTypes.asScala.map(_.getName) must contain(exactly("ManyTrees", "End", "Node"))
      
      val recursiveUnion2 = recordSchema.getField("right").schema
      recursiveUnion2.getName must_== "union"
      recursiveUnion2.getTypes.asScala.map(_.getName) must contain(exactly("ManyTrees", "End", "Node"))
    }

    "be able to read and write" in {
      grater[Node]
      grater[ManyTrees]
      grater[End]

      val oldRecurse = recurse()
      val newRecurse = serializeAndDeserialize(oldRecurse)
      println(newRecurse)
      newRecurse must_== oldRecurse
    }
  }
}