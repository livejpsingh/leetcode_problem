# Section 17: Advanced Concurrency & Java Memory Model (JMM)

> **Level:** L2 (Senior / Deep Technical Architecture)  
> **Target Experience:** 5–10+ Years (Senior Engineer / Tech Lead / Architect Perspective)  
> **Key Focus:** JMM Happens-Before Semantics, CAS & Lock-Free Data Structures, `LongAdder` vs `AtomicLong`, AQS, StampedLock, ThreadPoolExecutor Internals, `ThreadLocal` Leaks & Java 21 Virtual Threads.

---

## 1. Executive Summary & Senior Engineer Perspective

Concurrency is where system stability is made or broken. At the L2 level, interviewers probe **hardware-level memory barriers, lock-free algorithms, CPU cache contention (false sharing), thread pool saturation policies, and the revolutionary shift from platform threads to Virtual Threads in Java 21**.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: What is the Java Memory Model (JMM) and what is the "Happens-Before" Relationship?
**Answer:**  
The **Java Memory Model (JSR-133)** defines the rules that govern how threads interact through shared memory, specifying when a write by one thread is guaranteed to be visible to a read by another thread.

#### The Fundamental "Happens-Before" Guarantees:
If action $A$ happens-before action $B$ ($A \xrightarrow{hb} B$), the memory effects of $A$ are guaranteed to be fully visible to $B$:
1. **Program Order Rule:** Within a single thread, each action happens-before any subsequent action in program order.
2. **Monitor Lock Rule:** An unlock on a monitor lock happens-before every subsequent lock on that **same monitor**.
3. **Volatile Variable Rule:** A write to a `volatile` variable happens-before every subsequent read of that **same volatile variable**.
4. **Thread Start Rule:** Calling `thread.start()` happens-before any action in the started thread.
5. **Thread Join Rule:** Any action in a thread happens-before any other thread successfully returns from `thread.join()` on that thread.
6. **Transitivity:** If $A \xrightarrow{hb} B$ and $B \xrightarrow{hb} C$, then $A \xrightarrow{hb} C$.

---

### Q2: How does Lock-Free Concurrency work? Explain CAS (Compare-And-Swap) and the ABA Problem.
**Answer:**  

#### Compare-And-Swap (CAS):
CAS is an atomic CPU instruction (e.g., `CMPXCHG` on x86) that takes three arguments:
- Memory location $V$
- Expected old value $A$
- New value $B$

```
CAS(V, A, B):
  if (value at V == A) {
      value at V = B;
      return true; // update succeeded
  } else {
      return false; // another thread modified it; retry loop!
  }
```
All atomic classes (`AtomicInteger`, `AtomicReference`) use CAS in an optimistic loop. **No OS thread is blocked or put to sleep**, eliminating context-switching overhead.

#### The ABA Problem:
1. Thread 1 reads value $A$ from location $V$.
2. Thread 1 is pre-empted by the OS.
3. Thread 2 changes $A \to B \to A$.
4. Thread 1 resumes and performs CAS expecting $A$. It sees $A$ and succeeds, **completely oblivious to the fact that state was modified and reverted** (which can corrupt node pointers in lock-free linked lists!).
- **The Solution:** **`AtomicStampedReference`** or **`AtomicMarkableReference`**, which pairs an integer stamp/version with the object reference (`reference + version`).

---

### Q3: Why does `LongAdder` dramatically outperform `AtomicLong` under high contention?
**Answer:**  
- **`AtomicLong` Bottleneck:** All threads contend on a **single volatile 64-bit value**. Under high concurrency, hundreds of threads repeatedly fail CAS, spinning in busy loops and generating massive **cache-line bouncing** across CPU cores.
- **`LongAdder` (Striped64):** Internally maintains a base value plus an array of `Cell` objects:
  $$\text{Total} = \text{base} + \sum \text{Cell}[i].\text{value}$$
  - Each thread is hashed to a specific `Cell` using its thread probe hash.
  - Threads update their own local `Cell` independently with zero contention!
  - `@jdk.internal.vm.annotation.Contended` adds 128 bytes of padding around each `Cell` to prevent **False Sharing** (two cells sitting on the same 64-byte CPU L1 cache line).
  - Only when `sum()` is invoked does it aggregate all cells.

---

### Q4: How does `ThreadPoolExecutor` execute tasks internally?
**Answer:**  
When you submit a new task to a `ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAlive, queue)`:

```
               Task arrives at ThreadPoolExecutor
                              |
                 Are running threads < corePoolSize?
                              |
                    +---------+---------+
                 YES|                 NO|
                    v                   v
          Spawn a new worker      Is workQueue FULL?
          thread immediately            |
                               +--------+--------+
                            NO |              YES|
                               v                 v
                         Enqueue task   Are running threads < maxPoolSize?
                         into workQueue          |
                                        +--------+--------+
                                     YES|               NO|
                                        v                 v
                                  Spawn new worker   Execute Rejection Policy!
                                  thread up to max   (Abort, CallerRuns, Discard)
```

#### Rejection Policies (`RejectedExecutionHandler`):
1. **`AbortPolicy` (Default):** Throws `RejectedExecutionException`.
2. **`CallerRunsPolicy` (Best for Back-Pressure):** The calling thread that submitted the task executes the task itself! This naturally throttles the producer from submitting new tasks until it finishes.
3. **`DiscardPolicy`:** Silently drops the task with no error.
4. **`DiscardOldestPolicy`:** Discards the oldest unhandled task in the queue and retries execution.

> **Production Rule:** Never use `Executors.newCachedThreadPool()` (unbounded threads $\to$ CPU exhaustion) or `Executors.newFixedThreadPool()` (unbounded `LinkedBlockingQueue` $\to$ `OutOfMemoryError`). Always construct `ThreadPoolExecutor` explicitly with bounded queues and custom thread factories.

---

### Q5: How do `ThreadLocal` Memory Leaks occur in Thread Pools?
**Answer:**  
In Java, each `Thread` object has a field:
```java
ThreadLocal.ThreadLocalMap threadLocals;
```
The entries in `ThreadLocalMap` extend `WeakReference<ThreadLocal<?>>`:
- The **Key** is a `WeakReference` to the `ThreadLocal` object.
- The **Value** is a **Strong Reference** to the user data!

```
Thread (Worker in Pool - NEVER DIES)
  |
  +---> ThreadLocalMap
          |
          +---> Entry: [ WeakKey: ThreadLocal ] ---> [ StrongValue: LargeUserData ]
                             ^
                             | (Collected by GC when ThreadLocal reference is dropped)
```

#### The Leak Mechanism:
When application code abandons the `ThreadLocal` variable, the weak key is collected during the next GC, leaving an entry with a `null` key pointing to the strong `value`.  
Because threads in a thread pool are pooled and reused for days, the thread never terminates! **The strong value remains reachable and cannot be collected by GC, leaking memory continuously.**

#### The Inviolable Production Rule:
**Always clean up `ThreadLocal` in a `finally` block:**
```java
try {
    userContext.set(currentUser);
    processRequest();
} finally {
    userContext.remove(); // MANDATORY TO PREVENT MEMORY LEAK!
}
```

---

### Q6: What are Virtual Threads (Java 21 / Project Loom)?
**Answer:**  

| Metric | Platform (OS) Thread | Virtual Thread (Java 21) |
|---|---|---|
| **Mapping** | 1-to-1 with an Operating System kernel thread. | $M$-to-$N$: Millions of virtual threads mapped to a few OS **Carrier Threads**. |
| **Creation Cost** | Heavyweight ($1\text{MB}$ stack pre-allocated; limited to $\approx 2,000$ threads per JVM). | Lightweight ($\approx 1\text{KB}$ heap-allocated; millions can run concurrently). |
| **Blocking IO** | Blocks the native OS thread, wasting CPU cores while waiting for network/DB. | **Unmounts** from carrier thread on blocking IO; carrier thread immediately runs another virtual thread! |
| **Context Switch** | Heavyweight kernel context switch. | Lightweight JVM user-space switch (using continuations). |

```java
// Running 1,000,000 concurrent tasks effortlessly in Java 21:
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 1_000_000).forEach(i -> {
        executor.submit(() -> {
            Thread.sleep(1000); // Does NOT block OS thread! Unmounts automatically.
            return i;
        });
    });
}
```

#### Virtual Thread Pinning Caveat:
If a virtual thread enters a **`synchronized` block/method** or invokes a native JNI call, it becomes **pinned** to its carrier thread. When pinned, blocking IO will block the underlying OS carrier thread!  
**Fix:** Replace `synchronized` with `ReentrantLock` in codebases migrating to Virtual Threads.

---

## 3. Summary Cheatsheet for Revision

| Topic | Critical Concurrency Fact |
|---|---|
| **Happens-Before** | Volatile write $\xrightarrow{hb}$ volatile read; monitor unlock $\xrightarrow{hb}$ monitor lock. |
| **`LongAdder`** | Strips into cells; eliminates contention and cache-line bouncing compared to `AtomicLong`. |
| **`ThreadPoolExecutor`** | Core threads $\to$ Queue $\to$ Max threads $\to$ Rejection policy. Avoid unbounded queues. |
| **`ThreadLocal` Leak** | Worker threads in pools retain values via strong references. Always call `remove()` in `finally`. |
| **Virtual Threads** | Lightweight user-mode threads unmounted on IO. Avoid `synchronized` to prevent carrier pinning. |
