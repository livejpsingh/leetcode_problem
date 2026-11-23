# Section 08: Multithreading Basics

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** Thread Lifecycle, `Runnable` vs `Callable`, `start()` vs `run()`, Monitor Synchronization, `sleep()` vs `wait()`, `volatile` Visibility, Deadlock Conditions.

---

## 1. Executive Summary & Senior Engineer Perspective

Multithreading is the cornerstone of high-performance Java applications. In an L1 interview, interviewers assess whether a candidate understands the **fundamental execution model of threads, shared memory hazards, race conditions, and monitor synchronization**. 

Senior developers know that concurrency bugs are non-deterministic, hard to reproduce in test environments, and capable of causing silent data corruption in production.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: What is the difference between a Process and a Thread?
**Answer:**  
- **Process:** An executing instance of an application with its **own isolated memory address space** allocated by the OS. Processes communicate via Inter-Process Communication (IPC, sockets, pipes). Context switching between processes is heavyweight.
- **Thread:** A lightweight unit of execution within a process. Multiple threads within the same JVM process **share the Heap and Metaspace**, but each thread has its **own private Thread Stack, Program Counter (PC) Register, and Native Method Stack**. Context switching between threads is much faster.

---

### Q2: Extending `Thread` vs Implementing `Runnable` vs Implementing `Callable<V>`
**Answer:**  

| Metric | `Thread` Subclass | `Runnable` Interface | `Callable<V>` Interface |
|---|---|---|---|
| **Inheritance** | Uses up single inheritance (`extends Thread`). | Preserves inheritance (`implements Runnable`). | Preserves inheritance (`implements Callable<V>`). |
| **Return Value** | None (`void`). | None (`void`). | **Returns a value of generic type `V`**. |
| **Exceptions** | Cannot throw checked exceptions. | Cannot throw checked exceptions. | **Can throw checked exceptions (`throws Exception`)**. |
| **Execution** | Thread created directly. | Can be run by `Thread` or **submitted to `ExecutorService`**. | Submitted to **`ExecutorService`** returning a `Future<V>`. |
| **Design** | Couples task definition to thread execution. | **Separates the task from the execution mechanism**. | **Separates the task from the execution mechanism**. |

> **Production Recommendation:** Never extend `Thread` directly in enterprise applications. Always define tasks as `Runnable` or `Callable` and execute them via an `ExecutorService` thread pool.

---

### Q3: What is the Thread Lifecycle and its 6 States in Java?
**Answer:**  
Defined in the enum `java.lang.Thread.State`:

```
               [ NEW ] 
                  | thread.start()
                  v
            [ RUNNABLE ] <--------------------+
             /         \                      |
   waiting  /           \ lock needed         |
   for lock/             v                    |
          |         [ BLOCKED ]               |
          |              | acquired lock      |
          |              +------------------->|
          v                                   |
  wait() / join() / park()                    |
          |                                   |
          v                                   |
     [ WAITING ] -----------------------------+
          |         notify() / notifyAll()    |
          |                                   |
     sleep(t) / wait(t)                       |
          v                                   |
  [ TIMED_WAITING ] --------------------------+
          |         timeout / notify()
          v
    [ TERMINATED ] (run() completed or unhandled exception)
```

1. **`NEW`:** Thread created via `new Thread()`, but `start()` has not been invoked yet.
2. **`RUNNABLE`:** Thread is executing in the JVM, or ready and waiting for OS CPU scheduling.
3. **`BLOCKED`:** Thread is waiting to acquire a monitor lock to enter/re-enter a `synchronized` block/method.
4. **`WAITING`:** Thread is waiting indefinitely for another thread to perform a specific action (via `Object.wait()`, `Thread.join()`, or `LockSupport.park()`).
5. **`TIMED_WAITING`:** Thread is waiting for a specified period (via `Thread.sleep(ms)`, `Object.wait(ms)`, `Thread.join(ms)`).
6. **`TERMINATED`:** Thread has finished executing its `run()` method or died from an unhandled exception.

---

### Q4: `start()` vs `run()`: What happens if you call `run()` directly?
**Answer:**  
- **`thread.start()`:** Asks the JVM to allocate a new native operating system thread, initializes the private stack, and schedules the thread. The OS then invokes the `run()` method asynchronously on the **new thread**.
- **`thread.run()`:** Simply invokes the `run()` method as an ordinary method call on the **current calling thread**. No new thread is spawned!

```java
Thread t = new Thread(() -> {
    System.out.println("Executing thread: " + Thread.currentThread().getName());
});

t.run();   // Prints "Executing thread: main"  (Synchronous!)
t.start(); // Prints "Executing thread: Thread-0" (Asynchronous!)
```
> **Gotcha:** Calling `start()` more than once on the same `Thread` instance throws `java.lang.IllegalThreadStateException`. A thread cannot be restarted once started.

---

### Q5: `sleep()` vs `wait()` (Critical Difference)
**Answer:**  

| Metric | `Thread.sleep(ms)` | `Object.wait()` |
|---|---|---|
| **Class** | Static method of `java.lang.Thread`. | Instance method of `java.lang.Object`. |
| **Lock Release** | **DOES NOT RELEASE** any acquired locks/monitors! | **RELEASES** the monitor lock so other threads can enter. |
| **Condition to Call** | Can be called anywhere. | Must be called **inside a `synchronized` block/method** owning the monitor (otherwise throws `IllegalMonitorStateException`). |
| **Wake-up Trigger** | Elapsed timeout or thread interruption. | Notified via `notify()` / `notifyAll()` or elapsed timeout. |

---

### Q6: How does the `synchronized` keyword work?
**Answer:**  
In Java, every single object has an implicit internal lock called an **Intrinsic Lock** or **Monitor**. `synchronized` enforces **mutual exclusion** (only one thread can execute the protected critical section at a time):

1. **Instance Synchronized Method:** Locks on the **`this`** instance.
   ```java
   public synchronized void update() { ... }
   ```
2. **Static Synchronized Method:** Locks on the **`ClassName.class`** object in Metaspace.
   ```java
   public static synchronized void log() { ... }
   ```
3. **Synchronized Block:** Locks on a specific target object. **Always preferred** over method-level synchronization because it minimizes lock scope:
   ```java
   private final Object lock = new Object();
   public void process() {
       // non-critical code...
       synchronized (lock) {
           // critical section...
       }
   }
   ```

---

### Q7: What is the `volatile` keyword and what does it guarantee?
**Answer:**  
Modern multi-core CPUs use L1/L2/L3 hardware caches. Without synchronization, one thread modifying a variable in CPU Cache 1 may not be visible to another thread reading from CPU Cache 2.

Declaring a variable as **`volatile`**:
1. **Guarantees Visibility:** Every read of a volatile variable is fetched directly from **Main Memory (RAM)**, and every write is flushed immediately to Main Memory.
2. **Prevents Instruction Reordering:** Creates a memory barrier preventing the compiler and CPU from reordering instructions around the volatile read/write.

> **CRITICAL GOTCHA: `volatile` does NOT guarantee Atomicity!**  
> Operations like `count++` are composite operations (Read $\to$ Modify $\to$ Write). If multiple threads execute `volatileCount++`, race conditions still corrupt data. For atomicity, you must use `AtomicInteger` or `synchronized`.

---

### Q8: What is Deadlock, and what are the 4 Coffman conditions?
**Answer:**  
**Deadlock** is a situation where two or more threads are blocked forever, each waiting for a lock held by the other.

#### The 4 Necessary Coffman Conditions:
1. **Mutual Exclusion:** Resources cannot be shared; only one thread holds a resource at a time.
2. **Hold and Wait:** A thread holding a resource waits to acquire additional resources held by other threads.
3. **No Preemption:** A resource cannot be forcibly taken from a thread holding it.
4. **Circular Wait:** Thread A waits for lock 2 (held by B), while Thread B waits for lock 1 (held by A).

#### Prevention Strategy:
Always enforce a **strict, global lock acquisition order** across the entire codebase (e.g., always acquire Lock A before Lock B).

---

## 3. Summary Cheatsheet for Revision

| Topic | Key Rule |
|---|---|
| **Thread Creation** | Implement `Runnable` or `Callable` with `ExecutorService`. Never extend `Thread`. |
| **`start()` vs `run()`** | `start()` spawns native thread; `run()` executes synchronously on the current thread. |
| **`sleep()` vs `wait()`** | `sleep()` retains locks; `wait()` releases the object monitor. |
| **`volatile`** | Guarantees visibility and ordering; does NOT guarantee atomicity for compound ops (`count++`). |
| **Deadlock** | Eliminate circular wait by ordering lock acquisitions consistently. |
