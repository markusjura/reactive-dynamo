import Dependencies._

libraryDependencies ++= Seq(akkaStream, awsSdkDynamo)

fork in run := true
