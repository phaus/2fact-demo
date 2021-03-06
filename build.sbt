maintainer in Docker := "Philipp Haußleiter <philipp@haussleiter.de>"

packageName in Docker := packageName.value

version in Docker := version.value

dockerRepository := Some("phaus")

dockerExposedPorts := Seq(9000, 9443)

dockerExposedVolumes := Seq("/opt/docker/logs")

name := """2fact-demo"""

version := "2.5.10"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

resolvers ++= Seq(
    "maven.javastream.de" at "http://maven.javastream.de"
)

libraryDependencies ++= Seq(
  evolutions,
  javaJdbc,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "42.2.12",
  "org.webjars" %% "webjars-play" % "2.5.0",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "jquery" % "3.2.0",
  "com.warrenstrange" % "googleauth" % "1.1.1",
  "org.mindrot" % "jbcrypt" % "0.3m"
)
