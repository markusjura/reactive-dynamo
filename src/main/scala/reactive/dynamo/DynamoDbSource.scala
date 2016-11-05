package reactive.dynamo

import akka.NotUsed
import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.scaladsl.Source
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler, TimerGraphStageLogic}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreamsClient
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.model.Record
import reactive.dynamo.EventName.{Insert, Modify, Remove}

import scala.concurrent.duration._
import scala.collection.JavaConverters
import JavaConverters._
import scala.concurrent.{ExecutionContext, Future, blocking}


object DynamoDbSource {
  def apply(streamArn: String, endpoint: String)(implicit executionContext: ExecutionContext): Source[Record, NotUsed] = {
    Source.fromGraph(new GraphStage[SourceShape[Record]] {
      val recordOut = Outlet[Record]("recordOut")
      val shape: SourceShape[Record] = SourceShape(recordOut)

      override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new TimerGraphStageLogic(shape) {
        val streamsClient = new AmazonDynamoDBStreamsClient(new ProfileCredentialsProvider)
        streamsClient.setEndpoint(endpoint)
        val describeStreamResult: DescribeStreamResult = streamsClient.describeStream(new DescribeStreamRequest().withStreamArn(streamArn))
        var shards: List[Shard] = describeStreamResult.getStreamDescription.getShards.asScala.toList
        def currentShard = shards.head
        println(s"found shards ${shards.size}")

        var nextIterator = nextIter()
        setHandler(recordOut, new OutHandler {
          override def onPull(): Unit = {
            doPoll
          }
        })

        def handleRecords(recordsResult: GetRecordsResult): Unit = {
          val records: List[Record] = recordsResult.getRecords.asScala.toList.map(mapRecord)
          println("Getting records...")
          println(s"found ${records.size}")

          emitMultiple(recordOut, records)
          recordsResult.getNextShardIterator match {
            case null =>
              shards = shards.tail
              println(s"now using shard ${currentShard.getShardId}")
              nextIterator = nextIter()
            case next => nextIterator = next
          }
          if(records.isEmpty)
            if (currentShard.getSequenceNumberRange.getEndingSequenceNumber == null)
              scheduleOnce("PullTimer", 2 seconds)
            else
              doPoll
        }

        def getRecordsResult: Future[GetRecordsResult] = {
          Future(blocking{streamsClient.getRecords(new GetRecordsRequest().withShardIterator(nextIterator))})
        }

        override def onTimer(timerKey: Any): Unit = doPoll

        def doPoll: Unit = {
          val callback = getAsyncCallback(handleRecords)
          getRecordsResult.foreach(callback.invoke)
        }

        def nextIter(): String = {
          val getShardIteratorRequest: GetShardIteratorRequest = new GetShardIteratorRequest()
            .withStreamArn(streamArn)
            .withShardId(currentShard.getShardId)
            .withShardIteratorType(ShardIteratorType.TRIM_HORIZON)
          streamsClient.getShardIterator(getShardIteratorRequest).getShardIterator
        }

        def mapRecord(awsRecord: com.amazonaws.services.dynamodbv2.model.Record) : Record = {
          val eventName = awsRecord.getEventName match {
            case "INSERT" => Insert
            case "MODIFY" => Modify
            case "REMOVE" => Remove
          }
          Record(awsRecord.getAwsRegion, awsRecord.getEventID, eventName, null)
        }
      }
    })
  }
}
