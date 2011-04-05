package com.banno.salat.avro

import com.novus.salat._
import transformers._
import scala.collection.JavaConversions._
import org.apache.avro.Schema
import Schema.{ Field => SField }
import scala.tools.scalap.scalax.rules.scalasig.{ SingleType, Type, TypeRefType }

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
    val typeRef @ TypeRefType(_, symbol, typeArgs) = typeRefType
    // println("typeName = %s".format(symbol.name))
    // println("typeRefType.typeArgs = %s".format(typeArgs))
    // println("in context: " + ctx.lookup(symbol.path))
    (symbol.path, typeRef, ctx.lookup(symbol.path)) match {
      case ("scala.Predef.String", _, _) => Schema.create(Schema.Type.STRING)
      case ("scala.Boolean", _, _) => Schema.create(Schema.Type.BOOLEAN)
      case (path, _, _) if isInt(path) => Schema.create(Schema.Type.INT)
      case (path, _, _) if isBigDecimal(path) => Schema.create(Schema.Type.DOUBLE)
      case (path, _, _) if isJodaDateTime(path) => Schema.create(Schema.Type.STRING)
      case ("scala.Option", _, _) => optional(schemaTypeFor(typeArgs(0)))
      case (_, IsEnum(prefix), _) => enumSchema(prefix)
      case (_, _, Some(recordGrater)) => recordGrater.asInstanceOf[AvroGrater[_]].asAvroSchema
      case (path, _, _) => throw new UnknownTypeForAvroSchema(path)
    }
  }

  private def enumSchema(prefix: SingleType): Schema = {
    // TODO: actually create an enum schema instead of a string schema
    val SingleType(_, enum) = prefix
    Schema.create(Schema.Type.STRING)
  }
  
  private def optional(schema: Schema) = Schema.createUnion(schema :: Schema.create(Schema.Type.NULL) :: Nil)

}

class UnknownTypeForAvroSchema(symbolPath: String) extends Exception("Unknown Type for Avro Serialization: " + symbolPath)
