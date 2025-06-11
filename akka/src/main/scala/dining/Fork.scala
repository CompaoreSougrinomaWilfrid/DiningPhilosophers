package dining

import akka.actor.{Actor, ActorRef}

// Messages exchanged
case class Take(philosopher: ActorRef)
case class Release(philosopher: ActorRef)
case object Taken

class Fork(val id: Int) extends Actor {
  private var holder: Option[ActorRef] = None
  private var queue: List[ActorRef] = Nil

  def receive: Receive = {
    case Take(philosopher) =>
      if (holder.isEmpty) {
        holder = Some(philosopher)
        philosopher ! Taken
      } else {
        queue :+= philosopher
      }

    case Release(philosopher) =>
      if (holder.contains(philosopher)) {
        holder = None
        if (queue.nonEmpty) {
          val nextPhilosopher = queue.head
          queue = queue.tail
          holder = Some(nextPhilosopher)
          nextPhilosopher ! Taken
        }
      }
  }
}
