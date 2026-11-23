# L1 Level Java Developer Interview Guide

> **Target Profile:** 0–4 Years of Experience  
> **Evaluation Benchmark:** Evaluated from a 10+ YOE Senior Staff / Principal Engineer Perspective  
> **Coverage:** Foundational Java, Core Syntax, Language Mechanics, Collection Basics, OOP, and Runtime Principles.

---

## 1. L1 Interview Competency Matrix

| Competency Area | What the Interviewer Assesses | Target Depth |
|---|---|---|
| **Java Platform & Environment** | JVM vs JRE vs JDK, ClassLoader parent delegation, compilation and execution stages. | Internal architecture & lifecycle |
| **Data Types & Variables** | 8 primitives, sizes, IEEE 754 precision issues, Autoboxing/Unboxing NPE traps, operators. | Hardware & memory representation |
| **Object-Oriented Design** | 4 pillars, method overloading vs overriding rules, abstract classes vs interfaces, `equals()`/`hashCode()`. | Contract-based design & invariants |
| **Strings & Wrappers** | Immutability reasons, String Constant Pool, `StringBuilder` vs `StringBuffer`, `IntegerCache`. | Memory layout & GC optimization |
| **Arrays** | Object representation, array covariance traps, `System.arraycopy`, sorting mechanics. | Algorithmic complexity & safety |
| **Collections Framework** | Hierarchy, `ArrayList` vs `LinkedList`, `HashSet` backing, fail-fast mechanics, `Comparable`/`Comparator`. | Practical data structure selection |
| **Exception Handling** | Checked vs unchecked, `Throwable` hierarchy, Try-With-Resources, multi-catch, anti-patterns. | Fault tolerance & clean cleanup |
| **Multithreading Basics** | Thread lifecycle, `Runnable` vs `Callable`, `start()` vs `run()`, monitor locking, `volatile`, deadlock. | Concurrency fundamentals |
| **Java 8 Essentials** | Lambdas, `@FunctionalInterface`, built-in functional interfaces, Stream pipelines, `Optional`, `java.time`. | Idiomatic functional programming |
| **Code & Output Puzzles** | Initialization sequence, overload resolution with `null`, precedence, classic coding questions. | JLS specification adherence |

---

## 2. L1 Topic Navigation

Each section below contains comprehensive, production-grade interview questions, architectural explanations, code snippets, gotchas, and 10-YOE tips:

1. [**Section 01: Java Basics & Environment**](./01-java-basics-environment/)
   - JVM architecture, Bytecode, ClassLoaders, `main()` signature, Access modifiers, `PATH` vs `CLASSPATH`.
2. [**Section 02: Data Types, Variables & Operators**](./02-data-types-variables-operators/)
   - 8 primitives, Autoboxing/Unboxing gotchas, IEEE 754 floating-point traps, Bitwise operators, Switch evolution.
3. [**Section 03: OOP Concepts**](./03-oop-concepts/)
   - Encapsulation, Abstraction, Inheritance, Polymorphism, Overloading vs Overriding, `equals`/`hashCode` contract, Interfaces.
4. [**Section 04: Strings & Wrapper Classes**](./04-strings-wrapper-classes/)
   - Why String is immutable, String Constant Pool, `intern()`, `StringBuilder`, `IntegerCache` flyweight pattern.
5. [**Section 05: Arrays**](./05-arrays/)
   - Contiguous memory allocation, Array covariance hazards, Jagged arrays, `System.arraycopy`, Dual-Pivot Quicksort.
6. [**Section 06: Collections Framework Basics**](./06-collections-framework-basics/)
   - `Collection` hierarchy, `ArrayList` vs `LinkedList`, `HashSet` internal `HashMap`, Fail-fast iterators, `Comparator`.
7. [**Section 07: Exception Handling Basics**](./07-exception-handling-basics/)
   - `Throwable` hierarchy, Checked vs Unchecked, `finally` execution guarantees, Try-with-resources, Anti-patterns.
8. [**Section 08: Multithreading Basics**](./08-multithreading-basics/)
   - Thread states, `Runnable` vs `Callable`, `sleep()` vs `wait()`, `synchronized` monitors, `volatile` visibility, Deadlocks.
9. [**Section 09: Java 8 Basics**](./09-java-8-basics/)
   - Lambdas, Functional interfaces (`Predicate`, `Function`, etc.), Stream operations, `Optional` rules, `java.time`.
10. [**Section 10: Basic Programs & Output Puzzles**](./10-basic-programs-output/)
    - Static initialization hierarchy, Overload resolution with `null`, Type promotion in ternary, Anagram & Fibonacci programs.

---

## 3. Fast-Track L1 Revision Cheatsheet

- **Execution:** `.java` $\to$ `javac` $\to$ `.class` bytecode $\to$ ClassLoader (Parent delegation) $\to$ Bytecode Verifier $\to$ Execution Engine (Interpreter + C1/C2 JIT).
- **Primitives vs References:** Primitives stored directly; references point to Heap objects. Wrapper comparison must use `.equals()`.
- **String Immutability:** Enables String Constant Pool, thread safety, security parameters, and cached hash code.
- **Overriding Rules:** Same signature, covariant return permitted, cannot reduce visibility, cannot throw broader checked exceptions.
- **Collection Growth:** `ArrayList` grows by $50\%$ (`oldCap + (oldCap >> 1)`). Backed by contiguous array.
- **Fail-Fast:** Iterators check `modCount == expectedModCount`. Mutating without `iterator.remove()` throws `ConcurrentModificationException`.
- **Volatile:** Guarantees CPU cache visibility and prevents reordering. Does NOT guarantee atomicity for compound operations like `count++`.
- **Streams:** Lazy intermediate operations; eager terminal operations; streams cannot be reused after terminal consumption.
