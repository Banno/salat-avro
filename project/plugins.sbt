addSbtPlugin("com.banno" % "sbt-license-plugin" % "0.0.4")

resolvers += Resolver.file("Local Ivy Repository", file("/home/USER/.ivy2/local/"))(Resolver.ivyStylePatterns)
