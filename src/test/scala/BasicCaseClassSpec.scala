package com.banno.salat.avro.test
import org.specs2.matcher.Matcher

import com.banno.salat.avro._
import global._
import org.specs2.mutable._
import org.apache.avro.Schema
import scala.collection.JavaConversions._

object BasicCaseClassSpec extends Specification {
  import models._

  "a grater" should {
    "make an avro schema for a basic case class" in {
      val schema = grater[Edward].asAvroSchema
      schema must containField("a" -> Schema.Type.STRING)
    }
  }

  def containField(pair: Pair[String, Schema.Type]): Matcher[Schema] = ((_: Schema).getFields.map(f => (f.name, f.schema.getType)).contains(pair),
                                                                       (schema: Schema) => "Schema\n\t%s\n\tdoesn't have a field %s".format(schema, pair))
}
