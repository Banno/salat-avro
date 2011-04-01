package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._

import scala.collection.JavaConversions._
import org.specs2.mutable._
import org.apache.avro.Schema
import org.apache.avro.io.{ DatumWriter, EncoderFactory }
import org.specs2.matcher.Matcher

import java.io.ByteArrayOutputStream

object BasicCaseClassSpec extends Specification {
  import models._

  "a grater" should {
    "make an avro schema for a basic case class" in {
      val schema = grater[Edward].asAvroSchema
      println(schema)
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
      val datumWriter: DatumWriter[Edward] = grater[Edward].asDatumWriter

      val baos = new ByteArrayOutputStream
      val encoder = EncoderFactory.get.jsonEncoder(grater[Edward].asAvroSchema, baos)

      datumWriter.write(ed, encoder)
      encoder flush

      val json = new String(baos.toByteArray)
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
  }

  def containField(name: String, schemaType: Schema.Type): Matcher[Schema] =
    ((_: Schema).getFields.map(f => (f.name, f.schema.getType)).contains(Pair(name, schemaType)),
     (schema: Schema) => "Schema\n\t%s\n\tdoesn't have a field named '%s' of type '%s'".format(schema, name, schemaType))

  def containField(name: String, schemaTypes: List[Schema.Type]): Matcher[Schema] =
    ((_: Schema).getFields.filter(_.schema.getType eq Schema.Type.UNION).map(f => (f.name, f.schema.getTypes.map(_.getType).toList)).contains(Pair(name, schemaTypes)),
      (schema: Schema) => "Schema\n\t%s\n\tdoesn't have a field named '%s' of union types '%s'".format(schema, name, schemaTypes))
}
