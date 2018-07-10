lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "io.blue",
  scalaVersion := "2.12.3"
)


resolvers += Resolver.jcenterRepo

unmanagedJars in Compile ++= (file("lib/") * "*.jar").classpath

libraryDependencies ++= Seq(
   "org.apache.commons" % "commons-lang3" % "3.7"
  ,"commons-cli" % "commons-cli" % "1.4"
  ,"net.andreinc.ansiscape" % "ansiscape" % "0.0.2"
  ,"me.tongfei" % "progressbar" % "0.6.0"
  ,"org.apache.logging.log4j" % "log4j-api" % "2.11.0"
  ,"org.apache.logging.log4j" % "log4j-core" % "2.11.0"
  ,"org.slf4j" % "slf4j-log4j12" % "1.7.25"
  ,"commons-io" % "commons-io" % "2.6"
  ,"org.apache.commons" % "commons-configuration2" % "2.2"
  ,"commons-beanutils" % "commons-beanutils" % "1.9.3"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}