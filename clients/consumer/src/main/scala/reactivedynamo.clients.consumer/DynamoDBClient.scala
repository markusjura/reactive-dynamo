package reactivedynamo.clients.consumer

import akka.actor.{ Actor, ActorLogging, Props }
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.dynamodbv2.{ AmazonDynamoDBClient, AmazonDynamoDBStreamsClient }
import com.amazonaws.services.dynamodbv2.document.{ DynamoDB, Item, TableWriteItems }
import com.amazonaws.services.dynamodbv2.model.WriteRequest
import reactive.dynamo.{ DynamoDbSource, DynamoDbSourceConfig }

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

  //todo

  val endpoint = s"http://$ip:$port"
  private val streamClient = new AmazonDynamoDBStreamsClient(credentials)
  private val nonStreamClient = new AmazonDynamoDBClient(credentials)
  streamClient.withEndpoint(endpoint)
  nonStreamClient.withEndpoint(endpoint)
  private val db = new DynamoDB(nonStreamClient)

  override def receive: Receive = {
    case GetStream =>
      log.info("Stream events..")
      val describeTableResult = nonStreamClient.describeTable(ProductCatalogTableName)
      val streamArn = describeTableResult.getTable.getLatestStreamArn
      val sourceConfig = DynamoDbSourceConfig(streamArn, endpoint, streamClient)
      sender() ! DynamoDbSource(sourceConfig)
  }
}
