ThisBuild / organization := "s10myk4.com"

name := "scala-akka-cluster-otel-java"

scalaVersion := "2.13.10"
lazy val akkaHttpVersion = "10.4.0"
lazy val akkaVersion = "2.7.0"
lazy val akkaManagementVersion = "1.2.0"
lazy val otelVersion = "1.28.0"

// make version compatible with docker for publishing
ThisBuild / dynverSeparator := "-"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")
classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars
//run / fork := true
//Compile / run / fork := true

enablePlugins(JavaServerAppPackaging, DockerPlugin, JavaAgent)

dockerExposedPorts := Seq(8080, 8558, 25520)
dockerUpdateLatest := true
dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")
dockerBaseImage := "adoptopenjdk:11-jre-hotspot"

libraryDependencies ++= {
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.12",
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
    "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % akkaManagementVersion,
    "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion,
    "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion,
    "io.opentelemetry" % "opentelemetry-bom" % otelVersion pomOnly(),
    "io.opentelemetry" % "opentelemetry-api" % otelVersion,
    "io.opentelemetry" % "opentelemetry-sdk" % otelVersion,
    "io.opentelemetry" % "opentelemetry-exporter-otlp" % otelVersion,
  )
}

javaAgents += JavaAgent("io.opentelemetry.javaagent" % "opentelemetry-javaagent" % otelVersion % "runtime")
//javaOptions += "-Dotel.javaagent.configuration-file=src/main/resources/otel.properties"
