name := "salat-avro"

version := "0.0.10-4-SNAPSHOT"

organization := "com.banno.salat.avro"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "2.3.4" % "test",
  "com.novus" %% "salat" % "1.9.9",
  "org.apache.avro" % "avro" % "1.7.7"
)

