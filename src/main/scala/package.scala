/*
 * Copyright 2011-2013 T8 Webware
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.banno.salat

import com.novus.salat._
import util._
import com.banno.salat.avro.global._

case class MyRecord(x: Int)

package object avro {

  def grater[X <: CaseClass](implicit ctx: Context, m: Manifest[X]): AvroGrater[X] = ctx.lookup[X](m).asInstanceOf[AvroGrater[X]]

  def firstLine(f: java.io.File): Option[String] = {
    val src = io.Source.fromFile(f)
    try {
      src.getLines.find(_ => true)
    } finally {
      src.close()
    }
  }

  protected[avro] def getClassNamed_!(c: String)(implicit ctx: AvroContext): Class[_] = getClassNamed(c)(ctx).getOrElse {
    throw new Error("getClassNamed: path='%s' does not resolve in any of %d classloaders registered with context='%s'".
      format(c, ctx.clsLoaders.size, ctx.name))
  }

  protected[avro] def getClassNamed(c: String)(implicit ctx: AvroContext): Option[Class[_]] =
    resolveClass(c, ctx.clsLoaders)

  protected[avro] def getCaseClass(c: String)(implicit ctx: AvroContext): Option[Class[CaseClass]] = 
    getClassNamed(c).filter(_.getInterfaces.contains(classOf[Product])).
      map(_.asInstanceOf[Class[CaseClass]])

  protected[avro] def isCaseClass(clazz: Class[_]) = clazz.getInterfaces.contains(classOf[Product])

  protected[avro] def isCaseObject(clazz: Class[_]): Boolean = clazz.getInterfaces.contains(classOf[Product]) &&
    clazz.getInterfaces.contains(classOf[ScalaObject]) && clazz.getName.endsWith("$")


   def resClass_!(c: String, classLoaders: Seq[ClassLoader]): Class[_] = resolveClass(c, classLoaders).getOrElse {
    throw new Error("resolveClass: path='%s' does not resolve in any of %d available classloaders".format(c, classLoaders.size))
  }

    def toUsableClazzName(clazz: String) = if (clazz.endsWith("$")) clazz.substring(0, clazz.size - 1) else clazz

    def resolveClass[X <: AnyRef](c: String, classLoaders: Seq[ClassLoader]): Option[Class[X]] = { 
    //    log.info("resolveClass(): looking for %s in %d classloaders", c, classLoaders.size)
    try {
      var clazz: Class[_] = null
            var count = 0
      val iter = classLoaders.iterator

      while (clazz == null && iter.hasNext) {
        try {
          clazz = Class.forName(c, true, iter.next())
        }
        catch {
          case e: ClassNotFoundException => // keep going, maybe it's in the next one
        }
        //        log.info("resolveClass: %s %s in classloader '%s' %d of %d", c, (if (clazz != null) "FOUND" else "NOT FOUND"), ctx.name.getOrElse("N/A"), count, ctx.classloaders.size)
                count += 1
      }

      if (clazz != null) Some(clazz.asInstanceOf[Class[X]]) else None
    }
    catch {
      case _ : Throwable => None
    }
  }


}
