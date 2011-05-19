package com.banno.salat.avro.test
import com.banno.salat.avro.MultiAvroGrater

import com.banno.salat.avro._
import global._

import org.specs2.mutable._
import com.novus.salat.CaseClass
import java.io.ByteArrayOutputStream
import scala.collection.JavaConversions._
import org.specs2.mutable._
import org.specs2.matcher.Matcher
import org.apache.avro.Schema
import org.apache.avro.io.{ DatumReader, DatumWriter, DecoderFactory, EncoderFactory }

trait SalatAvroSpec extends Specification {
  import scala.collection.JavaConversions._

  
  def serializeToJSON[X <: CaseClass : Manifest](x: X, maybeGrater: Option[AvroGrater[X]] = None): String = {
    val g = maybeGrater.getOrElse(grater[X])
    val baos = new ByteArrayOutputStream
    val encoder = EncoderFactory.get.jsonEncoder(g.asAvroSchema, baos)
    g.serialize(x, encoder)
    new String(baos.toByteArray())
  }

  def serializeAndDeserialize[X <: CaseClass : Manifest](old: X, maybeGrater: Option[AvroGrater[X]] = None): X = {
    val g = maybeGrater.getOrElse(grater[X])
    val baos = byteArrayOuputStream()
    val encoder = binaryEncoder(baos)
    g.serialize(old, encoder)
    
    val decoder = binaryDecoder(baos.toByteArray)
    g.asObject(decoder)
  }

  def byteArrayOuputStream(): ByteArrayOutputStream = new ByteArrayOutputStream
  def binaryEncoder(byteArrayOS: ByteArrayOutputStream) = EncoderFactory.get().binaryEncoder(byteArrayOS, null)
  def binaryDecoder(bytes: Array[Byte]) = DecoderFactory.get().binaryDecoder(bytes, null)

  def containField(name: String, schemaType: Schema.Type): Matcher[Schema] =
    ((_: Schema).getFields.map(f => (f.name, f.schema.getType)).contains(Pair(name, schemaType)),
     (schema: Schema) => "Schema\n\t%s\n\tdoesn't have a field named '%s' of type '%s'".format(schema, name, schemaType))

  def containField(name: String, schemaTypes: List[Schema.Type]): Matcher[Schema] =
    ((_: Schema).getFields.filter(_.schema.getType eq Schema.Type.UNION).map(f => (f.name, f.schema.getTypes.map(_.getType).toList)).contains(Pair(name, schemaTypes)),
      (schema: Schema) => "Schema\n\t%s\n\tdoesn't have a field named '%s' of union types '%s'".format(schema, name, schemaTypes))
  
}
