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
import com.novus.salat.IsMap
  import com.novus.salat.IsMap

import com.novus.salat._
import transformers._
import scala.collection.JavaConversions._
import org.apache.avro.Schema
import Schema.{ Field => SField }
import scala.tools.scalap.scalax.rules.scalasig.{ SingleType, Type, TypeRefType }

object AvroSalatSchema {
  
  def schemeFor[X <: CaseClass](clazz: Class[X], grater: SingleAvroGrater[X])(implicit ctx: Context): Schema = {
    val schema = Schema.createRecord(clazz.getName, "", "", false)
    schema.setFields(schemaFields(grater))
    schema
  }
  
  private def schemaFields(grater: SingleAvroGrater[_])(implicit ctx: Context): Seq[SField] = {
    grater._indexedFields.map { field =>
      new SField(field.name, schemaTypeFor(field.typeRefType), null, null)
    }
  }

  private def schemaTypeFor(typeRefType: Type)(implicit ctx: Context): Schema = {
    val typeRef @ TypeRefType(_, symbol, typeArgs) = typeRefType
    // println("typeRef = %s".format(typeRef))
    // println("symbol = %s".format(symbol))
    // println("symbol.path = %s".format(symbol.path))
    // println("typeArgs = %s".format(typeArgs))
    // println("in context: " + ctx.lookup(symbol.path))
    (symbol.path, typeRef, ctx.lookup(symbol.path)) match {
      case ("scala.Predef.String", _, _) => Schema.create(Schema.Type.STRING)
      case ("scala.Boolean", _, _) => Schema.create(Schema.Type.BOOLEAN)
      case (path, _, _) if isInt(path) => Schema.create(Schema.Type.INT)
      case (path, _, _) if isLong(path) => Schema.create(Schema.Type.LONG)
      case (path, _, _) if isDouble(path) => Schema.create(Schema.Type.DOUBLE) //is it ok to override Double & BigDecimal like this?
      case (path, _, _) if isBigDecimal(path) => Schema.create(Schema.Type.DOUBLE)
      case (path, _, _) if isJodaDateTime(path) => Schema.create(Schema.Type.STRING)
      case ("scala.Option", _, _) => optional(schemaTypeFor(typeArgs(0)))
      case (_, IsSeq(_), _) => Schema.createArray(schemaTypeFor(typeArgs(0)))
      case (_, IsMap(k, v), _) => Schema.createMap(schemaTypeFor(v))
      case (_, IsEnum(prefix), _) => enumSchema(prefix)
      case (_, _, Some(recordGrater)) => recordGrater.asInstanceOf[SingleAvroGrater[_]].asSingleAvroSchema
      case (path, _, _) => throw new UnknownTypeForAvroSchema(path)
    }
  }

  def isLong(path: String) = path match {
    case "java.lang.Long" => true
    case "scala.Long" => true
    case _ => false
  }

  def isDouble(path: String) = path match {
    case "java.lang.Double" => true
    case "scala.Double" => true
    case _ => false
  }

  private def enumSchema(prefix: SingleType): Schema = {
    // TODO: actually create an enum schema instead of a string schema
    val SingleType(_, enum) = prefix
    Schema.create(Schema.Type.STRING)
  }
  
  private def optional(schema: Schema) = Schema.createUnion(schema :: Schema.create(Schema.Type.NULL) :: Nil)

}

class UnknownTypeForAvroSchema(symbolPath: String) extends Exception("Unknown Type for Avro Serialization: " + symbolPath)
