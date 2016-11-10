package reactive.dynamo

import akka.event.Logging
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreamsClient


trait TestConfig {
  implicit lazy val system = akka.actor.ActorSystem("DynamoDBTestingSystem")
  implicit lazy val materializer = akka.stream.ActorMaterializer()

  object Executor {
    protected implicit val executor = system.dispatcher
    protected implicit val log = Logging(system, "DynamoDBTestingSystem-Logger")
  }

  object AwsTestingConfig {
    val testStreamArn = "test"

    val aWSCredentials = new AWSCredentials {
      override def getAWSAccessKeyId: String = "testKey"

      override def getAWSSecretKey: String = "testKey"
    }
    val amazonStreamClient = new AmazonDynamoDBStreamsClient(aWSCredentials)
  }
}
