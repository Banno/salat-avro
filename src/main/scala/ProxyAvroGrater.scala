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
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat.Context
import com.novus.salat.Grater
import java.util.ArrayList
import org.apache.avro.Schema
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import org.json4s.JsonAST.JObject

class ProxyAvroGrater[X <: AnyRef](clazz: Class[X])(implicit ctx: AvroContext) extends Grater[X](clazz)(ctx) with AvroGrater[X] {

  def knownSubclassGraters: List[AvroGrater[_ <: AnyRef]] = ctx.avroGraters.collect {
    case (subclazz, grater) if clazz.isAssignableFrom(Class.forName(subclazz)) && clazz.getName.toString != subclazz =>  grater.asInstanceOf[AvroGrater[_ <: AnyRef]]}.toList
  
  def asAvroSchema: Schema = asSingleAvroSchema(new ListBuffer[Schema])

  private[avro] def asSingleAvroSchema(knownSchemas: ListBuffer[Schema]) = {
    //knownSubclassGraters' contained schemas get reordered here because the RecursiveTypeSpec requires a certain order: 
    val rootSchema = knownSubclassGraters.last
    val reordered = (knownSubclassGraters.dropRight(1).reverse):+rootSchema
    Schema.createUnion(  reordered.reverse.map(  _.asSingleAvroSchema(knownSchemas)  ).asJava  )}
  
  def +(other: AvroGrater[_]): MultiAvroGrater = null
  
  def supports[X](x: X)(implicit manifest: Manifest[X]): Boolean = false
  
  def asDBObject(o: X): DBObject =
    ctx.lookup(o.getClass.getName).asInstanceOf[Grater[X]].asDBObject(o)

  def asObject[A <% MongoDBObject](dbo: A): X =
    ctx.lookup(dbo).asInstanceOf[Grater[X]].asObject(dbo)

  def iterateOut[T](o: X, outputNulls: Boolean)(f: ((String, Any)) => T): Iterator[T] =
    ctx.lookup(o.getClass.getName).asInstanceOf[Grater[X]].iterateOut(o, true)(f)

  def fromJSON(j: JObject) = ctx.lookup(j).asInstanceOf[Grater[X]].fromJSON(j)

  def toJSON(o: X) = ctx.lookup(o.getClass.getName).asInstanceOf[Grater[X]].toJSON(o)

  def toMap(o: X) = ctx.lookup(o.getClass.getName).asInstanceOf[Grater[X]].toMap(o)

  def fromMap(m: Map[String, Any]) = ctx.lookup(m).asInstanceOf[Grater[X]].fromMap(m)
}
