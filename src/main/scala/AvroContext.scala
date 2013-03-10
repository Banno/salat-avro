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

import java.lang.reflect.Modifier
import com.novus.salat.{ Context, Grater, CaseClass }
import java.util.Comparator
import java.util.concurrent.ConcurrentSkipListMap
import scala.collection.mutable.SynchronizedQueue
import scala.collection.JavaConversions.JConcurrentMapWrapper

trait AvroContext extends Context {

  // since salat's graters is hidden from me, keeping my own collection
  private[avro] val avroGraters = JConcurrentMapWrapper(new ConcurrentSkipListMap[Class[_ <: AnyRef], Grater[_ <: AnyRef]](ClassComparator))
  
  override protected def generate(clazz: String): Grater[_ <: CaseClass] = {
    new SingleAvroGrater[CaseClass](getCaseClass(clazz)(this).map(_.asInstanceOf[Class[CaseClass]]).get)(this)
  }

  override def accept(grater: Grater[_ <: AnyRef]) = {
    super.accept(grater)
    avroGraters += (grater.clazz -> grater)
  }

  override protected def generate_?(c: String): Option[Grater[_ <: CaseClass]] = {
    if (suitable_?(c)) {
      val cc = getCaseClass(c)(this)
      cc match {
        case Some(clazz) if (clazz.isInterface) => {
          Some((new ProxyAvroGrater(clazz)(this)).asInstanceOf[AvroGrater[_ <: AnyRef]])
          None
        }
        case Some(clazz) if Modifier.isAbstract(clazz.getModifiers()) => {
          println("Got into isAbstract")
          None
        }
        case Some(clazz) => {
          Some(new SingleAvroGrater[CaseClass](clazz)(this))
        }
        case unknown => {
          None
        }
      }
    } else None
  }

}

object ClassComparator extends Comparator[Class[_]] {
  def compare(c1: Class[_], c2: Class[_]) = c1.getName.compare(c2.getName)
}