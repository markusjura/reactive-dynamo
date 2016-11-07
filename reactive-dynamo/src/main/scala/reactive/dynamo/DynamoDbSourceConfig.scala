package reactive.dynamo

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreamsClient

object DynamoDbSourceConfig {}

case class DynamoDbSourceConfig(streamArn: String, endpoint: String, client: AmazonDynamoDBStreamsClient)

