package com.banno.salat.avro

import com.novus.salat._
import org.apache.avro.Schema
import org.apache.avro.io.{ Decoder, Encoder, DatumReader, DatumWriter }

class AvroGrater[X <: CaseClass](clazz: Class[X])(implicit ctx: Context)
  extends Grater[X](clazz) {

  def serialize(x: X, encoder: Encoder): Encoder = {
    asDatumWriter.write(x, encoder)
    encoder.flush
    encoder
  }

  def asObject(decoder: Decoder): X = asDatumReader.read(decoder)

  lazy val asAvroSchema: Schema = AvroSalatSchema.schemeFor(clazz, this)
  // TODO: not sure if the writer and readers should be exposed
  lazy val asDatumWriter: DatumWriter[X] = new AvroGenericDatumWriter[X](this)
  lazy val asDatumReader: AvroDatumReader[X] = new AvroGenericDatumReader[X](this)

  // expose some nice methods for Datum Writers/Readers
  private[avro] lazy val _indexedFields = indexedFields
  private[avro] lazy val _constructor = constructor
  protected[avro] override def safeDefault(field: Field) = super.safeDefault(field)
}

