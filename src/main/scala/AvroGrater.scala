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
import org.apache.avro.file._
import org.apache.avro.generic._
import java.io.File
import scala.collection.mutable.ListBuffer
import collection.JavaConverters.{asScalaIteratorConverter => _,_}

trait AvroGrater[X <: AnyRef] {
  implicit val ctx: Context
  def asAvroSchema: Schema
  private[avro] def asSingleAvroSchema(knownSchemas: ListBuffer[Schema]): Schema
  def +(other: AvroGrater[_]): MultiAvroGrater
  def supports[X](x: X)(implicit manifest: Manifest[X]): Boolean

/*--------METHODS FOR IN-MEMORY SERIALIZATION/DESERIALIZATION-----------------------------------------
Serialize to an in-memory stream with 'serialize', deserialize from in in-memory stream to an object with 'asObject'.
*/

//Writing to memory
  def serialize(x: X, encoder: Encoder): Encoder = try {
    asDatumWriter.write(x, encoder)
    encoder.flush
    encoder
  } catch {
    case e: Throwable => throw new AvroSerializationException(this, x, e)
  }

//Reading from memory
  def asObject(decoder: Decoder): X = asDatumReader.read(decoder)

  lazy val asDatumWriter: DatumWriter[X] = new AvroGenericDatumWriter[X](asAvroSchema)
  lazy val asDatumReader: AvroDatumReader[X] = asGenericDatumReader
  lazy val asGenericDatumReader: AvroGenericDatumReader[X] = new AvroGenericDatumReader[X](asAvroSchema)
  lazy val asDataFileWriter: DataFileWriter[X] = new DataFileWriter[X](asDatumWriter) 

/*-----------METHODS FOR SINGLE-RECORD SERIALIZATION/DESERIALIZATION TO/FROM AVRO DATAFILE-----------
Serialize to an Avro file with serializeToDataFile, needs to be passed a file, datum, and DataFileWriter, deserialize from file with asObjectFromDataFile.  `serializeToDataFile` appends to the file indiscriminately. If there is no file, the file is created.
*/

//Writing to File
  def serializeToDataFile(outfile: File, x: X): DataFileWriter[X] = try {

    if (!outfile.exists()) asDataFileWriter.create(asAvroSchema, outfile) //check for preexisting file
    else asDataFileWriter.appendTo(outfile)

    asDataFileWriter.append(x)
    asDataFileWriter.close
    asDataFileWriter
  } catch {
    case e: Throwable => throw new AvroSerializationException(this, x, e)
  }

//Reading from File 
 
  def asObjectFromDataFile(infile: File): X = {
    val asDataFileReader: DataFileReader[X] = new DataFileReader[X](infile, asDatumReader)
    val genericRecord: GenericData.Record = asDataFileReader.next().asInstanceOf[GenericData.Record]
    val recordCast = asGenericDatumReader.applyValues(genericRecord)
    val record = recordCast.asInstanceOf[X]
    record
  }

/*-----------METHODS FOR SERIALIZATION/DESERIALIZATION OF RECORD STREAMS TO/FROM AVRO DATAFILE-----------
Serialize an `Iterator` of Avro records to an Avro Datafile with `serializeIteratorToDataFile` (a `Stream` can always be had from an iterator if one needs immutability at the risk of memory issues). `serializeIteratorToDataFile` appends to the file indiscriminately. If there is no file, the file is created.  Deserialization with asObjectIteratorFromDataFile, which converts the Java to a Scala `Iterator`
*/

//Writing to File  
  def serializeIteratorToDataFile(outfile: File, x: Iterator[X]): DataFileWriter[X] = try {

    if (!outfile.exists()) asDataFileWriter.create(asAvroSchema, outfile) //check for pre-existing file
    else asDataFileWriter.appendTo(outfile)

    x.foreach(i => asDataFileWriter.append(i))
    asDataFileWriter.close
    asDataFileWriter
  } catch {
    case e: Throwable => throw new AvroSerializationException(this, x, e)
  }

//Reading from File 
  def asObjectIteratorFromDataFile(infile: File): Iterator[X] = {
    val asDataFileReader: DataFileReader[X] = new DataFileReader[X](infile, asDatumReader)
    val objIterator = asDataFileReader.asScala
                                      .iterator
                                      .map(i => asGenericDatumReader.applyValues(i.asInstanceOf[GenericData.Record]).asInstanceOf[X])

    objIterator
  }

 
}
