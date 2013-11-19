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
 // private[avro] val avroGraters = JConcurrentMapWrapper(new ConcurrentSkipListMap[Class[_ <: AnyRef], Grater[_ <: AnyRef]](ClassComparator))

private[avro] val avroGraters: ConcurrentMap[String, Grater[_ <: AnyRef]] = JConcurrentMapWrapper(new ConcurrentHashMap[String, Grater[_ <: AnyRef]]())

 //private[avro] val avroGraters = JConcurrentMapWrapper(new ConcurrentSkipListMap[String, Grater[_ <: AnyRef]](ClassComparator))  

println("avro's graters: ")
avroGraters.foreach(println)

 var clsLoaders: Vector[ClassLoader] = Vector(this.getClass.getClassLoader)

    protected def generate(clazz: String): Grater[_ <: CaseClass] = {
 //  protected def generate(clazz: String): Grater[_ <: CaseClass] = {
println("avro generate")
    val caseClass = getCaseClass(clazz)(this).map(_.asInstanceOf[Class[CaseClass]]).get
println(caseClass)
    new SingleAvroGrater[CaseClass](caseClass)(this) {}//.asInstanceOf[Grater[CaseClass]]

  }

  override def accept(grater: Grater[_ <: AnyRef]) = {println("avro accept")
    super.accept(grater)
    avroGraters += (grater.clazz.getName.toString -> grater)
  }

//override def lookup[X <: AnyRef](c: String): Grater[_ <: AnyRef] = {println("lookup(String) avro calling lookup_?(c)");lookup_?(c).getOrElse(throw GraterGlitch(c)(this))}
 def lookp(c: String): Option[Grater[_ <: AnyRef]] = {println("avrocontext lookup(String) " + c); avroGraters.get(c)}//lookup_?(c).get}
// def lookp(c: String): Option[Grater[_ <: AnyRef]] = {println("avrocontext lookup(String) " + c); avroGraters.get(c)}//lookup_?(c).get}
 override def lookup_!(c: String): Grater[_ <: AnyRef] = {println("avrocontext lookup(String) " + c); avroGraters.get(c).get}//Option(lookup_?("None").get)}


//  override def lookup_?[X <: AnyRef](c: String): Option[Grater[_ <: AnyRef]] = Option[Grater[_ <: AnyRef]](avroGraters.get(c)) orElse {
   override def lookup_?[X <: AnyRef](c: String): Option[Grater[_ <: AnyRef]] =  {//Option[Grater[_ <: AnyRef]](avroGraters.get(c))/* orElse { 
avroGraters.foreach(println)
      if (suitable_?(c)) {println("avro context, it was suitable " + c)
    //  resolveClass(c, Vector(this.getClass.getClassLoader)) match {
        resolveClass(c, clsLoaders) match {
  //    resClass(c, classLoaders).map(n:Class => Some(new SingleAvroGrater[CaseClass](n.get)(this)))//.map(clazz: Class[_ <: CaseClass] => new SingleAvroGrater[CaseClass](clazz)(this))
  //  println( "resolve: "  + resolveClass(c, classLoaders) )
 
       // case IsCaseClass(clazz) => println("IsCaseClass = true"); 
//println("avro lookup_? making a SingleAvroGrater for: " + c + " " + getCaseClass(c)(this))
 // val caseClass = getCaseClass(c)(this).map(_.asInstanceOf[Class[CaseClass]]).get
  //val caseClass = IsCaseClass//(grater.clazz)//(this)
  //  println("case class: " + caseClass)


//var avroGrater: Grater[CaseClass] = null
//if (caseClass.isDefined){

  // val avroGrater = new SingleAvroGrater[CaseClass](caseClass)(this)
//  case Some(clazz) if isCaseClass(clazz)  => {println("avrocontext matched iscaseclass " + clazz); Some(new SingleAvroGrater[CaseClass](clazz)(this))}
        case Some(clazz) if needsProxyGrater(clazz) => {
          log.trace("lookup_?: creating proxy grater for clazz='%s'", clazz.getName)
          Some((new ProxyAvroGrater(clazz.asInstanceOf[Class[X]])(this) {}).asInstanceOf[Grater[_ <: AnyRef]])
       }

      case Some(clazz) if isCaseClass(clazz) => {
          Some((new SingleAvroGrater[CaseClass](clazz.asInstanceOf[Class[CaseClass]])(this) {}).asInstanceOf[Grater[_ <: AnyRef]])
        }
//   val avroGrater = Some(new SingleAvroGrater[CaseClass](resolveClass(c, classLoaders).get )(this))
//         case Some(clazz) if true => Some(new SingleAvroGrater[CaseClass](resClass(c, classLoaders).get )(this))
       //  case Some(clazz) if true => Some(new SingleAvroGrater[CaseClass](clazz.get)(this))
 //  println("avroGrater: " + avroGrater)
         case _ => None
     }  
//Option[Grater[_ <: AnyRef]](avroGrater)
     }
  else {println("wasn't suitable"); None}
//}

//  }


//.map(_.asInstanceOf[Class[CaseClass]]).get)(this) {})
       // case Some(clazz) if Modifier.isAbstract(clazz.getModifiers) => Some((
       //   new ProxyGrater(clazz.asInstanceOf[Class[X]])(this) {}).
       //   asInstanceOf[Grater[_ <: AnyRef]])
      //  case Some(clazz) if clazz.isInterface => Some((
      //    new ProxyGrater(clazz.asInstanceOf[Class[X]])(this) {}).asInstanceOf[Grater[_ <: AnyRef]])
     //   case _ => None
     // }
  // }
  //  else None).asInstanceOf[ Option[Grater[_ <: AnyRef]]]
  }



 // def resolveClass_!(c: String, classLoaders: Seq[ClassLoader]): Class[_] = resolveClass(c, classLoaders).getOrElse {
 //   throw new Error("resolveClass: path='%s' does not resolve in any of %d available classloaders".format(c, classLoaders.size))
  //}

 // protected[salat] def toUsableClassName(clazz: String) = if (clazz.endsWith("$")) clazz.substring(0, clazz.size - 1) else clazz
/*
   def resolveClass[X <: AnyRef](c: String, classLoaders: Seq[ClassLoader]): Option[Class[X]] = {
    // log.info("resolveClass(): looking for %s in %d classloaders", c, classLoaders.size)
    try {
      var clazz: Class[_] = null
      // var count = 0
      val iter = classLoaders.iterator
      while (clazz == null && iter.hasNext) {
        try {

println(c)
          clazz = Class.forName(c, true, iter.next())
        }
        catch {
          case e: ClassNotFoundException => // keep going, maybe it's in the next one
        }

        // log.info("resolveClass: %s %s in classloader '%s' %d of %d", c, (if (clazz != null) "FOUND" else "NOT FOUND"), ctx.name.getOrElse("N/A"), count, ctx.classloaders.size)
        // count += 1
      }

      if (clazz != null) Some(clazz.asInstanceOf[Class[X]]) else None
    }
    catch {
      case _ => None
    }
  }
*/
/*
//  override protected def generate_?(c: String): Option[Grater[_ <: CaseClass]] = {
   protected def generate_?(c: String): Option[Grater[_ <: CaseClass]] = {
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
*/
}

object ClassComparator extends Comparator[Class[_]] {
println("made a classComparator")
  def compare(c1: Class[_], c2: Class[_]) = c1.getName.compare(c2.getName)
}
