package com.banno.salat

import com.novus.salat._

package object avro {
  def grater[X <: CaseClass](implicit ctx: Context, m: Manifest[X]): AvroGrater[X] = ctx.lookup_![X](m).asInstanceOf[AvroGrater[X]]

  protected[avro] def getCaseClass(c: String)(implicit ctx: Context): Option[Class[CaseClass]] =
    getClassNamed(c).map(_.asInstanceOf[Class[CaseClass]])

  protected[avro] def getClassNamed(c: String)(implicit ctx: Context): Option[Class[_]] = {
    try {
      var clazz: Class[_] = null
      val iter = ctx.classLoaders.iterator
      while (clazz == null && iter.hasNext) {
        try {
          clazz = Class.forName(c, true, iter.next)
        }
        catch {
          case e: ClassNotFoundException =>
        }
      }
      if (clazz != null) Some(clazz) else None
    }
    catch {
      case _ => None
    }
  }
}
