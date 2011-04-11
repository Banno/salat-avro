import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val licensePlugin = "com.banno" % "sbt-license-plugin" % "0.0.2" from "http://cloud.github.com/downloads/T8Webware/sbt-license-plugin/sbt-license-plugin-0.0.2.jar"
}
