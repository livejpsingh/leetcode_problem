# Section 09: Java 8 Basics

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** Lambda Expressions, Functional Interfaces (`java.util.function`), Stream API Fundamentals, `Optional`, Modern Date/Time API (`java.time`).

---

## 1. Executive Summary & Senior Engineer Perspective

Java 8 introduced functional programming paradigms into an object-oriented language. Senior interviewers evaluate whether candidates use lambdas and Streams **idiomatically, declaratively, and cleanly**, avoiding side effects, mutable state capturing, and inefficient intermediate stream pipelines.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: What is a Functional Interface, and what is the Single Abstract Method (SAM) rule?
**Answer:**  
A **Functional Interface** is an interface that contains **exactly one abstract method**.
- It can contain any number of `default` methods, `static` methods, and public methods declared in `java.lang.Object` (e.g., `equals`, `toString`).
- The `@FunctionalInterface` annotation is optional, but strongly recommended because it instructs the compiler to generate an error if more than one abstract method is present.
- Lambdas and method references can be assigned to any variable whose target type is a functional interface.

```java
@FunctionalInterface
public interface Transformer<T, R> {
    R transform(T input); // The single abstract method

    default void log(T input) {
        System.out.println("Processing: " + input);
    }
}
```

---

### Q2: What are the 4 Core Built-In Functional Interfaces in `java.util.function`?
**Answer:**  

| Functional Interface | Method Signature | Functionality | Real-World Example |
|---|---|---|---|
| **`Predicate<T>`** | `boolean test(T t)` | Takes an argument, evaluates a boolean condition. | `filter(e -> e.getSalary() > 50000)` |
| **`Function<T, R>`** | `R apply(T t)` | Takes an input of type `T`, transforms it, returns `R`. | `map(User::getEmail)` |
| **`Consumer<T>`** | `void accept(T t)` | Consumes an argument, performs a side-effect, returns nothing. | `forEach(System.out::println)` |
| **`Supplier<T>`** | `T get()` | Takes no argument, generates/supplies an instance of `T`. | `Optional.orElseGet(() -> fetchDefault())` |

#### Common Bi- and Primitive Variations:
- `BiPredicate<T, U>`, `BiFunction<T, U, R>`, `BiConsumer<T, U>`
- `IntPredicate`, `LongFunction<R>`, `DoubleConsumer` (avoid autoboxing primitive numbers!).

---

### Q3: What can Lambdas capture? What is "Effectively Final"?
**Answer:**  
A lambda expression can capture:
- Instance variables and static class variables (can read and mutate them).
- Local variables from the enclosing method **ONLY IF they are `final` or "effectively final"**.

**What does "Effectively Final" mean?**  
A local variable whose value is never assigned or changed after initialization, even if the `final` keyword is omitted.

```java
int port = 8080; // effectively final
Runnable r1 = () -> System.out.println("Port: " + port); // Valid

int counter = 0;
counter++; // Mutated!
// Compilation error: local variables referenced from a lambda must be final or effectively final!
Runnable r2 = () -> System.out.println("Counter: " + counter); 
```
**Why this rule?**  
Local variables live on the thread stack. Lambdas may execute asynchronously on a different thread after the stack frame has been destroyed. Java captures local variables by **copying their value**. If the original variable could be mutated, the copy and original would diverge, creating race conditions.

---

### Q4: What is the Stream API, and what is the difference between Intermediate and Terminal Operations?
**Answer:**  
A **Stream** is a sequence of elements supporting sequential and parallel aggregate operations. Streams do **not** store elements (they are views over data structures like Collections) and do not modify the underlying source.

```
Source (Collection) ---> [ Intermediate Ops (Lazy) ] ---> [ Terminal Op (Eager) ]
                          - filter()                       - collect()
                          - map()                          - forEach()
                          - sorted()                       - reduce()
```

#### Intermediate Operations:
- **Lazy Evaluation:** Intermediate operations are not executed when declared. They build a query execution pipeline.
- Return a new `Stream<T>`.
- Examples: `filter()`, `map()`, `flatMap()`, `distinct()`, `sorted()`, `limit()`, `skip()`, `peek()`.

#### Terminal Operations:
- **Eager Execution:** Triggers the traversal of the stream pipeline, consumes the elements, and produces a final result (or side effect).
- **A Stream cannot be reused once a terminal operation is called** (calling another operation throws `IllegalStateException: stream has already been operated upon or closed`).
- Examples: `collect()`, `reduce()`, `count()`, `forEach()`, `findFirst()`, `findAny()`, `anyMatch()`, `allMatch()`.

---

### Q5: `map()` vs `flatMap()` (Critical Difference)
**Answer:**  
- **`map(Function<T, R>)`:** Transforms each element of type `T` into a single element of type `R` (1-to-1 mapping).  
  Produces: `Stream<R>`.
- **`flatMap(Function<T, Stream<R>>)`:** Transforms each element of type `T` into a `Stream<R>`, and then **flattens all inner streams into a single consolidated output stream** (1-to-Many mapping).  
  Produces: `Stream<R>`.

```java
List<List<String>> nested = List.of(
    List.of("A", "B"),
    List.of("C", "D")
);

// map returns Stream<List<String>>
List<List<String>> mapResult = nested.stream().map(l -> l).toList();

// flatMap flattens into Stream<String>
List<String> flatResult = nested.stream()
                                .flatMap(Collection::stream)
                                .toList(); // ["A", "B", "C", "D"]
```

---

### Q6: How does `Optional<T>` work and how should it be used properly?
**Answer:**  
`Optional<T>` is a container object that may or may not contain a non-null value, designed specifically as a **method return type** to explicitly communicate the possibility of an absent result without returning `null`.

#### Proper Usage:
```java
public Optional<User> findUserById(String id) {
    User user = database.query(id);
    return Optional.ofNullable(user);
}

// Consuming safely:
String email = findUserById("101")
    .map(User::getEmail)
    .orElse("default@domain.com");

// Or throwing domain exception:
User user = findUserById("101")
    .orElseThrow(() -> new UserNotFoundException("User 101 not found"));
```

#### `Optional` Anti-Patterns:
1. **Calling `.get()` without checking `.isPresent()`:** Defeats the entire purpose of `Optional`, throwing `NoSuchElementException`.
2. **Using `Optional` as method parameters or class fields:** `Optional` is not `Serializable` and adds object wrapping overhead. Use only for method return types.
3. **`orElse(expensiveComputation())` vs `orElseGet(() -> expensiveComputation())`:**  
   `orElse()` evaluates its argument **eagerly**, even if the Optional is present! Always use `orElseGet()` for dynamic computations to evaluate lazily.

---

### Q7: Why did Java 8 replace `java.util.Date` and `Calendar` with `java.time`?
**Answer:**  
The legacy `Date` and `Calendar` classes suffered from severe design flaws:
1. **Mutability:** `java.util.Date` is mutable, making it a major concurrency and thread-safety liability.
2. **Confusing Month Indexing:** Months were 0-indexed (January was `0`), causing endless off-by-one bugs.
3. **Cluttered Responsibilities:** `Date` represented both an instant in time and a date without timezone clarity.

#### The `java.time` (JSR-310) Standard:
- **100% Immutable and Thread-Safe.**
- Clear domain types:
  - `LocalDate`: Date only (`2026-11-23`), no time, no timezone.
  - `LocalTime`: Time only (`15:30:00`).
  - `LocalDateTime`: Date and time without timezone offset.
  - `ZonedDateTime`: Date and time with timezone (e.g., `Asia/Kolkata`).
  - `Instant`: Machine timestamp (epoch seconds and nanoseconds, UTC).
  - `Duration` (time-based difference: hours/minutes) vs `Period` (date-based difference: years/months/days).

---

## 3. Summary Cheatsheet for Revision

| Concept | Key Takeaway |
|---|---|
| **Functional Interface** | Exactly one abstract method (SAM). Annotated with `@FunctionalInterface`. |
| **Stream Operations** | Intermediate (lazy: `filter`, `map`, `sorted`); Terminal (eager: `collect`, `reduce`). |
| **`flatMap`** | Transforms and flattens nested streams into a single unified stream. |
| **`Optional`** | Use as method return type; use `orElseGet()` for lazy evaluation; avoid `get()`. |
| **`java.time`** | Modern, immutable, thread-safe date/time API replacing legacy `Date`/`Calendar`. |
