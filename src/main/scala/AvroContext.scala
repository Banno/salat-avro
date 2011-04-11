/*
 * Copyright 2011 T8 Webware
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