package reactivedynamo.clients.producer

import akka.actor.Actor.{ emptyBehavior }
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpResponse, StatusCodes }
import akka.http.scaladsl.server.{ Directives, Route }
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object ProducerClient {
  final val Name = "producer-client"

  def props(dynamoDBClient: ActorRef): Props =
    Props(new ProducerClient(dynamoDBClient))
}

class ProducerClient(dynamoDBClient: ActorRef) extends Actor with ActorLogging with ActorSettings {

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

    pathPrefix("start") {
      pathEndOrSingleSlash {
        get {
          complete {
            start()
          }
        }
      }
    } ~
      pathPrefix("stop") {
        pathEndOrSingleSlash {
          get {
            complete {
              stop()
            }
          }
        }
      }
  }

  private def start(): Future[HttpResponse] = {
    dynamoDBClient ! DynamoDBClient.StartWritingRandomData
    Future.successful(HttpResponse(StatusCodes.NoContent))
  }

  private def stop(): Future[HttpResponse] = {
    dynamoDBClient ! DynamoDBClient.StopWritingRandomData
    Future.successful(HttpResponse(StatusCodes.NoContent))
  }

  override def receive: Receive =
    emptyBehavior
}
