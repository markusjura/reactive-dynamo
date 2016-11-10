import sbt._

object Dependencies {
  val akkaV = "2.4.12"
  val akkaHttpV = "3.0.0-RC1"
  val scalaTestV = "3.0.1-SNAP1"
  val mockitoV = "2.0.2-beta"

  val akkaStreamTestKitV = "2.0.5"

  val awsSdkDynamo = "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.49"
  val akkaStream  = "com.typesafe.akka" %% "akka-stream" % akkaV
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpV

  val akkaStreamTestKit = "com.typesafe.akka" %% "akka-stream-testkit-experimental" % akkaStreamTestKitV
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % akkaV

  val mockitoTest = "org.mockito" % "mockito-all" % mockitoV

  val scalaTest = "org.scalatest" % "scalatest_2.11" % scalaTestV
}