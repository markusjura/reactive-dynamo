package reactivedynamo.clients.producer

import akka.actor.{ Actor, ExtendedActorSystem, Extension, ExtensionKey }

object Settings extends ExtensionKey[Settings]

/**
 * Settings for producer client
 */
class Settings(system: ExtendedActorSystem) extends Extension {

  object client {
    val ip: String =
      producer.getString("client.ip")

    val port: Int =
      producer.getInt("client.port")
  }

  object db {
    val ip: String =
      producer.getString("db.ip")

    val port: Int =
      producer.getInt("db.port")
  }

  private lazy val config = system.settings.config
  private lazy val producer = config.getConfig("producer")
}

trait ActorSettings {
  this: Actor =>

  protected val settings: Settings =
    Settings(context.system)
}