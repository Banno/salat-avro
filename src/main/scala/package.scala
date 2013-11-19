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
println("avro made a package object avro")
  //def grater[X <: CaseClass](implicit ctx: Context, m: Manifest[X]): AvroGrater[X] = {println("package object avro grater "); ctx.lookup_![X](m).asInstanceOf[AvroGrater[X]]}
def grater[X <: CaseClass](implicit ctx: Context, m: Manifest[X]): AvroGrater[X] = {println("package object avro grater "); ctx.lookup[X](m).asInstanceOf[AvroGrater[X]]}

 


  protected[avro] def getClassNamed_!(c: String)(implicit ctx: Context): Class[_] = { println("avro package getClassNamed_!"); getClassNamed(c)(ctx).getOrElse {
    throw new Error("getClassNamed: path='%s' does not resolve in any of %d classloaders registered with context='%s'".
      format(c, ctx.classLoaders.size, ctx.name))
  }}

  protected[avro] def getClassNamed(c: String)(implicit ctx: Context): Option[Class[_]] = {println("avro package getClassNamed")
    resolveClass(c, ctx.classLoaders)
  }

   protected[avro] def getCaseClass(c: String)(implicit ctx: Context): Option[Class[CaseClass]] = {
println("avro package getClassNamed")
    getClassNamed(c).filter(_.getInterfaces.contains(classOf[Product])).
      map(_.asInstanceOf[Class[CaseClass]])}




/*

  protected[avro] def getCaseClass(c: String)(implicit ctx: Context): Option[Class[CaseClass]] = {
println("getCaseClass");
    getClassNamed(c).map(_.asInstanceOf[Class[CaseClass]])}

  protected[avro] def getClassNamed(c: String)(implicit ctx: Context): Option[Class[_]] = {
println("getClassNamed")
    try {
      var clazz: Class[_] = null
      val iter = ctx.classLoaders.iterator
//while (iter.hasNext) {
     // val iter = Vector(this.getClass.getClassLoader).iterator
      //while (clazz == null && iter.hasNext) {
        try {
          clazz = Class.forName(c, true, iter.next)
println("avro get Class Named clazz:  " + clazz)
        }
        catch {
          case e: ClassNotFoundException =>
        }
      //}
//}
      if (clazz != null) Some(clazz) else None
    }
    catch {
      case _ => println("avro package none"); None
    }
  }
*/



   def resClass_!(c: String, classLoaders: Seq[ClassLoader]): Class[_] = {println("salat Util resolveClass_!"); resolveClass(c, classLoaders).getOrElse {
    throw new Error("resolveClass: path='%s' does not resolve in any of %d available classloaders".format(c, classLoaders.size))
  }}

    def toUsableClazzName(clazz: String) = if (clazz.endsWith("$")) clazz.substring(0, clazz.size - 1) else clazz

    def resolveClass[X <: AnyRef](c: String, classLoaders: Seq[ClassLoader]): Option[Class[X]] = { println("avro resolve " + c)
    //    log.info("resolveClass(): looking for %s in %d classloaders", c, classLoaders.size)
    try {
println("avro package resolve trying")
      var clazz: Class[_] = null
            var count = 0
      val iter = classLoaders.iterator

      while (clazz == null && iter.hasNext) {
        try {

 println("avro " + c );

println("avro clazz " + count + " " + clazz)
          clazz = Class.forName(c, true, iter.next())
println("avro clazz " + count + " " + clazz)
        }
        catch {
          case e: ClassNotFoundException => // keep going, maybe it's in the next one
        }

        //        log.info("resolveClass: %s %s in classloader '%s' %d of %d", c, (if (clazz != null) "FOUND" else "NOT FOUND"), ctx.name.getOrElse("N/A"), count, ctx.classloaders.size)
                count += 1
      }

      if (clazz != null) {println("class ain't null: " + clazz.asInstanceOf[Class[X]]); Some(clazz.asInstanceOf[Class[X]])} else None
    }
    catch {
      case _ => None
    }
  }


}
