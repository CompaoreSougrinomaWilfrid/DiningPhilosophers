package dining

import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.util.Timeout
import scala.io.StdIn

object Main extends App {
  val mode = if (args.nonEmpty) args(0) else "local"
  val N    = if (args.length > 1) args(1).toInt else 5

  implicit val timeout: Timeout = Timeout(5.seconds)

  mode match {
    case "local" =>
      val system = ActorSystem("DiningSystem")
      val forks = (0 until N).map(i => system.actorOf(Props(new Fork(i)), name = s"fork$i"))

      for (i <- 0 until N) {
        val leftForkRef  = forks(i)
        val rightForkRef = forks((i + 1) % N)
        system.actorOf(Props(new Philosopher(i, leftForkRef, rightForkRef)), name = s"phil$i")
      }

      // Empêche la fermeture immédiate
      println("Simulation en cours. Appuyez sur Entrée pour quitter.")
      StdIn.readLine()
      system.terminate()

    case "node1" =>
      val system = ActorSystem("DiningSystem")
      (0 until N).foreach(i => system.actorOf(Props(new Fork(i)), name = s"fork$i"))
      println(s"Fork server started with $N forks.")

      // Empêche la fermeture immédiate
      println("Fork server démarré. Appuyez sur Entrée pour quitter.")
      StdIn.readLine()
      system.terminate()

    case "node2" =>
      val system = ActorSystem("DiningSystem")
      val host = if (args.length > 2) args(2) else "127.0.0.1"
      val port = if (args.length > 3) args(3) else "2551"

      for (i <- 0 until N) {
        val forkPath1 = s"akka://DiningSystem@$host:$port/user/fork$i"
        val forkPath2 = s"akka://DiningSystem@$host:$port/user/fork${(i + 1) % N}"

        val forkRef1 = Await.result(system.actorSelection(forkPath1).resolveOne(), timeout.duration)
        val forkRef2 = Await.result(system.actorSelection(forkPath2).resolveOne(), timeout.duration)

        system.actorOf(Props(new Philosopher(i, forkRef1, forkRef2)), name = s"phil$i")
      }

      println(s"Philosopher node started with $N philosophers.")

      // Empêche la fermeture immédiate
      println("Noeud philosophe démarré. Appuyez sur Entrée pour quitter.")
      StdIn.readLine()
      system.terminate()
  }
}
