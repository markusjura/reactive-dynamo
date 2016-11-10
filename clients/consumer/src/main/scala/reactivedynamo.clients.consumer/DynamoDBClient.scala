package reactivedynamo.clients.consumer

import akka.actor.{Actor, ActorLogging, Props}
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDBClient, AmazonDynamoDBStreamsClient}
import reactive.dynamo.{DynamoDBSource, DynamoDBSourceConfig}
import reactivedynamo.clients.consumer.DynamoDBClient.GetStream


object DynamoDBClient {

  final val Name = "dynamo-db-client"

  def props: Props =
    Props(new DynamoDBClient)

  case object GetStream

}

class DynamoDBClient extends Actor with ActorSettings with ActorLogging {

  import settings.db._

  val endpoint = s"http://$ip:$port"
  private val ProductCatalogTableName = "ProductCatalog"

  private val credentials = new AWSCredentials {
    override def getAWSAccessKeyId: String = awsSecretKey

    override def getAWSSecretKey: String = awsAccessKeyId
  }


  private val streamClient = new AmazonDynamoDBStreamsClient(credentials)
  private val nonStreamClient = new AmazonDynamoDBClient(credentials)
  nonStreamClient.withEndpoint(endpoint)

  override def receive: Receive = {
    case GetStream =>
      log.info("Stream events..")
      val describeTableResult = nonStreamClient.describeTable(ProductCatalogTableName)
      val streamArn = describeTableResult.getTable.getLatestStreamArn

      val sourceConfig = DynamoDBSourceConfig(streamArn, streamClient, endpoint = Some(endpoint), None)


      sender() ! DynamoDBSource(sourceConfig)
  }
}
