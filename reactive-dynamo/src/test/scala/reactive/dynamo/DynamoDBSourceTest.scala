package reactive.dynamo

import org.scalatest.{FlatSpec, Matchers}


class DynamoDBSourceTest extends FlatSpec with Matchers with TestConfig {


  "A DynamoDBSource " should "be created successfully" in {

    val config = DynamoDBSourceConfig(streamArn = AwsTestingConfig.testStreamArn,
      client = AwsTestingConfig.amazonStreamClient,
      endpoint = None, region = None)

    val source = DynamoDBSource(config)
    source should be(source)
  }



}
