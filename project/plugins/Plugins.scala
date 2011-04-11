import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val licensePlugin = "com.banno" % "sbt-license-plugin" % "0.0.1" from "https://github.com/downloads/T8Webware/sbt-license-plugin/sbt-license-plugin-0.0.1.jar"
}
