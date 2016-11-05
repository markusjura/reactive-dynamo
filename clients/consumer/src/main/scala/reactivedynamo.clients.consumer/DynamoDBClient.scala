package reactivedynamo.clients.consumer

import akka.actor.{Actor, ActorLogging, Props}
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item, TableWriteItems}
import com.amazonaws.services.dynamodbv2.model.WriteRequest

object DynamoDBClient {

  final val Name = "dynamo-db-client"

  def props: Props =
    Props(new DynamoDBClient)

  case object StreamEvents
}

class DynamoDBClient extends Actor with ActorSettings with ActorLogging {

  import settings.db._
  import DynamoDBClient._

  private val ProductCatalogTableName = "ProductCatalog"

  private val credentials = new AWSCredentials {
    override def getAWSAccessKeyId: String = "qweqwe"
    override def getAWSSecretKey: String = "qweqwe"
  }

  private val client = new AmazonDynamoDBClient(credentials)
  client.withEndpoint(s"http://$ip:$port")
  private val db = new DynamoDB(client)

  override def receive: Receive = {
    case StreamEvents =>
      log.info("Stream events..:")
  }

  private def writeItem(tableName: String, item: Item): Unit = {
    def writeUnprocessedItems(unprocessedItems: java.util.Map[String, java.util.List[WriteRequest]]): Unit = {
      if(!unprocessedItems.isEmpty) {
        val result = db.batchWriteItemUnprocessed(unprocessedItems)
        log.info("Unprocessed item size: {}", result.getUnprocessedItems.size())
        writeUnprocessedItems(result.getUnprocessedItems)
      }
    }
    try {
      val writeItems = new TableWriteItems(tableName).withItemsToPut(item)
      val result = db.batchWriteItem(writeItems)
      writeUnprocessedItems(result.getUnprocessedItems)

    } catch {
      case e: Exception =>
        log.error("Failed to write items with error: {}", e.getMessage)
    }
  }
}
