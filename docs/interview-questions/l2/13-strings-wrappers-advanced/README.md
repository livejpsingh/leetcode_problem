# Section 13: Strings & Wrappers Advanced Internals

> **Level:** L2 (Senior / Deep Technical Architecture)  
> **Target Experience:** 5–10+ Years (Senior Engineer / Tech Lead / Architect Perspective)  
> **Key Focus:** Compact Strings (`byte[]` vs `char[]`), G1 String Deduplication, Unicode Surrogate Pairs, `BigDecimal` Scale & Rounding Modes, Regex Optimization.

---

## 1. Executive Summary & Senior Engineer Perspective

String and numerical precision issues directly impact heap sizing, garbage collection frequency, and monetary accuracy. A senior Java engineer must know **how Java 9+ cut heap usage in half with Compact Strings, how G1 GC deduplicates identical strings transparently, and how to perform exact arithmetic without non-terminating decimal expansion crashes**.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: How did String internals change from Java 8 to Java 9+ (Compact Strings)?
**Answer:**  

- **Java 8 and earlier:**  
  Strings were backed by a `char[]` array:
  ```java
  private final char value[]; // Each char consumes 2 bytes (16 bits)
  ```
  Analysis across millions of heap dumps by the HotSpot team revealed that **over $75\%$ of Strings in enterprise applications contained exclusively ASCII / Latin-1 characters**, where the upper 8 bits of every single character were wasted zeros (`0x00`).
- **Java 9+ (JEP 254: Compact Strings):**  
  Strings are now backed by a `byte[]` array and an encoding flag (`coder` byte):
  ```java
  private final byte[] value;
  private final byte coder; // 0 for LATIN1 (1 byte per char), 1 for UTF16 (2 bytes per char)
  ```

#### Impact:
- If a string contains only ISO-8859-1 / Latin-1 characters, it consumes **half the heap memory** compared to Java 8!
- Zero code changes required; enabled by default (`-XX:+CompactStrings`).

---

### Q2: What is G1 GC String Deduplication (`-XX:+UseStringDeduplication`)?
**Answer:**  
In high-volume applications, many distinct `String` objects on the heap have identical character contents. While `String.intern()` solves this, it requires manual code modification and pollutes the String Constant Pool.

#### How G1 String Deduplication Works:
1. When enabled via `-XX:+UseG1GC -XX:+UseStringDeduplication`, G1 GC inspects strings that survive minor GC collections.
2. It computes their hash and checks a global candidate table.
3. If it discovers two distinct `String` objects with identical `byte[]` values, **it alters the pointer of String B to share the backing `byte[]` array of String A**, and the duplicate array is garbage collected!
4. The two `String` object instances remain distinct (preserving identity `s1 != s2`), but the heavy memory payload is shared transparently.

---

### Q3: Why does `str.length()` return 2 for a single emoji? (Unicode & Surrogate Pairs)
**Answer:**  
Java's `char` is a 16-bit type based on **UTF-16**.  
- The **Basic Multilingual Plane (BMP)** covers code points from `U+0000` to `U+FFFF`, fitting within a single 16-bit `char`.
- Supplementary characters (such as emojis, mathematical symbols, and historical scripts) have code points from `U+10000` to `U+10FFFF`. They **cannot fit in a single 16-bit `char`**.
- Java represents supplementary characters using a **Surrogate Pair** (two 16-bit `char`s: a High Surrogate and a Low Surrogate).

```java
String emoji = "🚀"; // Rocket emoji (U+1F680)

System.out.println(emoji.length());                // Prints 2! (Counts UTF-16 code units)
System.out.println(emoji.codePointCount(0, emoji.length())); // Prints 1! (True character count)
```
> **Rule for Senior Engineers:** Never use `length()` or `charAt()` when processing international text or emojis. Always use **`codePointAt()`** and **`codePoints()`** stream APIs.

---

### Q4: `BigDecimal` Deep Dive: Scale, Precision, and Division Traps
**Answer:**  
A `BigDecimal` consists of an arbitrary precision integer **`unscaledValue`** (`BigInteger`) and a 32-bit integer **`scale`**:
$$\text{Value} = \text{unscaledValue} \times 10^{-\text{scale}}$$

```java
BigDecimal bd = new BigDecimal("123.45"); // unscaledValue = 12345, scale = 2
```

#### Trap 1: Double Constructor vs String Constructor
```java
// DANGEROUS BUG:
BigDecimal b1 = new BigDecimal(0.1); 
// b1 is actually: 0.1000000000000000055511151231257827021181583404541015625

// CORRECT PRODUCTION PRACTICE:
BigDecimal b2 = new BigDecimal("0.1"); // or BigDecimal.valueOf(0.1)
```

#### Trap 2: Non-Terminating Decimal Expansion Crash
```java
BigDecimal a = new BigDecimal("1");
BigDecimal b = new BigDecimal("3");

// CATASTROPHIC RUNTIME CRASH:
// a.divide(b); // Throws java.lang.ArithmeticException: Non-terminating decimal expansion!

// SAFE FIX: Always specify scale and RoundingMode!
BigDecimal c = a.divide(b, 4, RoundingMode.HALF_UP); // 0.3333
```

---

### Q5: High-Performance Regex Optimization: Pattern Compilation Overhead
**Answer:**  
Methods like `String.matches(regex)` or `String.split(regex)` compile the regex into a `java.util.regex.Pattern` object **on every single invocation**.

```java
// ANTI-PATTERN in high-throughput API:
public boolean validateEmail(String email) {
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$"); // Recompiles DFA regex on every call!
}

// PRODUCTION BEST PRACTICE: Precompile and reuse!
private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

public boolean validateEmail(String email) {
    return EMAIL_PATTERN.matcher(email).matches();
}
```

---

## 3. Summary Cheatsheet for Revision

| Topic | Critical Insight |
|---|---|
| **Compact Strings** | Java 9 uses `byte[]` + `coder` flag. Halves string memory for Latin-1 text. |
| **String Deduplication** | G1 GC shares duplicate `byte[]` arrays across distinct String instances without code changes. |
| **Unicode Code Points** | Emojis consume 2 `char` code units (surrogate pairs). Use `codePointCount()`. |
| **`BigDecimal`** | Never use double constructor; always supply `RoundingMode` to `divide()` to avoid `ArithmeticException`. |
| **Regex** | Cache `Pattern.compile()` as `static final` constants to avoid heavy recompilation. |
