# L2 Level Java Developer Interview Guide

> **Target Profile:** 5–10+ Years of Experience  
> **Evaluation Benchmark:** Evaluated from a 10+ YOE Senior Staff / Principal Architect Perspective  
> **Coverage:** JVM Internals, Garbage Collection Tuning, Memory Leaks, Concurrency & JMM, Collections Bitwise Math, Generics Invariance, Java 8–21 Evolution, and Resilient Architecture.

---

## 1. L2 Senior Competency & Architecture Rubric

| Domain | What the Interviewer Assesses | Target Senior Depth |
|---|---|---|
| **JVM & Memory Management** | Runtime memory layout, Metaspace vs PermGen, Escape Analysis, Object alignment, GC algorithms (G1, ZGC). | Heap/Thread dump analysis, GC tuning flags |
| **Advanced OOP & Design** | SOLID in enterprise systems, Liskov substitution violations, Double-Checked Locking Singleton, Proxies (JDK vs CGLIB). | Production design patterns & thread safety |
| **Strings & Wrappers Advanced** | Compact Strings (`byte[]` + coder), G1 String Deduplication, Unicode surrogate pairs, `BigDecimal` scale/rounding. | Low-level memory & mathematical precision |
| **Collections Internals** | `HashMap` hash perturbation, treeification thresholds, power-of-two resizing, `ConcurrentHashMap` CAS + synchronized bins. | Algorithmic internals & bitwise operations |
| **Generics & Type Systems** | Type erasure, compiler bridge methods, Invariance vs Covariance, PECS (`? extends` vs `? super`), heap pollution. | Generic API library design |
| **Resilient Exception Architecture** | Asynchronous exceptions, `UncaughtExceptionHandler`, `CompletableFuture` error pipelines, stack trace cost, Circuit Breaker. | Fault tolerance in distributed systems |
| **Advanced Concurrency & JMM** | JMM happens-before semantics, CAS & lock-free algorithms, `LongAdder` cell striping, `ThreadPoolExecutor` saturation, Virtual Threads. | High-throughput concurrent systems |
| **Modern Java (8 to 21)** | Stream internals (`Spliterator` / `Sink`), parallel stream starvation, Records, Sealed classes, Pattern Matching, Sequenced Collections. | Modern idiomatic Java language features |
| **Serialization & Reflection** | `serialVersionUID`, `transient`, deserialization RCE gadget chains, Serialization Proxy Pattern, Reflection overhead, Annotations. | System security & serialization safety |

---

## 2. L2 Topic Navigation

Each section below contains comprehensive, production-grade interview questions, architectural explanations, code snippets, gotchas, and 10-YOE tips:

1. [**Section 11: JVM Internals, Memory Management & GC**](./11-jvm-internals-memory-management/)
   - Metaspace vs PermGen, Escape Analysis, 16-byte object overhead, GC algorithms (Parallel, G1, ZGC), Reference types, Heap dump analysis with MAT.
2. [**Section 12: Advanced OOP & Software Design**](./12-oop-advanced-design/)
   - SOLID enterprise scenarios, Square-Rectangle LSP failure, DCL Singleton with `volatile`, JDK Dynamic Proxy vs CGLIB, Cloning alternatives.
3. [**Section 13: Strings & Wrappers Advanced Internals**](./13-strings-wrappers-advanced/)
   - Java 9 Compact Strings, G1 String Deduplication, Unicode surrogate pairs & emojis, `BigDecimal` scale & non-terminating decimal crashes.
4. [**Section 14: Collections Framework Advanced Internals**](./14-collections-framework-advanced/)
   - `HashMap` bitwise hash perturbation `(h) ^ (h >>> 16)`, Treeification threshold (8) & table limit (64), `ConcurrentHashMap` CAS + synchronized bins, LRU Cache.
5. [**Section 15: Generics & Type Systems**](./15-generics/)
   - Type Erasure bytecode reality, Bridge methods, Generic Invariance, PECS (`? extends` vs `? super`), Generic type limitations.
6. [**Section 16: Advanced Exception Handling & Resilient Architecture**](./16-exception-handling-advanced/)
   - Thread pool exception swallowing, `CompletableFuture` error handling stages, Stack trace CPU costs, Circuit Breaker (Resilience4j).
7. [**Section 17: Advanced Concurrency & Java Memory Model (JMM)**](./17-multithreading-concurrency-advanced/)
   - JMM happens-before rules, CAS lock-free algorithms, `LongAdder` striped cells, `ThreadPoolExecutor` queue & rejection policies, `ThreadLocal` leaks, Virtual Threads (Java 21).
8. [**Section 18: Java 8 to 21 Modern Features & Language Evolution**](./18-java-8-17-features/)
   - Stream `Sink` pipeline & loop fusion, `parallelStream` common pool starvation, Records, Sealed classes & interfaces, Sequenced Collections.
9. [**Section 19: Design Patterns, Serialization & Reflection**](./19-design-patterns-serialization-miscellaneous/)
   - `serialVersionUID` mismatch, `transient` hooks, Deserialization RCE attacks, Serialization Proxy Pattern, Reflection performance costs.

---

## 3. Fast-Track L2 Senior Revision Cheatsheet

- **Object Overhead:** Empty `Object` = 16 bytes (8B Mark Word + 4B Klass Word with Compressed OOPs + 4B padding).
- **Escape Analysis:** JIT scalar-replaces `NoEscape` objects directly onto registers/stack, bypassing heap allocation entirely.
- **ZGC Pause Times:** Achieved through colored pointers and load barriers; STW pause is strictly $< 1\text{ms}$ on multi-terabyte heaps.
- **HashMap Math:** Indexing via `(n - 1) & hash` requires power-of-two table capacity. Treeifies when bin $\ge 8$ and table capacity $\ge 64$.
- **ConcurrentHashMap:** Lock-free reads via volatile variables; CAS insertion for empty bins; synchronized lock restricted strictly to the bucket head.
- **PECS Rule:** Producer Extends (read-only from collection); Consumer Super (write-safe into collection).
- **ThreadLocal Leak:** Thread pool worker threads never terminate; always call `threadLocal.remove()` in a `finally` block.
- **Virtual Threads:** Lightweight user-mode threads unmounted from carrier threads during blocking I/O. Replace `synchronized` with `ReentrantLock` to prevent carrier pinning.
- **Serialization Proxy:** Protects immutable domain classes against byte-stream forgery by using `writeReplace()` and `readResolve()`.
