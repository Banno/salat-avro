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