package reactivedynamo.clients.consumer

import akka.NotUsed
import akka.actor.Actor.emptyBehavior
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.{ Directives, Route }
import akka.stream.ActorMaterializer
import akka.pattern._
import akka.stream.scaladsl.Source
import akka.util.{ ByteString, Timeout }
import reactive.dynamo.Record

import scala.concurrent.Future
import scala.concurrent.duration._

object ConsumerClient {
  final val Name = "consumer-client"

  def props(dynamoDBClient: ActorRef): Props =
    Props(new ConsumerClient(dynamoDBClient))
}

class ConsumerClient(dynamoDBClient: ActorRef) extends Actor with ActorLogging with ActorSettings {

  import settings.client._
  import context.dispatcher

  private implicit val mat = ActorMaterializer()

  override def preStart(): Unit = {
    super.preStart()
    log.debug(s"Listening on $ip:$port")
    Http(context.system)
      .bindAndHandle(route, ip, port)
  }

  private def route: Route = {
    import Directives._

    pathEndOrSingleSlash {
      get {
        complete {
          streamEvents()
        }
      }
    }
  }

  private def streamEvents(): Future[HttpResponse] = {
    implicit val timeout = Timeout(500.millis)
    dynamoDBClient
      .ask(DynamoDBClient.GetStream)
      .mapTo[Source[Record, NotUsed]]
      .map { source =>
        val eventLines = source
          .map(record => ByteString(record.toString + "\n"))
        HttpResponse(entity = HttpEntity.Chunked.fromData(ContentTypes.`text/plain(UTF-8)`, eventLines))
      }
  }

  override def receive: Receive =
    emptyBehavior
}
