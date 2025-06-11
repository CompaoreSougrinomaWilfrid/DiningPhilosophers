# Dining Philosophers Problem - Akka Implementation
## ✅ 1. Deadlock Prevention Strategy

### ♻ Resource Hierarchy (Ordered Forks)

Each fork is identified by a unique ID (0, 1, ..., N-1). Each philosopher **always picks up the fork with the smaller ID first**, then the second one. This breaks the circular wait condition and **prevents deadlocks**.

Example:

* Philosopher 2 picks up fork 2 (left), then fork 3 (right)
* Philosopher 4 picks up fork 0, then fork 4

This ordering ensures that no circular wait condition can occur, which is one of the four necessary conditions for deadlock. Since all philosophers are constrained to acquire forks in the same global order, the system is deadlock-free.
---

## 🔧 2. Project Structure

```
DiningPhilosophers/
├── akka/
│   ├── build.sbt
│   ├── project/
│   ├── src/
│   │   └── main/scala/dining/
│   │       ├── Fork.scala
│   │       ├── Philosopher.scala
│   │       └── Main.scala
│   ├── src/main/resources/application.conf
│   └── README.md (this file)
```

---

## 🚀 3. Running in **Local Mode**

### Compile

In a terminal:

```bash
cd akka
sbt compile
```

### Run with 5 philosophers (default)

```bash
sbt run
```

### Run with `N` philosophers (e.g., 7)

```bash
sbt "run local 7"
```

Each philosopher will use two forks (i and (i+1)%N).

---

## 🌐 4. Running in **Remote Mode** (Akka Remoting)

### ⚖️ Architecture

* **Node1** (Fork server) hosts all `Fork` actors
* **Node2** (Client) hosts all `Philosopher` actors

They communicate over TCP using Akka Remote.

### ⚙️ Configuration `application.conf`

In `src/main/resources/application.conf`:

```hocon
akka {
  actor.provider = "akka.remote.RemoteActorRefProvider"
  remote.artery {
    transport = tcp
    canonical.hostname = "127.0.0.1"
    canonical.port = 2551
  }
  loglevel = "INFO"
}
```

For `node2`, override the port using the command line: `-Dakka.remote.artery.canonical.port=2552`

### 📘 Run the Fork Server (Node1)

```bash
sbt -Dakka.remote.artery.canonical.port=2551 "run node1 5"
```

### 📘 Run the Philosopher Node (Node2)

```bash
sbt -Dakka.remote.artery.canonical.port=2552 "run node2 5 127.0.0.1 2551"
```

This connects the philosophers to forks remotely.

---

## 🔁 5. Expected Output

In the console, you'll see logs like:

```
Philosopher-0 is thinking.
Philosopher-0 is hungry.
Philosopher-0 picked up Fork-0.
Philosopher-0 picked up Fork-1.
Philosopher-0 starts eating.
Philosopher-0 releases Fork-0 and Fork-1.
```

This confirms:

* Each philosopher takes turns eating
* ✅ No deadlocks occur
* ✅ The system continues indefinitely

---

## 📝 6. Conclusion

This project provides a robust simulation of the Dining Philosophers problem using Akka. It follows best practices in concurrency and distributed actor modeling, while enforcing deadlock freedom through a proven strategy.

💡 Press **Enter** in the terminal to terminate the ActorSystem gracefully (thanks to `StdIn.readLine()` in `Main.scala`).
