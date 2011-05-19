import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with ApacheLicense2 {
  val novusRels = "repo.novus rels" at "http://repo.novus.com/releases/"
  val novusSnaps = "repo.novus snaps" at "http://repo.novus.com/snapshots/"

  val salat = "com.novus" %% "salat-core" % "0.0.7"
  val avro = "org.apache.avro" % "avro" % "1.5.0"

  // necessary for scalaz snapshot that specs2 depends on
  val snapshots = "snapshots" at "http://scala-tools.org/repo-snapshots"

  val specs2 = "org.specs2" %% "specs2" % "1.3" % "test"
  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

  Credentials(Path.userHome / ".ivy2" / ".banno_credentials", log)
  override def managedStyle = ManagedStyle.Maven
  lazy val publishTo = "Banno Snapshots Repo" at "http://10.3.0.26:8081/nexus/content/repositories/releases"

  def copyrightLine = "Copyright 2011 T8 Webware"
  override def compileAction = super.compileAction dependsOn formatLicenseHeaders
}
