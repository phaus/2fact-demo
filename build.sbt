name := """2fact-demo"""

version := "1.0-SNAPSHOT"

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
