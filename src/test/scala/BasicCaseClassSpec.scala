package com.banno.salat.avro.test
import org.specs2.matcher.Matcher

import com.banno.salat.avro._
import global._

import scala.collection.JavaConversions._
import org.specs2.mutable._
import org.apache.avro.Schema
import org.apache.avro.io.{DatumWriter, EncoderFactory}

import java.io.ByteArrayOutputStream

object BasicCaseClassSpec extends Specification {
  import models._

  "a grater" should {
    "make an avro schema for a basic case class" in {
      val schema = grater[Edward].asAvroSchema
      println(schema)
      schema must containField("a" -> Schema.Type.STRING)
      schema must containField("b" -> Schema.Type.INT)
    }

    "make a datum writer for a basic case class" in {
      val datumWriter: DatumWriter[Edward] = grater[Edward].asDatumWriter

      val baos = new ByteArrayOutputStream
      val encoder = EncoderFactory.get.jsonEncoder(grater[Edward].asAvroSchema, baos)

      datumWriter.write(ed, encoder)
      encoder flush

      val json = new String(baos.toByteArray)
      json must /("a" -> ed.a)
      json must /("b" -> ed.b)
    }
  }

  def containField(pair: Pair[String, Schema.Type]): Matcher[Schema] = ((_: Schema).getFields.map(f => (f.name, f.schema.getType)).contains(pair),
                                                                       (schema: Schema) => "Schema\n\t%s\n\tdoesn't have a field named '%s' of type '%s'".format(schema, pair._1, pair._2))
}
