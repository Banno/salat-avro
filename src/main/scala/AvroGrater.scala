/*
 * Copyright 2011 T8 Webware
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  def supports[X](x: X)(implicit manifest: Manifest[X]): Boolean =
    manifest.erasure == clazz
  def +(other: AvroGrater[_]) = new MultiAvroGrater(this, other)

  lazy val asAvroSchema: Schema = AvroSalatSchema.schemeFor(clazz, this)
  // TODO: not sure if the writer and readers should be exposed
  lazy val asDatumWriter: DatumWriter[X] = new AvroGenericDatumWriter[X](this)
  lazy val asDatumReader: AvroDatumReader[X] = new AvroGenericDatumReader[X](this)

  // expose some nice methods for Datum Writers/Readers
  private[avro] lazy val _indexedFields = indexedFields
  private[avro] lazy val _constructor = constructor
  protected[avro] override def safeDefault(field: Field) = super.safeDefault(field)
}
