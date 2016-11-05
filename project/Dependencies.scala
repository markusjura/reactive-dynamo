import sbt._

object Dependencies {
  val akkaV = "2.4.12"
  val akkaHttpV = "3.0.0-RC1"

  val awsSdkDynamo = "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.49"
  val akkaStream  = "com.typesafe.akka" %% "akka-stream" % akkaV
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpV
}