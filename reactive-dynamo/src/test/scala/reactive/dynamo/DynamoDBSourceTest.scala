package reactive.dynamo

import org.scalatest.{FlatSpec, Matchers}
import reactive.dynamo.TestConfig.AwsTestingConfig


class DynamoDBSourceTest extends FlatSpec with Matchers with TestConfig.Executor {


  "A DynamoDBSource " should "be created successfully" in {

    val config = DynamoDBSourceConfig(streamArn = AwsTestingConfig.testStreamArn,
      client = AwsTestingConfig.amazonStreamClient,
      endpoint = None, region = None)

    val source = DynamoDBSource(config)
    source should be(source)
  }



}
