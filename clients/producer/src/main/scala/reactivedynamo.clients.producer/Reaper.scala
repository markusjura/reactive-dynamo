package reactivedynamo.clients.producer

import akka.actor.{ Actor, ActorLogging, Props, Terminated }

object Reaper {
  final val Name = "reaper"

  def props: Props =
    Props(new Reaper)
}

class Reaper extends Actor with ActorLogging {

  override def preStart(): Unit = {
    super.preStart()

    val dynamoDBClient = context.watch(context.actorOf(DynamoDBClient.props, DynamoDBClient.Name))
    context.watch(context.actorOf(ProducerClient.props(dynamoDBClient), ProducerClient.Name))
  }

  override def receive: Receive = {
    case Terminated(childActor) =>
      log.info(s"Terminating Producer Client: The functionality provided by ${childActor.path} is terminated.")
      context.stop(self)
  }
}
