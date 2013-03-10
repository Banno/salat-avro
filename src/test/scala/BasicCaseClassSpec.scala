package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._

import java.io.ByteArrayOutputStream
import org.apache.avro.io.{ DatumReader, DatumWriter, DecoderFactory, EncoderFactory }
import org.apache.avro.Schema

object BasicCaseClassSpec extends SalatAvroSpec {
  import models._

  "a grater" should {
    "make an avro schema for a basic case class" in {
      val schema = grater[Edward].asAvroSchema
      println(schema)
      schema.getName must_== "union"
      val recordSchema = schema.getTypes().get(0)
      recordSchema.getName must_== "Edward"
      recordSchema.getNamespace must_== "com.banno.salat.avro.test.models"
      recordSchema must containField("a", Schema.Type.STRING)
      recordSchema must containField("b", Schema.Type.INT)
      recordSchema must containField("c", Schema.Type.DOUBLE)
      recordSchema must containField("aa", List(Schema.Type.STRING, Schema.Type.NULL))
      recordSchema must containField("bb", List(Schema.Type.INT, Schema.Type.NULL))
      recordSchema must containField("cc", List(Schema.Type.DOUBLE, Schema.Type.NULL))
      recordSchema must containField("aaa", List(Schema.Type.STRING, Schema.Type.NULL))
      recordSchema must containField("bbb", List(Schema.Type.INT, Schema.Type.NULL))
      recordSchema must containField("ccc", List(Schema.Type.DOUBLE, Schema.Type.NULL))
    }

    "make a datum writer for a basic case class" in {
      val json = serializeToJSON(ed)
      println(json)
      json must /("com.banno.salat.avro.test.models.Edward") /("a" -> ed.a)
      json must /("com.banno.salat.avro.test.models.Edward") /("b" -> ed.b)
      json must /("com.banno.salat.avro.test.models.Edward") /("c" -> ed.c)
      json must /("com.banno.salat.avro.test.models.Edward") /("aa") /("string" -> ed.aa.get)
      json must /("com.banno.salat.avro.test.models.Edward") /("bb") /("int" -> ed.bb.get)
      json must /("com.banno.salat.avro.test.models.Edward") /("cc") /("double" -> ed.cc.get)
      json must /("com.banno.salat.avro.test.models.Edward") /("aaa" -> null)
      json must /("com.banno.salat.avro.test.models.Edward") /("bbb" -> null)
      json must /("com.banno.salat.avro.test.models.Edward") /("ccc" -> null)
    }

    "make a datum reader for a basic case class" in {
      val oldEd = ed
      val newEd: Edward = serializeAndDeserialize(oldEd)
      println(newEd)
      newEd must_== oldEd
    }

    "be able to serialize a basic case with an optional list" in {
      val oldDep = Department(Some(List("me", "you")))
      val newDep = serializeAndDeserialize(oldDep)
      println(newDep)
      newDep must_== oldDep 
    }
    
  }

}
