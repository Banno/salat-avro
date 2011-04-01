package com.banno.salat.avro

import com.novus.salat._
import scala.collection.JavaConversions._
import org.apache.avro.Schema
import Schema.{Field => SField}
import scala.tools.scalap.scalax.rules.scalasig.TypeRefType

class AvroGrater[X <: CaseClass](clazz: Class[X])(implicit ctx: Context)
  extends Grater[X](clazz) {

  lazy val asAvroSchema: Schema = {
    Schema.createRecord(recordFields)
  }

  lazy val recordFields: Seq[SField] = {
    indexedFields.map { field =>
      new SField(field.name, schemaTypeFor(field.typeRefType), "", null)
    }
  }

  protected def schemaTypeFor(typeRefType: TypeRefType): Schema = {
    Schema.create(Schema.Type.STRING)
  }
}
