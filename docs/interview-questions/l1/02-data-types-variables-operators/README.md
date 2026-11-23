# Section 02: Data Types, Variables & Operators

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** 8 Primitives, Memory Representation, Autoboxing Pitfalls, Variable Scopes, Operators & Control Flow.

---

## 1. Executive Summary & Senior Engineer Perspective

In senior evaluations, questions about primitives and operators test whether you understand **hardware-level data representation, precision limits, and the cost of abstraction**. A 10-year Java developer knows that subtle bugs—such as integer overflow, floating-point rounding errors in financial transactions, and unexpected `NullPointerException`s during unboxing—can cripple production systems.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: What are the 8 primitive data types in Java, their sizes, and default values?
**Answer:**  
Java defines 8 primitive types stored directly on the stack (when local) or inlined within object memory on the heap (when fields):

| Primitive | Size | Range | Default (Field) | Wrapper Class |
|---|---|---|---|---|
| `byte` | 1 byte (8 bits) | $-128$ to $127$ ($-2^7$ to $2^7-1$) | `0` | `Byte` |
| `short` | 2 bytes (16 bits) | $-32,768$ to $32,767$ ($-2^{15}$ to $2^{15}-1$) | `0` | `Short` |
| `int` | 4 bytes (32 bits) | $-2^{31}$ to $2^{31}-1$ ($\approx \pm 2.14 \times 10^9$) | `0` | `Integer` |
| `long` | 8 bytes (64 bits) | $-2^{63}$ to $2^{63}-1$ (suffix `L`) | `0L` | `Long` |
| `float` | 4 bytes (32 bits) | IEEE 754 single precision (suffix `f`/`F`) | `0.0f` | `Float` |
| `double` | 8 bytes (64 bits) | IEEE 754 double precision (default decimal) | `0.0d` | `Double` |
| `char` | 2 bytes (16 bits) | `'\u0000'` to `'\uffff'` ($0$ to $65,535$ unsigned) | `'\u0000'` | `Character` |
| `boolean`| JVM dependent | `true` or `false` (internally treated as int/byte) | `false` | `Boolean` |

> **Key Rule on Defaults:** Only instance and static fields receive default values. **Local variables receive NO default values** and must be explicitly assigned before use; otherwise, the code fails compilation with `variable might not have been initialized`.

---

### Q2: What is Autoboxing and Unboxing, and what are the major production risks?
**Answer:**  
- **Autoboxing:** The automatic conversion that the Java compiler makes between the primitive types and their corresponding object wrapper classes (e.g., `int` to `Integer`). Under the hood, the compiler invokes `Integer.valueOf(int)`.
- **Unboxing:** The reverse process—converting an object of a wrapper type to its corresponding primitive (e.g., `Integer` to `int`). Under the hood, the compiler invokes `Integer.intValue()`.

```java
// What you write
Integer count = 100;     // Autoboxing
int value = count;       // Unboxing

// What the compiler generates
Integer count = Integer.valueOf(100);
int value = count.intValue();
```

#### Production Risks:
1. **Unboxing NullPointerException:**
   ```java
   Integer balance = null;
   int current = balance; // Throws java.lang.NullPointerException at runtime!
   ```
2. **Hidden Performance & GC Overhead in Loops:**
   ```java
   // Catastrophic memory allocation: creates 10,000,000 Integer objects!
   Long sum = 0L;
   for (long i = 0; i < 10_000_000L; i++) {
       sum += i; // unboxes sum, adds i, autoboxes new Long!
   }
   // Fix: declare 'long sum = 0L;'
   ```

---

### Q3: Explain Type Casting: Implicit (Widening) vs Explicit (Narrowing) Casting.
**Answer:**  
- **Widening (Implicit) Casting:** Converting a smaller data type to a larger data type. It is done automatically by Java because there is no loss of magnitude:
  $$\text{byte} \to \text{short} \to \text{int} \to \text{long} \to \text{float} \to \text{double}$$
  *(Note: Converting `long` to `float` or `int` to `float` can result in precision loss of least significant bits, though not magnitude).*
- **Narrowing (Explicit) Casting:** Converting a larger data type to a smaller data type. Requires explicit cast `(targetType)` and may result in data truncation or unexpected signed overflow:
  ```java
  int bigVal = 130;
  byte smallVal = (byte) bigVal;
  System.out.println(smallVal); // Prints -126 (130 - 256 due to 8-bit two's complement overflow)
  ```

---

### Q4: Local vs Instance vs Static Variables (Memory & Lifecycle)
**Answer:**  

| Characteristic | Local Variable | Instance Variable | Static (Class) Variable |
|---|---|---|---|
| **Declaration** | Inside method, constructor, or block. | Inside class, outside any method. | Declared with `static` inside class. |
| **Memory Location** | Thread Stack (inside stack frame). | Heap (part of parent object). | Metaspace (class metadata / static area). |
| **Lifecycle** | Created on block entry, destroyed on exit. | Created with `new`, destroyed when GCed. | Loaded when class loads, unloaded on class unload. |
| **Default Values** | None. Must be definitely assigned. | Yes (`0`, `false`, `null`). | Yes (`0`, `false`, `null`). |
| **Access Modifiers** | None (only `final` is allowed). | Any (`private`, `public`, etc.). | Any (`private`, `public`, etc.). |

---

### Q5: What are Short-Circuit Operators (`&&`, `||`) vs Non-Short-Circuit Operators (`&`, `|`)?
**Answer:**  
- **`&&` (Conditional AND):** Evaluates the right-hand operand **only if** the left-hand operand is `true`. If the left operand is `false`, the overall expression is already `false`, so the right side is skipped.
- **`||` (Conditional OR):** Evaluates the right-hand operand **only if** the left-hand operand is `false`. If the left operand is `true`, the overall expression is already `true`.
- **`&` and `|` on booleans:** Evaluate **both operands unconditionally**, even if the result is already determined.

```java
String text = null;
// Safe short-circuit: does NOT throw NPE
if (text != null && text.length() > 0) { ... }

// Dangerous: throws NullPointerException!
if (text != null & text.length() > 0) { ... }
```

> **Bitwise Context:** When applied to integers, `&`, `|`, and `^` perform bitwise AND, OR, and XOR operations.

---

### Q6: What is the difference between `>>` (Signed Right Shift) and `>>>` (Unsigned Right Shift)?
**Answer:**  
- **`>>` (Arithmetic / Signed Right Shift):** Shifts bits to the right and preserves the sign bit (fills the leftmost empty positions with `1` if the number is negative, `0` if positive).
- **`>>>` (Logical / Unsigned Right Shift):** Shifts bits to the right and **always fills the leftmost bits with zeros**, regardless of whether the original number was negative or positive.

```java
int x = -8;
System.out.println(x >> 1);   // -4 (sign bit preserved)
System.out.println(x >>> 1);  // 2147483644 (leftmost bit becomes 0, turns into large positive int)
```

---

### Q7: Pre-Increment (`++i`) vs Post-Increment (`i++`) in Complex Expressions
**Answer:**  
- `++i`: Increments value first, then returns the updated value.
- `i++`: Returns current value first for the expression evaluation, then increments the variable.

```java
int a = 5;
int b = a++ + ++a; 
// Step 1: a++ evaluates to 5, then a becomes 6
// Step 2: ++a increments a to 7, evaluates to 7
// b = 5 + 7 = 12, a = 7
```

---

### Q8: What is the Ternary Operator Type Promotion Trap?
**Answer:**  
The ternary operator `condition ? exp1 : exp2` performs numeric type promotion between the two branches:

```java
Object result = true ? Integer.valueOf(1) : Double.valueOf(2.0);
System.out.println(result.getClass()); // class java.lang.Double!
System.out.println(result);            // 1.0
```
Because `Integer` and `Double` share a numeric context, Java promotes the integer branch to a `Double` so both branches have a compatible return type.

---

### Q9: Why is `float` or `double` never used for financial calculations?
**Answer:**  
Floating-point numbers (`float` and `double`) follow the **IEEE 754 standard**, which represents numbers using binary fractions. Many base-10 decimal fractions cannot be represented exactly in binary (just like $1/3$ cannot be represented exactly in decimal):

```java
System.out.println(0.1 + 0.2); // Prints 0.30000000000000004 !
System.out.println(0.1 + 0.2 == 0.3); // Prints false !
```
In financial applications, rounding errors accumulate into catastrophic balance discrepancies.  
**Rule:** Always use **`BigDecimal`** (with string constructor: `new BigDecimal("0.1")`) or store currency as integral minor units (e.g., cents/paisa as `long`).

---

### Q10: How does `switch` work in Java, and what types are supported?
**Answer:**  
Historically, `switch` supported:
- `byte`, `short`, `char`, `int` (and their wrappers `Byte`, `Short`, `Character`, `Integer`).
- `enum` types (since Java 5).
- `String` (since Java 7 - uses `hashCode()` and `.equals()` under the hood).

#### Modern Switch (Java 14+ Expressions & Java 21 Pattern Matching):
```java
// Java 14+ switch expression: no break required, yields values, arrow syntax
String dayType = switch (day) {
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
    case SATURDAY, SUNDAY -> "Weekend";
    default -> throw new IllegalArgumentException("Unknown day: " + day);
};

// Java 21 Pattern Matching for switch:
static String formatValue(Object obj) {
    return switch (obj) {
        case Integer i -> String.format("int %d", i);
        case Long l    -> String.format("long %d", l);
        case String s when s.length() > 5 -> "Long string: " + s;
        case String s  -> "Short string: " + s;
        case null      -> "null value";
        default        -> obj.toString();
    };
}
```

---

## 3. Summary Cheatsheet for Revision

| Topic | Key Rule |
|---|---|
| **Primitives** | 8 primitives. Locals have no defaults; fields do. Stored in stack/inline. |
| **Autoboxing** | Beware unboxing `null` (throws NPE) and looping with wrappers (GC pressure). |
| **Financials** | Never use `double`/`float`. Always use `BigDecimal` with String constructor. |
| **Short-circuit** | `&&` and `||` stop early if left side determines result. Protects against NPE. |
| **Shifts** | `>>` preserves sign bit; `>>>` fills leftmost bits with `0`. |
