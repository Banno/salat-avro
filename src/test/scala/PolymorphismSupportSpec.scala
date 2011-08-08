package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._
import org.apache.avro.Schema

object PolymorphismSupportSpec extends SalatAvroSpec {
  import models._

  "a grater" should {
    "make a avro schema for something that refers to a trait" in {
      // must add the subclass grater first
      grater[SomeSubclassExtendingSaidTrait]
      grater[AnotherSubclassExtendingSaidTrait]
      
      val schema = grater[SomeContainerClass].asAvroSchema
      schema.getName must_== "union"
      val recordSchema = schema.getTypes().get(0)
      recordSchema.getName must_== "SomeContainerClass"
      recordSchema.getNamespace must_== "com.banno.salat.avro.test.models"
      recordSchema must containField("e", Schema.Type.STRING)
      recordSchema must containField("theListWhichNeedsToBeTested", Schema.Type.ARRAY)

      val subclassSchemas = recordSchema.getField("theListWhichNeedsToBeTested").schema.getElementType
      subclassSchemas.getName must_== "union"
      subclassSchemas.getTypes.get(0).getName must_== "SomeSubclassExtendingSaidTrait"
      subclassSchemas.getTypes.get(1).getName must_== "AnotherSubclassExtendingSaidTrait"
    }
  }
}
