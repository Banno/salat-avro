package com.banno.salat.avro

import java.lang.reflect.Modifier
import com.novus.salat.{ Context, Grater, CaseClass }

trait AvroContext extends Context {
  override protected def generate(clazz: String): Grater[_ <: CaseClass] = {
    new AvroGrater[CaseClass](getCaseClass(clazz)(this).map(_.asInstanceOf[Class[CaseClass]]).get)(this)
  }

  override protected def generate_?(c: String): Option[Grater[_ <: CaseClass]] = {
    if (suitable_?(c)) {
      val cc = getCaseClass(c)(this)
      cc match {
        case Some(clazz) if (clazz.isInterface) => {
          None
        }
        case Some(clazz) if Modifier.isAbstract(clazz.getModifiers()) => {
          None
        }
        case Some(clazz) => {
          Some(new AvroGrater[CaseClass](clazz)(this))
        }
        case unknown => {
          None
        }
      }
    } else None
  }

}
