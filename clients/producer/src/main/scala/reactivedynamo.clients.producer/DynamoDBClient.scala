package reactivedynamo.clients.producer

import akka.actor.{Actor, ActorLogging, Cancellable, Props}
import app.ItemGenerator
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item, TableWriteItems}
import com.amazonaws.services.dynamodbv2.model.WriteRequest

import scala.concurrent.duration._

object DynamoDBClient {

  final val Name = "dynamo-db-client"

  def props: Props =
    Props(new DynamoDBClient)

  case object StartWritingRandomData
  case object StopWritingRandomData

  private case object WriteItems
}

class DynamoDBClient extends Actor with ActorSettings with ActorLogging {

  import DynamoDBClient._
  import settings.db._
  import context.dispatcher

  private val ProductCatalogTableName = "ProductCatalog"

  private val credentials = new AWSCredentials {
    override def getAWSAccessKeyId: String = "qweqwe"
    override def getAWSSecretKey: String = "qweqwe"
  }

  private val client = new AmazonDynamoDBClient(credentials)
  client.withEndpoint(s"http://$ip:$port")
  private val db = new DynamoDB(client)

  private val helper = new TableHelper(log)
  helper.deleteTable(ProductCatalogTableName, db)
  helper.createTable(ProductCatalogTableName, 10L, 5L, "Id", "N", db)

  override def receive: Receive =
    idle()

  def idle(): Receive = {
    case StartWritingRandomData =>
      val cancellable = context.system.scheduler.schedule(0.seconds, 200.millis, self, WriteItems)
      context.become(processing(cancellable))
  }

  def processing(cancellable: Cancellable): Receive = {
    case WriteItems =>
      val item = ItemGenerator.next
      log.info("Create new item: {}", item)
      writeItem(ProductCatalogTableName, ItemGenerator.next)

    case StopWritingRandomData =>
      log.info("Stop writing items..")
      cancellable.cancel()
      context.become(idle())
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
