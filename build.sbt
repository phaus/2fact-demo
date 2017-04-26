maintainer in Docker := "Philipp Haußleiter <philipp@haussleiter.de>"

packageName in Docker := packageName.value

version in Docker := version.value

dockerRepository := Some("phaus")

dockerExposedPorts := Seq(9000, 9443)

dockerExposedVolumes := Seq("/opt/docker/logs")

name := """2fact-demo"""

version := "1.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

resolvers += ("maven.javastream.de" at "http://maven.javastream.de/")

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "com.warrenstrange" % "googleauth" % "0.2-SNAPSHOT"
)
