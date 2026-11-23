# Section 04: Strings & Wrapper Classes

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** String Immutability, String Constant Pool (SCP), `intern()`, `StringBuilder` vs `StringBuffer`, `IntegerCache`, Autoboxing Pitfalls.

---

## 1. Executive Summary & Senior Engineer Perspective

Strings are the most widely used data structure in enterprise Java. Understanding String internals separates junior developers from senior engineers. In high-throughput systems, improper String concatenation creates millions of short-lived objects, triggering GC spikes and CPU throttling.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: Why is `String` immutable in Java?
**Answer:**  
In Java, once a `String` object is created, its internal character sequence can never be altered. Any method modifying a string (e.g., `toLowerCase()`, `concat()`) returns a brand new `String` object.

```
       String s1 = "Java";
       String s2 = s1.concat(" 21");
       
       s1 ---> [ "Java" ]        (unchanged on Heap / SCP)
       s2 ---> [ "Java 21" ]     (new object created on Heap)
```

#### The 4 Critical Reasons Why String is Immutable:
1. **String Constant Pool (SCP) Optimization:**  
   Multiple variables referring to `"hello"` share the exact same physical memory address in the SCP. If strings were mutable, changing the value through one reference would silently corrupt all other references across the application.
2. **Security:**  
   Strings are ubiquitous in sensitive parameters: database connection URLs, usernames, passwords, file system paths, network sockets, and class loading names. If `String` were mutable, a malicious thread could alter the file path after security checks have passed (Time-of-Check to Time-of-Use vulnerability).
3. **Thread Safety:**  
   Because state never changes, Strings are inherently thread-safe without requiring synchronization blocks or volatile variables. They can be shared freely across concurrent threads.
4. **Caching Hash Code for Collection Performance:**  
   `String` caches its `hashCode()` in a private field `hash`. Because the characters cannot change, the hash code is calculated lazily once and reused. This makes `String` the ideal key in hash-based collections (`HashMap`, `HashSet`).

---

### Q2: What is the String Constant Pool (SCP), and how does `new String()` differ from string literals?
**Answer:**  
- **String Literal (`String s = "apple";`):**  
  The JVM checks the String Constant Pool (located within the Java Heap). If `"apple"` already exists in the pool, it returns the existing reference. If not, it creates a new `"apple"` object in the SCP and returns that reference. **Creates 0 or 1 object.**
- **`new String("apple")`:**  
  The `new` operator **always forces the creation of a brand new object on the regular Java Heap**, outside the pool. In addition, the literal `"apple"` is interned into the SCP if not already present. **Creates 1 or 2 objects.**

```java
String s1 = "apple";                  // 1 object created in SCP
String s2 = "apple";                  // 0 objects created; points to s1 in SCP
String s3 = new String("apple");      // 1 new object created on Heap

System.out.println(s1 == s2); // true  (same SCP reference)
System.out.println(s1 == s3); // false (s3 is a separate Heap object)
System.out.println(s1.equals(s3)); // true (same character content)
```

---

### Q3: What does `String.intern()` do?
**Answer:**  
`s.intern()` queries the String Constant Pool:
- If the pool already contains a string equal to `s` (according to `.equals()`), it returns the reference from the pool.
- If not, it adds `s` to the pool and returns its reference.

```java
String s1 = new String("cloud");
String s2 = s1.intern();
String s3 = "cloud";

System.out.println(s1 == s2); // false
System.out.println(s2 == s3); // true (both reference the SCP entry)
```

> **10 YOE Production Warning:** Do not call `.intern()` indiscriminately on untrusted user inputs (e.g., millions of unique IDs). The SCP lives in Heap memory and contains a hashtable with fixed buckets; overloading it causes native overhead and slows down pool lookups.

---

### Q4: `String` vs `StringBuilder` vs `StringBuffer`
**Answer:**  

| Feature | `String` | `StringBuilder` | `StringBuffer` |
|---|---|---|---|
| **Mutability** | **Immutable** | **Mutable** | **Mutable** |
| **Thread Safety** | **Thread-safe** (read-only) | **Not thread-safe** | **Thread-safe** (synchronized methods) |
| **Performance** | Slow for modifications | **Fastest** (no lock contention) | Slower due to lock overhead |
| **Introduced In** | JDK 1.0 | JDK 1.5 | JDK 1.0 |
| **Buffer Growth** | N/A (creates new strings) | `(oldCapacity * 2) + 2` | `(oldCapacity * 2) + 2` |

#### The String Concatenation In Loops Anti-Pattern:
```java
// SEVERE ANTI-PATTERN: Creates O(N^2) memory copies!
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i; // Each iteration allocates a new StringBuilder and String!
}

// PRODUCTION BEST PRACTICE:
StringBuilder sb = new StringBuilder(1000);
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
String result = sb.toString();
```

---

### Q5: What is the `IntegerCache` and how does it affect equality?
**Answer:**  
Java caches `Integer` objects in the range **`-128` to `127`** (the Flyweight pattern) via an internal static cache class `java.lang.Integer.IntegerCache`.

When you autobox or call `Integer.valueOf(int)` within this range, Java returns the cached pre-instantiated instance:

```java
Integer a = 127;
Integer b = 127;
System.out.println(a == b); // true (both point to cached instance!)

Integer c = 128;
Integer d = 128;
System.out.println(c == d); // FALSE! (outside cache range; two distinct Heap objects)
System.out.println(c.equals(d)); // true (compares values)
```

> **Pro Tip:** The upper limit of the cache can be tuned using the JVM parameter `-XX:AutoBoxCacheMax=<size>`, but the lower limit is fixed at `-128`. Always use `.equals()` to compare wrapper objects!

---

### Q6: `trim()` vs `strip()` (Java 11)
**Answer:**  
- `trim()`: Trims characters with ASCII codes $\le \text{U+0020}$ (space). It fails to strip non-breaking spaces or Unicode whitespace (such as em-space `\u2001`).
- `strip()`: Introduced in Java 11. It is **Unicode-aware** and strips all characters that return `true` for `Character.isWhitespace(codePoint)`. Always prefer `strip()` in modern Java.

---

### Q7: `replace()` vs `replaceAll()`
**Answer:**  
- `str.replace(CharSequence target, CharSequence replacement)`: Performs exact literal sequence replacement (no regex parsing, fast).
- `str.replaceAll(String regex, String replacement)`: Treats the first argument as a **Regular Expression**. If you only need literal replacement, calling `replaceAll` compiles a regex engine under the hood, wasting significant CPU cycles.

---

## 3. Summary Cheatsheet for Revision

| Question | Quick Answer |
|---|---|
| **String Mutability** | Immutable for security, thread safety, SCP pooling, and hash code caching. |
| **`new String("x")`** | Always creates a new object on Heap; creates SCP object only if not already present. |
| **`StringBuilder`** | Mutable, unsynchronized, fastest for single-threaded mutation. |
| **`IntegerCache`** | Caches `-128` to `127`. Never use `==` for wrapper comparisons; always use `.equals()`. |
