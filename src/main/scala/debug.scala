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

package debug
import models._
import com.banno.salat.avro._
import com.banno.salat.avro.global._
import java.io.File
//import com.novus.salat.annotations.util._
//import reflect.ScalaSignature
//import reflect.generic.ByteCodecs
//import scala.tools.scalap.scalax.rules.scalasig._
import org.objectweb.asm._
import org.apache.avro._
import org.apache.avro.io._
import org.apache.avro.file._
import org.apache.avro.generic._

import scala.util.parsing.json._

object Main extends App {
// val myRecord1 = MyRecord("Hi", 2, true)
 // val myRecord1 = MyRecord(rec(), 2, true)
  //val myRecord1 = MyRecord(List("pantagruel", "gargantua"), List(1,"hi"), List(List(List("1innermost", "1nest"), List("2innermost", "2nest")), List(List("1mid", "1mnest"), List("2mid", "2mnest"))))
  //val myRecord2 = MyRecord(List())
  //val myRecord3 = MyRecord("Escher")

//  val myRecordIterator = Iterator[MyRecord](myRecord1)//, myRecord2, myRecord3)
  //lazy val myRecordIterator = Iterator[models](myRecord1, myRecord2, myRecord3)
/*
In this example, we'll need to show that both pre-serialized and deserialized records are equal.  But normally the iterator is consumed upon serialation, leaving us nothing to compare to the deserialized result.  To solve this, we'll make a copy of the iterator, serializing myRecordIterator1 and leaving myRecordIterator2 for use in comparison.  A `Stream` can always be had from an `Iterator` if immutability is desired, at the possible risk of memory issues for large files(?).
*/

 // val(myRecordIterator1, myRecordIterator2) = myRecordIterator.duplicate

/*-------------STREAM RECORDS TO AND FROM AVRO DATAFILE------------------------------------------
In order to stream records to an avro file that can be read by an avro datafilereader, we need to provide record model, a destination file, and a stream of records. `serializeIteratorToDataFile` appends indiscriminately.  If there is no file, a file is created.  To deserialize from file we will need to provide a record model and an infile. 
*/


//Serialize to an Avro DataFile: Record model MyRecord is defined in package models
 // val outfile = new File("input.avro")
 // grater[MyRecord].serializeCollectionToFile(outfile, myRecordIterator1)



//Deserialize from File: Read DataFile, dynamically create the model class and deserialize back to object 
  val infile = new File("input.avro")


println(grater[MyRecord])
  val sameRecordIterator = grater[MyRecord].asObjectsFromFile(infile)
 


  //val sameRecordIterator = grater[MyRecord].asObjectsFromFile(infile)


  //val sameRecordIterator = com.banno.salat.avro.grater[MyRecord].asObjectsFromFile(streamInfile)
   // println(sameRecordIterator)
    sameRecordIterator foreach println

//Verify Records are Equal
 // println("All Records From Avro Data File Same As The Originals?: " + (sameRecordIterator sameElements myRecordIterator2))

 

}
