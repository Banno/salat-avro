package com.banno.salat.avro

import com.novus.salat._
import scala.collection.JavaConversions._
import org.apache.avro.Schema
import Schema.{ Field => SField }
import org.apache.avro.io.DatumWriter
import org.apache.avro.generic.{ GenericData, GenericDatumWriter }
import scala.tools.scalap.scalax.rules.scalasig.{Type,TypeRefType}

class AvroGrater[X <: CaseClass](clazz: Class[X])(implicit ctx: Context)
  extends Grater[X](clazz) {

  lazy val asAvroSchema: Schema = {
    val schema = Schema.createRecord(clazz.getName, "", "", false)
    schema.setFields(recordFields)
    schema
  }

  lazy val asDatumWriter: DatumWriter[X] = new AvroGenericDatumWriter[X]

  protected lazy val recordFields: Seq[SField] = {
    indexedFields.map { field =>
      new SField(field.name, schemaTypeFor(field.typeRefType), null, null)
    }
  }

  protected def schemaTypeFor(typeRefType: Type): Schema = {
    val TypeRefType(_, typeName, typeArgs) = typeRefType
//    println("typeName = %s".format(typeName.name))
//    println("typeRefType.typeArgs = %s".format(typeArgs))
    typeName.name match {
      case "String" => Schema.create(Schema.Type.STRING)
      case "Int" => Schema.create(Schema.Type.INT)
      case "BigDecimal" => Schema.create(Schema.Type.DOUBLE)
      case "Option" => optional(schemaTypeFor(typeArgs(0)))
      case _ => throw new RuntimeException("I don't know this type")
    }
  }

  private def optional(schema: Schema) = Schema.createUnion(schema :: Schema.create(Schema.Type.NULL) :: Nil)

  protected class AvroGenericDatumWriter[X] extends GenericDatumWriter[X](asAvroSchema, avroGenericData)

  protected val avroGenericData = new GenericData {
    override def getField(record: Any, name: String, pos: Int): Object = {
      val caseClass = record.asInstanceOf[X]
      val (value, field) = caseClass.productIterator.zip(indexedFields.iterator).toList.apply(pos)
      field.out_!(value) match {
        case Some(None) => None
        case Some(serialized) => serialized.asInstanceOf[AnyRef]
        case _ => None
      }
    }

    override def resolveUnion(union: Schema, datum: Any): Int = datum match {
      case None    => 1
      case _       => 0
    }
  }
}
