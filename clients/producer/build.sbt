name := "producer-client"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "3.0.0-RC1",
  "com.amazonaws" % "aws-java-sdk" % "1.11.49"
)
