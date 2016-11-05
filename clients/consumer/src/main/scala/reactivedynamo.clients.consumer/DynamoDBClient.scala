package reactivedynamo.clients.consumer

import akka.actor.{ Actor, ActorLogging, Props }
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.{ DynamoDB, Item, TableWriteItems }
import com.amazonaws.services.dynamodbv2.model.WriteRequest
import reactive.dynamo.DynamoDbSource

object DynamoDBClient {

  final val Name = "dynamo-db-client"

  def props: Props =
    Props(new DynamoDBClient)

  case object GetStream
}

class DynamoDBClient extends Actor with ActorSettings with ActorLogging {

  import settings.db._
  import DynamoDBClient._
  import context.dispatcher

  private val ProductCatalogTableName = "ProductCatalog"

  private val credentials = new AWSCredentials {
    override def getAWSAccessKeyId: String = "qweqwe"
    override def getAWSSecretKey: String = "qweqwe"
  }

  val endpoint = s"http://$ip:$port"
  private val client = new AmazonDynamoDBClient(credentials)
  client.withEndpoint(endpoint)
  private val db = new DynamoDB(client)

  override def receive: Receive = {
    case GetStream =>
      log.info("Stream events..")
      val describeTableResult = client.describeTable(ProductCatalogTableName)
      val streamArn = describeTableResult.getTable.getLatestStreamArn
      sender() ! DynamoDbSource(streamArn, endpoint)
  }
}
