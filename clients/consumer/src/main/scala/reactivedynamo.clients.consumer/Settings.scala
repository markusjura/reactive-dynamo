package reactivedynamo.clients.consumer

import akka.actor.{Actor, ExtendedActorSystem, Extension, ExtensionKey}

object Settings extends ExtensionKey[Settings]

/**
  * Settings for producer client
  */
class Settings(system: ExtendedActorSystem) extends Extension {

  private lazy val config = system.settings.config
  private lazy val consumer = config.getConfig("consumer")

  object client {
    val ip: String =
      consumer.getString("client.ip")

    val port: Int =
      consumer.getInt("client.port")
  }

  object db {
    val ip: String =
      consumer.getString("db.ip")

    val port: Int =
      consumer.getInt("db.port")

    val awsSecretKey: String = consumer.getString("db.AWSSecretKey")

    val awsAccessKeyId: String = consumer.getString("db.AWSAccessKeyId")

  }

}

trait ActorSettings {
  this: Actor =>

  protected val settings: Settings =
    Settings(context.system)
}