package com.banno.salat.avro.test.models

import scala.math.{BigDecimal => ScalaBigDecimal}

case class Edward(a:          String,           b:        Int,           c:        ScalaBigDecimal,
                  aa:  Option[String] = None,   bb: Option[Int] = None,  cc: Option[ScalaBigDecimal] = None,
                  aaa:  Option[String] = None,  bbb: Option[Int] = None, ccc: Option[ScalaBigDecimal] = None)
