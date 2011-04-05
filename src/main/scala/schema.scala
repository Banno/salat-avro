package com.banno.salat.avro

import com.novus.salat._
import transformers._
import scala.collection.JavaConversions._
import org.apache.avro.Schema
import Schema.{ Field => SField }
import scala.tools.scalap.scalax.rules.scalasig.{ Type, TypeRefType }

object AvroSalatSchema {
  
  def schemeFor[X <: CaseClass](clazz: Class[X], grater: AvroGrater[X])(implicit ctx: Context): Schema = {
    val schema = Schema.createRecord(clazz.getName, "", "", false)
    schema.setFields(schemaFields(grater))
    schema
  }
  
  private def schemaFields(grater: AvroGrater[_])(implicit ctx: Context): Seq[SField] = {
    grater._indexedFields.map { field =>
      new SField(field.name, schemaTypeFor(field.typeRefType), null, null)
    }
  }

  private def schemaTypeFor(typeRefType: Type)(implicit ctx: Context): Schema = {
    val TypeRefType(_, symbol, typeArgs) = typeRefType
    // println("typeName = %s".format(symbol.name))
    // println("typeRefType.typeArgs = %s".format(typeArgs))
    // println("in context: " + ctx.lookup(symbol.path))
    (symbol.path, ctx.lookup(symbol.path)) match {
      case ("scala.Predef.String", _) => Schema.create(Schema.Type.STRING)
      case (path, _) if isInt(path) => Schema.create(Schema.Type.INT)
      case (path, _) if isBigDecimal(path) => Schema.create(Schema.Type.DOUBLE)
      case ("scala.Option", _) => optional(schemaTypeFor(typeArgs(0)))
      case (_, Some(recordGrater)) => recordGrater.asInstanceOf[AvroGrater[_]].asAvroSchema
      case (path, _) => throw new UnknownTypeForAvroSchema(path)
    }
  }
  
  private def optional(schema: Schema) = Schema.createUnion(schema :: Schema.create(Schema.Type.NULL) :: Nil)

}

class UnknownTypeForAvroSchema(symbolPath: String) extends Exception("Unknown Type for Avro Serialization: " + symbolPath)
