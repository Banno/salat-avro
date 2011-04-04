package com.banno.salat.avro

import com.novus.salat._
import scala.collection.JavaConversions._
import org.apache.avro.Schema
import Schema.{ Field => SField }
import org.apache.avro.io.{Decoder, Encoder, DatumReader, DatumWriter}
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
    schema.setFields(schemaFields)
    schema
  }

  lazy val asDatumWriter: DatumWriter[X] = new AvroGenericDatumWriter[X]
  lazy val asDatumReader: AvroDatumReader[X] = new AvroGenericDatumReader[X]

  def serialize(x: X, encoder: Encoder): Encoder = {
    asDatumWriter.write(x, encoder)
    encoder.flush
    encoder
  }

  def asObject(decoder: Decoder): X = asDatumReader.read(decoder)

  protected lazy val schemaFields: Seq[SField] = {
    indexedFields.map { field =>
      new SField(field.name, schemaTypeFor(field.typeRefType), null, null)
    }
  }
    
  protected def schemaTypeFor(typeRefType: Type): Schema = {
    val TypeRefType(_, symbol, typeArgs) = typeRefType
    // println("typeName = %s".format(symbol.name))
    // println("typeRefType.typeArgs = %s".format(typeArgs))
    // println("in context: " + ctx.lookup(symbol.path))
    (symbol.name, ctx.lookup(symbol.path)) match {
      case ("String", _) => Schema.create(Schema.Type.STRING)
      case ("Int", _) => Schema.create(Schema.Type.INT)
      case ("BigDecimal", _) => Schema.create(Schema.Type.DOUBLE)
      case ("Option", _) => optional(schemaTypeFor(typeArgs(0)))
      case (_, Some(recordGrater)) => recordGrater.asInstanceOf[AvroGrater[_]].asAvroSchema
      case _ => throw new RuntimeException("I don't know this type")
    }
  }

  protected class AvroGenericDatumWriter[X] extends GenericDatumWriter[X](asAvroSchema, avroGenericData)
    
  protected val avroGenericData = new GenericData {
    override def getField(record: Any, name: String, pos: Int): Object = {
      // println("getField in grater %s \n\twith record %s\n\twith name %s \n\tat pos %s".format(AvroGrater.this, record, name, pos))
      val caseClass = record.asInstanceOf[Product]
      val grater: AvroGrater[_] = ctx.lookup(caseClass.getClass.getName).get.asInstanceOf[AvroGrater[_]]
      
      val (value, field) = caseClass.productIterator.zip(grater.indexedFields.iterator).toList.apply(pos)
      val outTransformer = Extractors.select(field.typeRefType).getOrElse(field.out)
      // println("out transformer is " + outTransformer)
      val returnedValue = outTransformer.transform_!(value) match {
        case Some(None) => None
        case Some(serialized) => serialized.asInstanceOf[AnyRef]
        case _ => None
      }
      // println("returnedValue = " + returnedValue)
      returnedValue
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
