package com.banno.salat.avro

import com.novus.salat._
import scala.collection.JavaConversions._
import org.apache.avro.Schema
import Schema.{ Field => SField }
import org.apache.avro.io.DatumWriter
import org.apache.avro.generic.{ GenericData, GenericDatumWriter }
import scala.tools.scalap.scalax.rules.scalasig.TypeRefType

class AvroGrater[X <: CaseClass](clazz: Class[X])(implicit ctx: Context)
  extends Grater[X](clazz) {

  lazy val asAvroSchema: Schema = Schema.createRecord(recordFields)

  lazy val asDatumWriter: DatumWriter[X] = new AvroGenericDatumWriter[X]

  protected lazy val recordFields: Seq[SField] = {
    indexedFields.map { field =>
      new SField(field.name, schemaTypeFor(field.typeRefType), null, null)
    }
  }

  protected def schemaTypeFor(typeRefType: TypeRefType): Schema = {
    val TypeRefType(_, typeName, _) = typeRefType
    typeName.name match {
      case "String" => Schema.create(Schema.Type.STRING)
      case "Int" => Schema.create(Schema.Type.INT)
      case _ => throw new RuntimeException("I don't know this type")
    }
  }

  protected class AvroGenericDatumWriter[X] extends GenericDatumWriter[X](asAvroSchema, avroGenericData)

  protected val avroGenericData = new GenericData {
    override def getField(record: Any, name: String, pos: Int): Object = {
      record.asInstanceOf[X].productIterator.toList.apply(pos).asInstanceOf[AnyRef]
    }
  }
}
