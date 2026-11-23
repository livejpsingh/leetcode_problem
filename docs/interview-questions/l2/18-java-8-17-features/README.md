# Section 18: Java 8 to 21 Modern Features & Language Evolution

> **Level:** L2 (Senior / Deep Technical Architecture)  
> **Target Experience:** 5–10+ Years (Senior Engineer / Tech Lead / Architect Perspective)  
> **Key Focus:** Stream Pipeline Internals (Spliterator & Sink chaining), Parallel Stream Thread Starvation, Records, Sealed Classes, Pattern Matching, Sequenced Collections (Java 21).

---

## 1. Executive Summary & Senior Engineer Perspective

Java has evolved from an old-school verbose language into a modern, expressive, highly optimized language with algebraic data types, pattern matching, records, and virtual threads. A senior developer must demonstrate **mastery of modern Java versions (Java 8, 11, 17, and 21 LTS releases)**, avoiding legacy idioms and writing expressive, safe, high-throughput code.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: How does the Stream API work under the hood? (Spliterator & Sink Chaining)
**Answer:**  

1. **Pipeline Representation (`ReferencePipeline`):**  
   Each stream operation creates a new stage in a doubly linked list of pipeline stages:
   $$\text{Head (Collection)} \to \text{Stage 1 (filter)} \to \text{Stage 2 (map)} \to \text{Terminal (collect)}$$
2. **`Sink<T>` Chaining:**  
   When the terminal operation is invoked, the JVM constructs a chain of `Sink` objects backwards from the terminal sink to the source. Each `Sink` implements:
   - `begin(long size)`: Prepares the stage.
   - `accept(T t)`: Pushes a single element downstream.
   - `end()`: Signals completion.
   - `cancellationRequested()`: Enables short-circuiting (`limit()`, `findFirst()`).
3. **Loop Fusion (Single Pass Execution):**  
   Elements are **not** collected into intermediate arrays between stages! An element passes through `filter()` and immediately into `map()` and the collector in a single CPU cache-friendly iteration loop.
4. **`Spliterator<T>`:**  
   The underlying iteration engine. It supports `tryAdvance(Consumer)` (sequential traversal) and `trySplit()` (splits chunks of data recursively for parallel processing).

---

### Q2: Why is `parallelStream()` dangerous with blocking I/O?
**Answer:**  
By default, all `parallelStream()` operations across the **entire JVM** share a single, JVM-wide thread pool:
$$\textbf{ForkJoinPool.commonPool()}$$
The size of this common pool is limited to:
$$\text{Runtime.getRuntime().availableProcessors()} - 1$$

#### The Thread Starvation Disaster:
If you execute a `parallelStream()` that performs **blocking network I/O** (e.g., calling an external REST API or database):
```java
// SEVERE SYSTEM OUTAGE HAZARD:
userList.parallelStream().forEach(user -> {
    restClient.callThirdPartyApi(user); // Blocks for 2 seconds!
});
```
All worker threads in `ForkJoinPool.commonPool()` become blocked waiting for network sockets. **Every other parallel stream in the entire application freezes**, stalling background tasks and degrading response times across the entire JVM!

> **Rule for Senior Engineers:** Never perform blocking I/O inside `parallelStream()`. Use parallel streams strictly for **CPU-bound in-memory computations**, or use a dedicated custom `ForkJoinPool` or `CompletableFuture` with a specialized I/O executor.

---

### Q3: Advanced `Collectors`: `groupingBy`, `partitioningBy`, and Multi-Level Aggregation
**Answer:**  

```java
// Grouping employees by department, calculating average salary per department:
Map<Department, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.averagingDouble(Employee::getSalary)
    ));

// Two-level grouping: Department -> Job Title -> List of Names
Map<Department, Map<String, List<String>>> hierarchy = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.groupingBy(
            Employee::getTitle,
            Collectors.mapping(Employee::getName, Collectors.toList())
        )
    ));

// Partitioning (splits into exactly two groups: true and false):
Map<Boolean, List<Employee>> seniorVsJunior = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.getYearsOfExperience() >= 5));
```

---

### Q4: Java Records (Java 16+): Canonical vs Compact Constructors
**Answer:**  
A **`record`** is an immutable, data-carrier class where the compiler automatically generates:
- `private final` fields for all components.
- Public read accessors (e.g., `user.name()`, not `getName()`).
- Correct `equals()`, `hashCode()`, and `toString()` based on all components.

```java
public record Transaction(String id, BigDecimal amount, Instant timestamp) {
    // Compact Constructor: Used for validation/normalization without repeating field assignments
    public Transaction {
        Objects.requireNonNull(id, "ID cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        // Compiler automatically assigns this.id = id; this.amount = amount; etc.
    }
}
```

---

### Q5: Sealed Classes and Interfaces (Java 17+)
**Answer:**  
Historically, a class was either `final` (cannot be extended by anyone) or open (could be extended by any class anywhere).  
**Sealed Classes** allow you to restrict which exact subclasses are permitted to extend an interface or class, enabling **Algebraic Data Types (Sum Types)**:

```java
public sealed interface PaymentMethod 
    permits CreditCardPayment, UPIPayment, CryptoPayment {}

public final class CreditCardPayment implements PaymentMethod { ... }
public final class UPIPayment implements PaymentMethod { ... }
public final class CryptoPayment implements PaymentMethod { ... }
```
**Benefits:**
- Enforces strict domain boundaries.
- **Exhaustiveness Checking in Pattern Matching:** When switching over a sealed interface, the compiler knows all possible subtypes and does **not require a `default` case**!

---

### Q6: Sequenced Collections (Java 21)
**Answer:**  
Prior to Java 21, Java lacked a unified collection type for elements arranged in a defined encounter order (`List` has index 0, `Deque` has `getFirst()`, `LinkedHashSet` has an encounter order but no `getFirst()`, and `SortedSet` has `first()`).

Java 21 introduced **`SequencedCollection`**, **`SequencedSet`**, and **`SequencedMap`**:
```java
SequencedCollection<String> seq = new ArrayList<>();
seq.addFirst("front");
seq.addLast("back");

String first = seq.getFirst();
String last = seq.getLast();

// Clean reverse-order view:
SequencedCollection<String> reversed = seq.reversed();
```

---

## 3. Summary Cheatsheet for Revision

| Feature | Version | Architectural Advantage |
|---|---|---|
| **Loop Fusion** | Java 8 | Streams fuse intermediate operations into a single pass; no intermediate collections created. |
| **`parallelStream`** | Java 8 | Uses shared `commonPool()`. Strictly avoid blocking I/O to prevent thread starvation. |
| **Records** | Java 16 | Boilerplate-free immutable DTOs with transparent component accessors. |
| **Sealed Types** | Java 17 | Restricts inheritance to explicit permitted classes (`permits`); enables exhaustive pattern matching. |
| **Sequenced Collections** | Java 21 | Unified `addFirst()`, `addLast()`, `reversed()` across List, Deque, LinkedHashSet. |
