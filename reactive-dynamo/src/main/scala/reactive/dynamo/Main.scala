package reactive.dynamo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

object Main extends App {
  implicit val system = ActorSystem.apply("dynamoSystem")
  implicit val materializer = ActorMaterializer()
  import scala.concurrent.ExecutionContext.Implicits.global
  val source = DynamoDbSource.apply("arn:aws:dynamodb:eu-west-1:969395864620:table/Music/stream/2016-11-04T20:07:03.258", "streams.dynamodb.eu-west-1.amazonaws.com")
  //  val source = DynamoDbSource.apply("arn:aws:dynamodb:ddblocal:000000000000:table/TestTableForStreams/stream/2016-11-05T12:23:25.246", "http://localhost:8000")
  source.to(Sink.foreach(println)).run()
}
