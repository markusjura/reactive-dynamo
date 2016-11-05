package reactivedynamo.clients.consumer

import java.util.concurrent.{ CountDownLatch, TimeUnit }
import scala.concurrent.duration._
import akka.actor.{ ActorRef, ActorSystem }

object ConsumerApp extends App {

  run()

  private def run(): Unit = {
    val (system, reaper) = startup()
    sys.addShutdownHook(shutdown(system, reaper))
  }

  private def startup(): (ActorSystem, ActorRef) = {
    println("Consumer client is starting up..\n")
    val system = ActorSystem("consumer")
    val reaper = system.actorOf(Reaper.props, Reaper.Name)
    system -> reaper
  }

  private def shutdown(system: ActorSystem, reaper: ActorRef): Unit = {
    system.log.info("Shutting down actor system")
    val leaveLatch = new CountDownLatch(1)
    val terminateTimeout = 5.seconds
    val terminateLatch = new CountDownLatch(1)
    system.registerOnTermination {
      terminateLatch.countDown()
    }
    system.terminate()
    println("Awaiting shutdown..")
    if (!leaveLatch.await(terminateTimeout.toMillis, TimeUnit.MILLISECONDS)) {
      println("ERROR - Did not receive confirmation that our actor system shutdown. Exiting as we cannot continue.")
      sys.exit(1)
    }
  }
}
