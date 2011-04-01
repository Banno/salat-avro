package com.banno.salat.avro

import com.novus.salat.Context

package object global {
  implicit val ctx = new AvroContext { val name = Some("global") }
}
