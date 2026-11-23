# Section 10: Basic Programs & Output Puzzles

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** Tricky Execution Puzzles, Static Initialization Hierarchy, Method Overload Resolution Rules, Classic Core Algorithms.

---

## 1. Executive Summary & Senior Engineer Perspective

Output questions and tricky code snippets test whether you understand the **Java Language Specification (JLS) exact compiler rules**, rather than guessing based on intuition. In L1 screening rounds, candidates are frequently given these exact snippets to test compile-time vs runtime dispatch, evaluation order, and string/integer pooling.

---

## 2. Classic Output Puzzles & Exact Rules

### Puzzle 1: Static and Instance Initialization Hierarchy
**Question: What is the exact execution output of the following code?**

```java
class SuperClass {
    static { System.out.println("1. Super Static Block"); }
    { System.out.println("3. Super Instance Block"); }
    public SuperClass() { System.out.println("4. Super Constructor"); }
}

class SubClass extends SuperClass {
    static { System.out.println("2. Sub Static Block"); }
    { System.out.println("5. Sub Instance Block"); }
    public SubClass() { System.out.println("6. Sub Constructor"); }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("--- First Instantiation ---");
        new SubClass();
        System.out.println("--- Second Instantiation ---");
        new SubClass();
    }
}
```

**Output:**
```text
1. Super Static Block
2. Sub Static Block
--- First Instantiation ---
3. Super Instance Block
4. Super Constructor
5. Sub Instance Block
6. Sub Constructor
--- Second Instantiation ---
3. Super Instance Block
4. Super Constructor
5. Sub Instance Block
6. Sub Constructor
```

**Exact JLS Rule:**
1. **Static Initializers** run **only once** when the class is first loaded into memory by the ClassLoader, in order from SuperClass to SubClass.
2. For each instance creation (`new`):
   - Superclass instance initialization blocks execute in declaration order.
   - Superclass constructor executes.
   - Subclass instance initialization blocks execute in declaration order.
   - Subclass constructor executes.

---

### Puzzle 2: Method Overloading Resolution with `null`
**Question: What does this print?**

```java
public class OverloadTest {
    public static void test(Object o) { System.out.println("Object"); }
    public static void test(String s) { System.out.println("String"); }

    public static void main(String[] args) {
        test(null);
    }
}
```

**Output:** `String`  
**Explanation:**  
Under the Java Language Specification (JLS §15.12.2.5), when multiple overloaded methods are applicable for an argument, the compiler chooses the **most specific method**.  
Since `String` is a subtype of `Object`, `String` is strictly more specific than `Object`.

> **Trap Variation:** What if you add `public static void test(Integer i)`?  
> **Compilation Fails!** Error: `reference to test is ambiguous`. Neither `String` nor `Integer` is a subtype of the other, so the compiler cannot decide.

---

### Puzzle 3: Primitive Widening vs Autoboxing vs Varargs
**Question: Which method is selected?**

```java
public class OverloadHierarchy {
    static void compute(int x)        { System.out.println("Primitive int"); }
    static void compute(long x)       { System.out.println("Primitive Widening long"); }
    static void compute(Integer x)    { System.out.println("Autoboxing Integer"); }
    static void compute(int... x)     { System.out.println("Varargs"); }

    public static void main(String[] args) {
        int val = 10;
        compute(val);
    }
}
```

**Precedence Order of Java Overloading:**
1. **Exact Match:** `compute(int)` (Chosen if present).
2. **Primitive Widening:** `compute(long)` (Wins over Autoboxing!).
3. **Autoboxing:** `compute(Integer)` (Wins over Varargs).
4. **Varargs:** `compute(int...)` (Always lowest priority for backward compatibility).

---

### Puzzle 4: String Concatenation Operator Precedence
**Question: What does this print?**

```java
System.out.println(10 + 20 + "Hello" + 30 + 40);
```

**Output:** `30Hello3040`  
**Explanation:**  
The `+` operator evaluates left-to-right:
1. `10 + 20` $\to$ integer addition evaluates to `30`.
2. `30 + "Hello"` $\to$ integer coerced to string, evaluates to `"30Hello"`.
3. `"30Hello" + 30` $\to$ string concatenation evaluates to `"30Hello30"`.
4. `"30Hello30" + 40` $\to$ string concatenation evaluates to `"30Hello3040"`.

---

## 3. Core Coding Programs (Clean & Production-Grade)

### Program 1: Check if Two Strings are Anagrams
```java
public static boolean isAnagram(String s1, String s2) {
    if (s1 == null || s2 == null || s1.length() != s2.length()) {
        return false;
    }
    // Optimal O(N) time and O(1) space using fixed frequency array
    int[] count = new int[256];
    for (int i = 0; i < s1.length(); i++) {
        count[s1.charAt(i)]++;
        count[s2.charAt(i)]--;
    }
    for (int c : count) {
        if (c != 0) return false;
    }
    return true;
}
```

---

### Program 2: Find Second Largest Element in an Array
```java
public static int findSecondLargest(int[] arr) {
    if (arr == null || arr.length < 2) {
        throw new IllegalArgumentException("Array must contain at least two elements");
    }
    int first = Integer.MIN_VALUE;
    int second = Integer.MIN_VALUE;

    for (int num : arr) {
        if (num > first) {
            second = first;
            first = num;
        } else if (num > second && num != first) {
            second = num;
        }
    }
    if (second == Integer.MIN_VALUE) {
        throw new NoSuchElementException("No distinct second largest element exists");
    }
    return second;
}
```

---

### Program 3: Fibonacci with Memoization
```java
public static long fibonacci(int n) {
    if (n < 0) throw new IllegalArgumentException("Negative input not supported");
    if (n <= 1) return n;
    
    long prev2 = 0, prev1 = 1;
    for (int i = 2; i <= n; i++) {
        long current = prev1 + prev2;
        prev2 = prev1;
        prev1 = current;
    }
    return prev1; // O(n) time, O(1) space!
}
```

---

## 4. Summary Cheatsheet for Revision

| Puzzle | Key Rule |
|---|---|
| **Static Block** | Runs once on class loading before any constructor. |
| **Overload with `null`** | Compiler picks most specific subclass parameter (`String` over `Object`). |
| **Overload Priority** | Exact Match > Widening (`long`) > Autoboxing (`Integer`) > Varargs (`...`). |
| **String + Evaluation** | Left-to-right evaluation; numbers before string sum mathematically; after string they concatenate. |
