package com.banno.salat.avro.test

import com.banno.salat.avro._
import global._

object NumberSupportSpec extends SalatAvroSpec {
  import models._
  
  "a grater" should {
    "handle Long" in {
      val oldLouis = Louis(123456l)
      val newLouis: Louis = serializeAndDeserialize(oldLouis)
      newLouis must_== oldLouis
    }
    
    "handle Double" in {
      val oldDirk = Dirk(10.123d)
      val newDirk: Dirk = serializeAndDeserialize(oldDirk)
      newDirk must_== oldDirk
    }
  }
}    
