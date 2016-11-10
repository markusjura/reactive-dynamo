import Dependencies._

libraryDependencies ++= Seq(akkaStream,awsSdkDynamo,scalaTest,mockitoTest,akkaStreamTestKit,akkaTestKit)

fork in run := true
