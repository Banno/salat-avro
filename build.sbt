import com.banno.license.Plugin.LicenseKeys._
import com.banno.license.Licenses._

licenseSettings

license := apache2("Copyright 2011-2013 T8 Webware")

removeExistingHeaderBlock := true

name := "salat-avro"

version := "0.0.8-1-SNAPSHOT"

organization := "com.banno.salat.avro"

scalaVersion := "2.9.1"
//scalaVersion := "2.10.0"

resolvers += Resolver.file("Local Ivy Repository", file("/home/julianpeeters/.ivy2/local/"))(Resolver.ivyStylePatterns)

resolvers ++= Seq(
  "repo.scalatools rels" at "https://oss.sonatype.org/content/groups/scala-tools/",
  "repo.scalatools snaps" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "mvnRepository" at "mvnrepository.com/artifact/",
  "repo.novus rels" at "http://repo.novus.com/releases/",
  "repo.novus snaps" at "http://repo.novus.com/snapshots/"
)

credentials += Credentials(Path.userHome / ".ivy2" / ".banno_credentials")

publishMavenStyle := true

publishTo := Some("Banno Repo" at (if (version.toString.endsWith("SNAPSHOT")) "http://nexus.banno.com/nexus/content/repositories/snapshots" else "http://nexus.banno.com/nexus/content/repositories/releases"))

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.12" % "test",
 // "org.specs2" %% "specs2" % "1.13" % "test",
//  "com.novus" % "salat-core_2.9.0-1" % "0.0.8-SNAPSHOT",
//  "com.novus" % "salat-core_2.9.1" % "0.0.8",
 // "com.novus" % "salat-core_2.9.1" % "1.9.2",//was using this one before mysalat
  // "com.novus" %% "salat" % "1.9.2",
//  "com.julianpeeters" % "my-salat_2.9.1" % "0.0.1.4-SNAPSHOT",
  "com.julianpeeters" % "my-salat_2.9.1" % "0.0.1.3-SNAPSHOT",
"org.json4s" %% "json4s-native" % "3.2.5",
"org.scalaj" %% "scalaj-time" % "0.7",
  "org.apache.avro" % "avro" % "1.7.4"
)
