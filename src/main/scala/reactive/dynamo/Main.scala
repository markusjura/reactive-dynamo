package reactive.dynamo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

object Main extends App {
  implicit val system = ActorSystem.apply("dynamoSystem")
  implicit val materializer = ActorMaterializer()
  val source = DynamoDbSource.apply("arn:aws:dynamodb:ddblocal:000000000000:table/TestTableForStreams/stream/2016-11-05T12:23:25.246", "http://localhost:8000")
  source.to(Sink.foreach(println)).run()
}
