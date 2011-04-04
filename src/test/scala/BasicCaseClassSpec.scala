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
      schema.getName must_== "Edward"
      schema.getNamespace must_== "com.banno.salat.avro.test.models"
      schema must containField("a", Schema.Type.STRING)
      schema must containField("b", Schema.Type.INT)
      schema must containField("c", Schema.Type.DOUBLE)
      schema must containField("aa", List(Schema.Type.STRING, Schema.Type.NULL))
      schema must containField("bb", List(Schema.Type.INT, Schema.Type.NULL))
      schema must containField("cc", List(Schema.Type.DOUBLE, Schema.Type.NULL))
      schema must containField("aaa", List(Schema.Type.STRING, Schema.Type.NULL))
      schema must containField("bbb", List(Schema.Type.INT, Schema.Type.NULL))
      schema must containField("ccc", List(Schema.Type.DOUBLE, Schema.Type.NULL))
    }

    "make a datum writer for a basic case class" in {
      val json = serializeToJSON(ed)
      println(json)
      json must /("a" -> ed.a)
      json must /("b" -> ed.b)
      json must /("c" -> ed.c)
      json must /("aa") /("string" -> ed.aa.get)
      json must /("bb") /("int" -> ed.bb.get)
      json must /("cc") /("double" -> ed.cc.get)
      json must /("aaa" -> null)
      json must /("bbb" -> null)
      json must /("ccc" -> null)
    }

    "make a datum reader for a basic case class" in {
      val oldEd = ed
      val newEd: Edward = serializeAndDeserialize(oldEd)
      println(newEd)
      newEd must_== oldEd
    }
  }

}
