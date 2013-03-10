/*
 * Copyright 2011-2013 T8 Webware
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
import scala.collection.mutable.ListBuffer

trait AvroGrater[X <: AnyRef] {
  implicit val ctx: Context
  def asAvroSchema: Schema
  private[avro] def asSingleAvroSchema(knownSchemas: ListBuffer[Schema]): Schema
  def +(other: AvroGrater[_]): MultiAvroGrater
  def supports[X](x: X)(implicit manifest: Manifest[X]): Boolean

  def serialize(x: X, encoder: Encoder): Encoder = try {
    asDatumWriter.write(x, encoder)
    encoder.flush
    encoder
  } catch {
    case e: Throwable => throw new AvroSerializationException(this, x, e)
  }

  def asObject(decoder: Decoder): X = asDatumReader.read(decoder)

  lazy val asDatumWriter: DatumWriter[X] = new AvroGenericDatumWriter[X](asAvroSchema)
  lazy val asDatumReader: AvroDatumReader[X] = asGenericDatumReader
  lazy val asGenericDatumReader: AvroGenericDatumReader[X] = new AvroGenericDatumReader[X](asAvroSchema)

}