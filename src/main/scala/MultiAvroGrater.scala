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
import org.apache.avro.io.DatumReader
import org.apache.avro.io.Encoder
import scala.collection.JavaConversions._

class UnsupportedCaseClassMultiException(obj: Any)
extends RuntimeException("Cannot serialize " + obj)

class MultiAvroGrater(val graters: AvroGrater[_]*) {
  def +(other: MultiAvroGrater) = new MultiAvroGrater((graters ++ other.graters): _*)
  def +(other: AvroGrater[_]) = new MultiAvroGrater((graters :+ other): _*)

  def asAvroSchema: Schema = Schema.createUnion(graters.map(_.asAvroSchema))
  
  def serialize[X <: CaseClass : Manifest](x: X, encoder: Encoder): Encoder = {
    graters.find(_.supports(x)) match {
      case Some(serializingGrater) =>
        serializingGrater.asInstanceOf[AvroGrater[X]].serialize(x, encoder)
      case _ => throw new UnsupportedCaseClassMultiException(x)
    }
    encoder
  }
}

