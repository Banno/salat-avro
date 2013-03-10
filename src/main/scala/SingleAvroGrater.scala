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
import scala.collection.JavaConversions._
import scala.collection.mutable.LinkedHashSet
import scala.collection.mutable.ListBuffer
import scala.tools.scalap.scalax.rules.scalasig.MethodSymbol

class SingleAvroGrater[X <: CaseClass](clazz: Class[X])(implicit ctx: Context)
  extends ConcreteGrater[X](clazz) with AvroGrater[X] {
    
  lazy val asAvroSchema: Schema = Schema.createUnion(asSingleAvroSchema(new ListBuffer[Schema]) :: Nil)
  def asSingleAvroSchema(knownSchemas: ListBuffer[Schema]): Schema = AvroSalatSchema.schemaFor(clazz, this, knownSchemas)
  def supports[X](x: X)(implicit manifest: Manifest[X]): Boolean = manifest.erasure == clazz

  def +(other: AvroGrater[_]): MultiAvroGrater = other match {
    case sg: SingleAvroGrater[_] => new MultiAvroGrater(LinkedHashSet(this, sg))
    case mg: MultiAvroGrater => new MultiAvroGrater(mg.graters + this)
  }

  // expose some nice methods for Datum Writers/Readers
  
  // TODO: for some reason, Grater.indexedFields is no protected to just salat (just copied it for now)
  private[avro] lazy val _indexedFields = {
    // don't use allTheChildren here!  this is the indexed fields for clazz and clazz alone
    sym.children
      .filter(c => c.isCaseAccessor && !c.isPrivate)
      .map(_.asInstanceOf[MethodSymbol])
      .zipWithIndex
      .map {
      case (ms, idx) => {
        //        log.info("indexedFields: clazz=%s, ms=%s, idx=%s", clazz, ms, idx)
        Field(idx, keyOverridesFromAbove.get(ms).getOrElse(ms.name), typeRefType(ms), clazz.getMethod(ms.name))
      }

    }
  }
    
  private[avro] lazy val _constructor = constructor
  protected[avro] override def safeDefault(field: Field) = super.safeDefault(field)
}