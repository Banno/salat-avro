package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._
import org.apache.avro.Schema

object CaseClassGraphSpecs extends SalatAvroSpec {
  import models._

  "a grater for nested case classes" should {
    "generate an avro schema" in {
      val schema = grater[Alice].asAvroSchema
      println(schema)
      schema.getName must_== "Alice"
      schema.getNamespace must_== "com.banno.salat.avro.test.models"
      schema must containField("x", Schema.Type.STRING)
      schema must containField("y", List(Schema.Type.STRING, Schema.Type.NULL))
      schema must containField("z", Schema.Type.RECORD)
      val basilSchema = schema.getField("z").schema
      basilSchema.getName must_== "Basil"
      basilSchema must containField("p", List(Schema.Type.INT, Schema.Type.NULL))
      basilSchema must containField("q", Schema.Type.INT)
    }

    "write to a JSON encoding" in {
      val json = serializeToJSON(graph)
      println(json)
      json must /("x" -> graph.x)
      json must /("y") /("string" -> graph.y.get)
      json must /("z") /("p") /("int" -> graph.z.p.get)
      json must /("z") /("q" -> graph.z.q)
    }
    
    "serialize and deserialize an object" in {
      val oldGraph = graph()
      val newGraph = serializeAndDeserialize(oldGraph)
      newGraph must_== oldGraph
    }
  }
}
