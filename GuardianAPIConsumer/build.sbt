name := """hello-akka"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-remote" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "com.gu" %% "content-api-client" % "10.2",
  "org.joda" % "joda-convert" % "1.2",
  "mysql" % "mysql-connector-java" % "5.1.12"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")


fork in run := true