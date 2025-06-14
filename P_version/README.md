# Dining Philosophers Problem in P Language - Assignment Implementation

This project implements the Dining Philosophers problem using the P language to demonstrate formal verification and deadlock detection. It includes two variants and a formal specification to verify their behavior.

---

## ✅ Assignment Requirements Fulfilled

### Core Requirements:

* Two variants of Dining Philosophers:

  1. All philosophers take the left fork first (**deadlock-prone**)
  2. One philosopher takes the forks in reverse order (**deadlock-free**)
* A formal specification for deadlock detection
* Verification of both solutions using the P checker

### Bonus Features:

* ✅ Structure mirrors the Akka implementation
* ✅ Parametric version that allows any number of philosophers `N`

---

## 📁 Project Structure

```
P_version/
├── PSrc/              # Source files
│   ├── Philosopher.p  # Philosopher machine
│   ├── Fork.p         # Fork machine
│   ├── Simulation1.p  # Deadlock-prone version
│   ├── Simulation2.p  # Deadlock-free version
│   └── Modules.p      # Shared modules
├── PSpec/
│   └── Specification.p  # Deadlock detection monitor
├── PTst/              # Test entrypoints
└── PGenerated/        # Output from compilation
```

---

## 🧠 Implementation Details

### 🔴 Variant 1: Deadlock-Prone Solution (`Simulation1.p`)

```p
machine Philosopher {
  var leftFirst = true;
  // ... philosopher takes left fork then right ...
}
```

* All philosophers use the same order: **Left → Right**
* They hold the left fork and wait for the right
* **Expected behavior:** deadlock when all philosophers are blocked

### ✅ Variant 2: Deadlock-Free Solution (`Simulation2.p`)

```p
machine Philosopher {
  var leftFirst = (id != NUM_PHILOSOPHERS - 1);
  // ... last philosopher reverses fork order ...
}
```

* Philosopher N-1 picks up **right fork first**
* Breaks the circular wait condition
* **Expected behavior:** no deadlock occurs

---

## ▶️ How to Run the P Checker

### Prerequisites:

* P language CLI tool installed
* All files organized as described above

### Compilation:

```bash
p compile -pp DiningPhilosophers.pproj
```

### Run Deadlock-Prone Test (Simulation1):

```bash
p check -tc tcDeadlock -ms 1000 -s 5
```

### Run Deadlock-Free Test (Simulation2):

```bash
p check -tc tcNoDeadlock -ms 1000 -s 5
```

These commands:

* Launch the checker with a test case
* Search for bugs or confirm correctness

---

## ✅ Expected Outcomes

### 🔴 Simulation1 (deadlock expected):

```
Checker found a bug.
Writing trace: DiningPhilosophers_0_0.txt
...
Found 1 bug.
```

### ✅ Simulation2 (no deadlock):

```
Checker completed.
Found 0 bugs.
```

---

## 📐 Specification Overview (PSpec/Specification.p)

The monitor watches `eForkAcquired` and `eReleaseFork`. It keeps track of how many philosophers are holding forks:

```p
spec DeadlockDetection observes eForkAcquired, eReleaseFork {
  var philosophers: set[machine];

  on eForkAcquired do (payload: (philosopher: machine)) {
    philosophers += payload.philosopher;
    assert sizeof(philosophers) < N;
  }

  on eReleaseFork do (payload: (philosopher: machine)) {
    philosophers -= payload.philosopher;
  }
}
```

If all `N` philosophers are holding one fork, it asserts a **hard deadlock**.

---

## 🔁 Relationship to Akka Implementation

| Akka Component     | P Equivalent          |
| ------------------ | --------------------- |
| `Philosopher`      | `Philosopher` machine |
| `Fork`             | `Fork` machine        |
| Actor messages     | Events in P           |
| State transitions  | P state transitions   |
| Deadlock Avoidance | Same asymmetry logic  |

The P model mimics the structure and logic of the Akka actor system.

---

## 🛠 Requirements

* P language compiler (CLI tool)
* Graphviz (optional, for visualizing state machines)

Make sure all files are properly organized in their folders, and that the `.pproj` file correctly lists all source and spec files.
