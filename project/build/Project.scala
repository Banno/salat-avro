import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val novusRels = "repo.novus rels" at "http://repo.novus.com/releases/"
  val novusSnaps = "repo.novus snaps" at "http://repo.novus.com/snapshots/"

  val salat = "com.novus" %% "salat-core" % "0.0.7-SNAPSHOT"
  val avro = "org.apache.avro" % "avro" % "1.5.0"

  // necessary for scalaz snapshot that specs2 depends on
  val snapshots = "snapshots" at "http://scala-tools.org/repo-snapshots"

  val specs2 = "org.specs2" %% "specs2" % "1.0.1" % "test"
  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
}
