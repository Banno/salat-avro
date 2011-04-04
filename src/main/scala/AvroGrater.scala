package com.banno.salat.avro

import com.novus.salat._
import scala.collection.JavaConversions._
import org.apache.avro.Schema
import Schema.{ Field => SField }
import org.apache.avro.io.{Decoder, DatumReader, DatumWriter}
import org.apache.avro.generic.{ GenericData, GenericDatumReader, GenericDatumWriter }
import org.apache.avro.util.Utf8
import scala.tools.scalap.scalax.rules.scalasig.{Type,TypeRefType}

trait AvroDatumReader[X] extends DatumReader[X] {
  def read(decoder: Decoder): X
}

class AvroGrater[X <: CaseClass](clazz: Class[X])(implicit ctx: Context)
  extends Grater[X](clazz) {

  lazy val asAvroSchema: Schema = {
    val schema = Schema.createRecord(clazz.getName, "", "", false)
    schema.setFields(recordFields)
    schema
  }

  lazy val asDatumWriter: DatumWriter[X] = new AvroGenericDatumWriter[X]
  lazy val asDatumReader: AvroDatumReader[X] = new AvroGenericDatumReader[X]

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

  private def optional(schema: Schema) = Schema.createUnion(schema :: Schema.create(Schema.Type.NULL) :: Nil)

  protected class AvroGenericDatumReader[X] extends GenericDatumReader[X](asAvroSchema) with AvroDatumReader[X] {
    val collectingGenericData = new CollectingGenericData          
    val colletingReader = new GenericDatumReader[Object](asAvroSchema, asAvroSchema, collectingGenericData)
              
    def read(decoder: Decoder): X = {
      colletingReader.read(null, decoder)
      val recordFields: Seq[Object] = collectingGenericData.fields
      
      val arguments = indexedFields.zip(recordFields).map {
        case (field, Some(value)) => field.in_!(value)
        case (field, _) => safeDefault(field)
      }.map(_.get.asInstanceOf[AnyRef])
      
      constructor.newInstance(arguments: _*).asInstanceOf[X]
    }
  }
    
  import scala.collection.mutable.ListBuffer
  protected class CollectingGenericData extends GenericData {
    val fields = new ListBuffer[Object]
    override def setField(record: Any, name: String, pos: Int, obj: Object) {
      // println("pos = " + pos)
      // println("obj = " + obj)
      // println("fields = " + fields)
      val scalaObj = obj match {
        case utf8: Utf8 => utf8.toString
        case x => x
      }
      fields.insert(pos, Option(scalaObj))
    }
  }
}
