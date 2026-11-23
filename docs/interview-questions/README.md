# End-to-End Java Developer Interview Preparation Master Guide

> **Standard:** Comprehensive alignment with **GeeksforGeeks Java Curriculum** & Enterprise Tech Interviews  
> **Target Audience:** Java Engineers preparing for **L1 (Core Screening, 0–4 YOE)** and **L2 (Senior / Deep Architecture, 5–10+ YOE)** rounds  
> **Author Perspective:** 10+ Years Experienced Senior Java Developer & Technical Lead

---

## 1. Structure of this Knowledge Base

This documentation hub organizes the entire Java landscape into two progressive interview tiers, retaining the structured topic folders:

```
docs/interview-questions/
├── README.md                                          <-- (You are here: Master Portal & Study Plan)
├── l1/                                                <-- L1 Level (Core Screening - 0 to 4 YOE)
│   ├── README.md                                      <-- L1 Master Guide & Revision Cheatsheet
│   ├── 01-java-basics-environment/                    <-- JVM/JRE/JDK, ClassLoaders, Execution Flow
│   ├── 02-data-types-variables-operators/             <-- 8 Primitives, Autoboxing, IEEE 754, Operators
│   ├── 03-oop-concepts/                               <-- 4 Pillars, Overload vs Override, Interfaces
│   ├── 04-strings-wrapper-classes/                    <-- Immutability, SCP, StringBuilder, IntegerCache
│   ├── 05-arrays/                                     <-- Layout, Covariance, Copying, Dual-Pivot Quicksort
│   ├── 06-collections-framework-basics/               <-- List/Set/Queue/Map, Fail-Fast, Comparators
│   ├── 07-exception-handling-basics/                  <-- Checked vs Unchecked, Try-With-Resources
│   ├── 08-multithreading-basics/                      <-- Thread States, Runnable/Callable, volatile
│   ├── 09-java-8-basics/                              <-- Lambdas, Functional Interfaces, Streams, Optional
│   └── 10-basic-programs-output/                      <-- Static Order, Overload Resolution, Anagrams
└── l2/                                                <-- L2 Level (Senior Deep Dive - 5 to 10+ YOE)
    ├── README.md                                      <-- L2 Master Guide & Architect Cheatsheet
    ├── 11-jvm-internals-memory-management/            <-- Metaspace, Escape Analysis, ZGC, Heap/Thread Dumps
    ├── 12-oop-advanced-design/                        <-- SOLID, DCL Singleton, Proxies (JDK vs CGLIB)
    ├── 13-strings-wrappers-advanced/                  <-- Compact Strings, G1 Deduplication, BigDecimal
    ├── 14-collections-framework-advanced/             <-- HashMap & ConcurrentHashMap Bitwise Internals
    ├── 15-generics/                                   <-- Type Erasure, Bridge Methods, PECS Rule
    ├── 16-exception-handling-advanced/                <-- Thread Pool Error Swallowing, Circuit Breakers
    ├── 17-multithreading-concurrency-advanced/        <-- JMM Happens-Before, CAS, LongAdder, Virtual Threads
    ├── 18-java-8-17-features/                         <-- Stream Sinks, Records, Sealed Classes, Java 21
    └── 19-design-patterns-serialization-miscellaneous/<-- Deserialization RCE, Proxy Pattern, Reflection
```

---

## 2. L1 vs L2 Interview Level Expectations

| Dimension | L1 Level (Core / Foundational Round) | L2 Level (Senior / Technical Deep Dive) |
|---|---|---|
| **Target Roles** | Software Engineer, Java Developer (0–4 YOE) | Senior Java Engineer, Tech Lead, Staff Architect (5–10+ YOE) |
| **Primary Focus** | Syntax precision, language rules, OOP principles, standard library usage, core algorithms. | Under-the-hood JVM mechanics, JMM memory barriers, lock-free concurrency, memory leak profiling, system resiliency. |
| **Collections** | Choosing between `ArrayList`, `HashSet`, and `HashMap`. Explaining fail-fast iterators. | Proving why `HashMap` capacity must be a power of two, bitwise hash spreading, and `ConcurrentHashMap` CAS bin locking. |
| **Concurrency** | Thread states, `Runnable` vs `Callable`, `sleep()` vs `wait()`, basic `synchronized` blocks. | JMM happens-before semantics, ABA problem in CAS, `LongAdder` cell striping, `ThreadLocal` leaks, and Virtual Threads. |
| **Memory & GC** | Stack vs Heap, garbage collection definition, `OutOfMemoryError` recognition. | Metaspace native sizing, Escape Analysis scalar replacement, G1 vs ZGC sub-millisecond pauses, analyzing `.hprof` dumps in MAT. |
| **Design & OOP** | Implementing interfaces, method overloading vs overriding rules, `equals`/`hashCode` contract. | SOLID violations in enterprise systems, Liskov Substitution traps, double-checked locking with `volatile`, dynamic bytecode proxies. |

---

## 3. GeeksforGeeks Full Syllabus Alignment

This compilation maps 100% of the topics from the official **GeeksforGeeks Java Tutorial & Interview Preparation Tracks**:

- **Basics & Environment:** JDK, JRE, JVM architecture, Bytecode format, ClassLoader hierarchy and parent delegation, `PATH` vs `CLASSPATH`, `main` method signature variants, Access modifiers.
- **Variables, Data Types & Flow:** 8 primitives, sizes, default values, Autoboxing/Unboxing hazards, implicit vs explicit type casting, IEEE 754 floating point precision with `BigDecimal`, bitwise operations (`>>` vs `>>>`), loops and modern `switch` pattern matching.
- **Object-Oriented Programming (OOP):** 4 pillars (Encapsulation, Abstraction, Inheritance, Polymorphism), constructor chaining, covariant return types, method hiding vs overriding, abstract classes vs interfaces (Java 8 default/static, Java 9 private), Diamond problem, `equals()` and `hashCode()` contract.
- **Strings & Characters:** Immutability rationale, String Constant Pool (SCP), `intern()`, `StringBuilder` vs `StringBuffer`, Java 9 Compact Strings (`byte[]` + coder), G1 string deduplication, UTF-16 surrogate pairs and emojis.
- **Arrays:** Contiguous heap layout, Array covariance vs generic invariance, `System.arraycopy`, Dual-Pivot Quicksort for primitives vs TimSort for objects.
- **Collections Framework:** `Collection` vs `Map`, `ArrayList` growth math, `LinkedList` cache miss overhead, `HashSet` backing map, `ArrayDeque` vs legacy `Stack`, fail-fast vs fail-safe, `Comparable` vs `Comparator` chaining, LRU cache via `LinkedHashMap`.
- **Exception Handling:** `Throwable` hierarchy, checked vs unchecked exceptions, `try-catch-finally` return traps, Try-with-resources and suppressed exceptions, anti-patterns, asynchronous thread exception handling, Circuit Breaker and Bulkhead patterns.
- **Multithreading & Concurrency:** Process vs Thread, 6 thread states, `start()` vs `run()`, monitor locking, `volatile` visibility vs atomicity, deadlock Coffman conditions, CAS lock-free algorithms, `LongAdder` vs `AtomicLong`, `ThreadPoolExecutor` parameters and rejection policies, `ThreadLocal` memory leaks, `CompletableFuture`, Java 21 Virtual Threads and carrier thread pinning.
- **Java 8 to 21 Evolution:** Lambdas, SAM functional interfaces, Stream pipeline `Sink` chaining and loop fusion, `parallelStream` common pool hazards, `Optional` rules, `java.time` API, Local variable type inference (`var`), Records, Sealed classes, Pattern Matching, Sequenced Collections.
- **JVM Internals & Performance:** Metaspace vs PermGen, Mark Word and object memory layout, Escape Analysis and scalar replacement, Generational GC, Parallel vs G1 vs ZGC, reference types (Strong, Soft, Weak, Phantom), thread/heap dump diagnosis using `jcmd`, `jstack`, and Eclipse MAT.
- **Design Patterns & Architecture:** Singleton (DCL with `volatile`, Bill Pugh, Enum), Factory, Builder, Decorator, Proxy (JDK vs CGLIB), Strategy, Observer, Template Method, Serialization vulnerabilities (gadget chains, RCE), and Serialization Proxy Pattern.

---

## 4. How to Use this Repository for Interview Preparation

1. **For Screening / Core Rounds (L1):**  
   Start with the [**L1 Master Guide**](./l1/README.md). Review each of the 10 L1 sections thoroughly, focusing on writing error-free code snippets and memorizing language boundary rules.
2. **For Senior / Deep Architectural Rounds (L2):**  
   Study the [**L2 Master Guide**](./l2/README.md). Focus on internal data structure mechanics, hardware memory models, GC tuning flags, and real-world production outage diagnostics.
3. **Before an Interview (1-Hour Fast Revision):**  
   Read the **Summary Cheatsheets** located at the end of each section's `README.md` for rapid recall of formulas, complexities, and gotchas.
