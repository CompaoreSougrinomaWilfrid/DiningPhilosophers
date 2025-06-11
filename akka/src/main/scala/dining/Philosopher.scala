package dining

import akka.actor.{Actor, ActorRef}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

case object Hungry
case object DoneEating

class Philosopher(id: Int, leftFork: ActorRef, rightFork: ActorRef) extends Actor {
  private val (firstFork, secondFork) = {
    val leftId = leftFork.path.name.stripPrefix("fork").toInt
    val rightId = rightFork.path.name.stripPrefix("fork").toInt
    if (leftId < rightId) (leftFork, rightFork) else (rightFork, leftFork)
  }

  override def preStart(): Unit = {
    println(s"Philosopher-$id is thinking.")
    context.system.scheduler.scheduleOnce((100 + Random.nextInt(400)).millis, self, Hungry)
  }

  def receive: Receive = thinking

  private def thinking: Receive = {
    case Hungry =>
      println(s"Philosopher-$id is hungry.")
      firstFork ! Take(self)
      context.become(waitingForFirst)
  }

  private def waitingForFirst: Receive = {
    case Taken =>
      println(s"Philosopher-$id picked up Fork-${sender().path.name.stripPrefix("fork")}")
      secondFork ! Take(self)
      context.become(waitingForSecond)
  }

  private def waitingForSecond: Receive = {
    case Taken =>
      println(s"Philosopher-$id picked up Fork-${sender().path.name.stripPrefix("fork")}")
      println(s"Philosopher-$id starts eating.")
      context.system.scheduler.scheduleOnce(200.millis, self, DoneEating)
      context.become(eating)
  }

  private def eating: Receive = {
    case DoneEating =>
      println(s"Philosopher-$id releases Fork-${firstFork.path.name.stripPrefix("fork")} and Fork-${secondFork.path.name.stripPrefix("fork")}.")
      firstFork ! Release(self)
      secondFork ! Release(self)
      println(s"Philosopher-$id is thinking.")
      context.system.scheduler.scheduleOnce(500.millis, self, Hungry)
      context.become(thinking)
  }
}
