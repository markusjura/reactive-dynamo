package reactivedynamo.clients.consumer

import akka.actor.Actor.{ emptyBehavior }
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.http.scaladsl.server.{ Directives, Route }
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object ConsumerClient {
  final val Name = "consumer-client"

  def props(dynamoDBClient: ActorRef): Props =
    Props(new ConsumerClient(dynamoDBClient))
}

class ConsumerClient(dynamoDBClient: ActorRef) extends Actor with ActorLogging with ActorSettings {

  import settings.client._

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
    dynamoDBClient ! DynamoDBClient.StreamEvents
    Future.successful(HttpResponse(StatusCodes.NoContent))
  }

  override def receive: Receive =
    emptyBehavior
}
