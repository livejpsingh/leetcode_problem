# Section 07: Exception Handling Basics

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** `Throwable` Hierarchy, Checked vs Unchecked, `try-catch-finally` Nuances, Try-with-Resources, Exception Anti-Patterns.

---

## 1. Executive Summary & Senior Engineer Perspective

In production systems, bad exception handling is the primary cause of swallowed bugs, unclosed sockets/file handles, and cascading system outages. A senior engineer looks for **defensive resource management (Try-with-Resources), proper exception translation with root cause chaining, and avoiding anti-patterns like catching `Throwable` or swallowing exceptions**.

```
                        java.lang.Throwable
                       /                   \
        java.lang.Error             java.lang.Exception
        (Fatal System Failures)     /                 \
        - OutOfMemoryError      Checked Exceptions   RuntimeException (Unchecked)
        - StackOverflowError    - IOException        - NullPointerException
                                - SQLException       - IllegalArgumentException
                                                     - IndexOutOfBoundsException
```

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: What is the difference between Checked and Unchecked Exceptions?
**Answer:**  

| Metric | Checked Exception | Unchecked Exception (`RuntimeException`) |
|---|---|---|
| **Hierarchy** | Direct subclass of `Exception` (excluding `RuntimeException`). | Subclass of `java.lang.RuntimeException`. |
| **Compiler Requirement** | **Mandatory:** Must be either caught in a `try-catch` or declared via `throws`. | **Optional:** Compiler does not enforce handling or declaration. |
| **Philosophy** | Represents **recoverable conditions** outside program control (e.g., missing file, network timeout). | Represents **programmer bugs / contract violations** (e.g., null dereference, bad argument, illegal state). |
| **Examples** | `IOException`, `SQLException`, `ClassNotFoundException`. | `NullPointerException`, `IllegalArgumentException`, `ArithmeticException`. |

> **10 YOE Modern Trend:** Modern frameworks (Spring, Hibernate, Jackson) avoid checked exceptions entirely because they clutter code with boilerplate and violate abstraction layers. They wrap low-level checked exceptions into meaningful unchecked domain exceptions (e.g., `DataAccessException`).

---

### Q2: What is the difference between `Error` and `Exception`?
**Answer:**  
Both extend `Throwable`:
- **`Error`:** Indicates **catastrophic, non-recoverable system conditions** occurring within the JVM itself (e.g., `OutOfMemoryError`, `StackOverflowError`, `VirtualMachineError`, `NoClassDefFoundError`). Applications should **never** attempt to catch `Error` because the JVM's internal integrity is compromised.
- **`Exception`:** Indicates conditions that a reasonable application might want to catch, handle, recover from, or report cleanly.

---

### Q3: When does the `finally` block NOT execute?
**Answer:**  
The `finally` block **always executes**, regardless of whether an exception is thrown, caught, or an early `return` statement is encountered in `try` or `catch`.

**The ONLY 4 Scenarios Where `finally` Does NOT Execute:**
1. **`System.exit(int status)` is called:** The JVM terminates immediately.
2. **Fatal JVM Crash / OS Kill:** `SIGKILL` (`kill -9`) or hardware power failure terminates the process.
3. **Infinite Loop or Deadlock in `try` block:** Execution never reaches the end of `try`.
4. **Underlying Host OS Process crash** (e.g., native C-library segmentation fault).

---

### Q4: What happens if both `try` and `finally` have `return` statements?
**Answer:**  
The `return` statement in the **`finally` block completely overrides and suppresses** any return value or pending unhandled exception from the `try` or `catch` block!

```java
public static int testReturn() {
    try {
        return 10;
    } finally {
        return 20; // DANGEROUS ANTI-PATTERN!
    }
}
// Calling testReturn() returns 20!
```
> **Warning:** Never put `return` or `throw` statements inside a `finally` block. It silently discards original exceptions and creates baffling production bugs.

---

### Q5: How does Try-With-Resources work, and what are Suppressed Exceptions?
**Answer:**  
Introduced in Java 7, **Try-With-Resources** guarantees that any resource implementing `java.lang.AutoCloseable` or `java.io.Closeable` is automatically closed at the end of the block, even if exceptions occur.

```java
// Automatic deterministic closing:
try (FileInputStream fis = new FileInputStream("data.txt");
     BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
    System.out.println(br.readLine());
} // Both br and fis are closed automatically in REVERSE order of declaration!
```

#### Suppressed Exceptions:
In traditional `try-finally`, if both the `try` block and the `finally.close()` throw exceptions, the exception in `finally` completely masks and obliterates the original exception in `try`.  
In **Try-With-Resources**, the exception thrown from the `try` block is the **primary exception** thrown to the caller, while any subsequent exception thrown during closing is attached as a **Suppressed Exception** (retrievable via `e.getSuppressed()`).

---

### Q6: Multi-Catch Block Rules (Java 7+)
**Answer:**  
You can catch multiple distinct exception types in a single `catch` block using the pipe (`|`) operator:

```java
try {
    executeDbOperation();
} catch (SQLException | IOException ex) {
    logger.error("IO or DB operation failed", ex);
    // Note: The exception variable 'ex' is implicitly FINAL and cannot be reassigned!
}
```
**Rule:** Exceptions combined with `|` must **not** have an inheritance relationship (e.g., `catch (IOException | FileNotFoundException e)` fails compilation because `FileNotFoundException` is already a subclass of `IOException`).

---

### Q7: Top 4 Production Exception Handling Anti-Patterns
**Answer:**  

1. **Swallowing Exceptions:**
   ```java
   // WORST OFFENSE IN JAVA: Bug vanishes without a trace!
   try { ... } catch (Exception e) {} 
   ```
2. **Catching `Throwable` or `Exception` indiscriminately:**  
   Catches and masks runtime errors and system errors that should have crashed or alerted monitoring systems.
3. **Log and Rethrow Anti-Pattern:**
   ```java
   try { ... } 
   catch (IOException e) {
       logger.error("Error occurred", e);
       throw e; // Results in duplicated, multi-page stack traces in production logs!
   }
   ```
   *Rule: Either handle and log it, OR wrap/rethrow it. Never do both.*
4. **Losing the Root Cause:**
   ```java
   try { ... } 
   catch (SQLException e) {
       throw new ServiceException("DB failed"); // Original cause is lost!
   }
   // FIX: Always chain cause: new ServiceException("DB failed", e);
   ```

---

## 3. Summary Cheatsheet for Revision

| Concept | Key Takeaway |
|---|---|
| **Checked vs Unchecked** | Checked must be caught/declared (`IOException`); unchecked extends `RuntimeException`. |
| **`finally` Execution** | Always runs unless `System.exit()`, JVM crash, or infinite loop occurs. |
| **Try-with-resources** | Closes `AutoCloseable` resources automatically; preserves primary exceptions and attaches suppressed ones. |
| **Exception Chaining** | Always pass the original exception to the constructor: `new CustomException(msg, cause)`. |
