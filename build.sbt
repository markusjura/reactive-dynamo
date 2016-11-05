import sbt.Keys.scalaVersion

import scalariform.formatter.preferences._

inThisBuild(Seq(
  version := "0.1",
  scalaVersion := "2.11.8"
))

val formatterSettings = Seq(
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(PreserveDanglingCloseParenthesis, true)
) ++ scalariformSettings

lazy val root =
  Project(
    id = "reactive-dynamo-ROOT",
    base = file(".")
  ).aggregate(reactiveDynamo, consumerClient, producerClient)

lazy val reactiveDynamo = Project(
  id = "reactiveDynamo",
  base = file("reactive-dynamo")
).settings(formatterSettings)

lazy val consumerClient = Project(
  id = "consumer-client",
  base = file("clients/consumer")
)
  .settings(formatterSettings)
  .dependsOn(reactiveDynamo)

lazy val producerClient = Project(
  id = "producer-client",
  base = file("producer/consumer")
).settings(formatterSettings)