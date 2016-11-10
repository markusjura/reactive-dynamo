package reactive.dynamo

import com.amazonaws.regions.Region
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreamsClient

object DynamoDBSourceConfig {
  /**
    *
    * @param streamArn The Amazon Resource Name (ARN) for the stream.
    * @param endpoint  The endpoint (ex: "ec2.amazonaws.com") or a full URL,
    *                  including the protocol (ex: "https://ec2.amazonaws.com") of
    *                  the region specific AWS endpoint this client will communicate
    *                  with.
    * @param region Metadata for an AWS region, including its name and what services.
    * @param client  Amazon DynamoDB Streams client.
    */
  def apply(streamArn: String, client: AmazonDynamoDBStreamsClient, endpoint: Option[String] = None,
            region: Option[Region] = None): DynamoDBSourceConfig = {
    endpoint foreach client.setEndpoint
    region foreach client.setRegion
    new DynamoDBSourceConfig(streamArn, client)
  }
}

case class DynamoDBSourceConfig(streamArn: String, client: AmazonDynamoDBStreamsClient)

