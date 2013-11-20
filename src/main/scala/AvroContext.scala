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
package com.banno.salat.avro

import util._
import com.novus.salat._
import java.lang.reflect.Modifier
//import com.novus.salat.{ Context, Grater, ProxyGrater, ConcreteGrater, IsCaseClass, CaseClass }
//import com.novus.salat.{ Context, Grater, ProxyGrater, ConcreteGrater, CaseClass }

import java.util.Comparator
import java.util.concurrent.ConcurrentSkipListMap
import scala.collection.mutable.SynchronizedQueue
import scala.collection.JavaConversions.JConcurrentMapWrapper
import scala.collection.mutable.{ ConcurrentMap }
import java.util.concurrent.{ CopyOnWriteArrayList, ConcurrentHashMap }

trait AvroContext extends Context {

  // since salat's graters is hidden from me, keeping my own collection
private[avro] val avroGraters: ConcurrentMap[String, Grater[_ <: AnyRef]] = JConcurrentMapWrapper(new ConcurrentHashMap[String, Grater[_ <: AnyRef]]())

  override def accept(grater: Grater[_ <: AnyRef]) = {println("avro accept")
    super.accept(grater)
    avroGraters += (grater.clazz.getName.toString -> grater)
  }

  var clsLoaders: Vector[ClassLoader] = Vector(this.getClass.getClassLoader)

  def lookp(c: String): Option[Grater[_ <: AnyRef]] = {println("avrocontext lookup(String) " + c); avroGraters.get(c) }

  override def lookup_?[X <: AnyRef](c: String): Option[Grater[_ <: AnyRef]] =  {//Option[Grater[_ <: AnyRef]](avroGraters.get(c))/* orElse { 
avroGraters.foreach(println)
      if (suitable_?(c)) {println("avro context, it was suitable " + c)
        resolveClass(c, clsLoaders) match {
        case Some(clazz) if needsProxyGrater(clazz) => {
          log.trace("lookup_?: creating proxy grater for clazz='%s'", clazz.getName)
          Some((new ProxyAvroGrater(clazz.asInstanceOf[Class[X]])(this) {}).asInstanceOf[Grater[_ <: AnyRef]])
       }
      case Some(clazz) if isCaseClass(clazz) => {
          Some((new SingleAvroGrater[CaseClass](clazz.asInstanceOf[Class[CaseClass]])(this) {}).asInstanceOf[Grater[_ <: AnyRef]])
        }
      case _ => None
     }  
     }
  else {println("wasn't suitable"); None}
  }
}

object ClassComparator extends Comparator[Class[_]] {
println("made a classComparator")
  def compare(c1: Class[_], c2: Class[_]) = c1.getName.compare(c2.getName)
}
