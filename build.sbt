name := """play-akka-stream"""
organization := "fscoward"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  filters,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "slf4j-api" % "1.7.12",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.2" ,
  "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % "0.3",



  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "fscoward.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "fscoward.binders._"
