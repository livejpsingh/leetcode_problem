# Section 01: Java Basics & Runtime Environment

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** JVM/JRE/JDK Architecture, Bytecode, ClassLoaders, Execution Flow, Access Modifiers, and Core Language Fundamentals.

---

## 1. Executive Summary & Senior Engineer Perspective

In an L1 interview, interviewers do not merely look for memorized textbook definitions; they evaluate whether a candidate understands **how Java actually executes under the hood**, how code transitions from source to native CPU instructions, and how the environment prevents security breaches and memory corruption.

### Key Evaluation Criteria for L1:
1. **Architectural Clarity:** Explaining JVM vs JRE vs JDK with modern context (e.g., modular runtime images in JDK 9+, no standalone JRE installer).
2. **Execution Lifecycle:** Tracing the lifecycle from `.java` compilation, bytecode generation, class loading with parent delegation, bytecode verification, to JIT compilation.
3. **Language Rules & Edge Cases:** Exact method signatures, access modifier boundaries, and fundamental operator behaviors (`==` vs `equals`).

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: What is Java and what are its core architectural features?
**Answer:**  
Java is a high-level, class-based, object-oriented, statically typed programming language developed by Sun Microsystems (now Oracle). It was built around the principle of **"Write Once, Run Anywhere" (WORA)**.

**Key Features:**
- **Platform Independence:** Source code compiles to intermediate bytecode (`.class`), executed on any operating system with a compatible Java Virtual Machine (JVM).
- **Object-Oriented:** Supports OOP principles (Encapsulation, Abstraction, Inheritance, Polymorphism). Java is *not* 100% pure object-oriented because it retains 8 primitive types (`int`, `boolean`, etc.) for computational performance.
- **Robustness:** Strong compile-time and runtime type checking, automatic garbage collection (GC), and structured exception handling eliminate pointer manipulation errors and memory corruption common in C/C++.
- **Security:** Code runs inside a managed sandbox. The Bytecode Verifier ensures no illegal memory access or stack underflows before execution.
- **Multithreaded:** Native support for concurrent execution through the `Thread` class, `java.util.concurrent`, and modern Virtual Threads (Java 21).

---

### Q2: What is the difference between JDK, JRE, and JVM?
**Answer:**  

```
+-----------------------------------------------------------------+
| JDK (Java Development Kit)                                      |
|  [javac, javadoc, jar, jdb, jcmd, jstack, jconsole, ...]        |
|  +-----------------------------------------------------------+  |
|  | JRE (Java Runtime Environment)                            |  |
|  |  [Core Libraries (rt.jar / java.base), Deployment Tools]  |  |
|  |  +-----------------------------------------------------+  |  |
|  |  | JVM (Java Virtual Machine)                          |  |  |
|  |  |  - ClassLoader Subsystem                             |  |  |
|  |  |  - Memory Areas (Heap, Stack, Metaspace, etc.)      |  |  |
|  |  |  - Execution Engine (JIT Compiler, Interpreter, GC) |  |  |
|  |  +-----------------------------------------------------+  |  |
|  +-----------------------------------------------------------+  |
+-----------------------------------------------------------------+
```

| Component | Definition | Primary Purpose | Included Contents |
|---|---|---|---|
| **JVM** | Virtual execution engine specification and implementation. | Executes bytecode into native machine instructions. | ClassLoader, Memory layout, Execution Engine (Interpreter + C1/C2 JIT), GC. |
| **JRE** | Runtime package. | Provides environment to *run* compiled Java programs. | JVM + core standard class libraries (`java.base`, etc.) + configuration. |
| **JDK** | Complete software development kit. | Used to *develop, compile, debug, and profile* Java apps. | JRE + developer CLI tools (`javac`, `jar`, `javap`, `jcmd`, `jstat`, `jmap`). |

> **10 YOE Note:** Since Java 9 (JPMS) and Java 11, Oracle stopped distributing a standalone JRE. Modern deployments use `jlink` to generate tailor-made, minimal runtime images containing only the modules needed by the application.

---

### Q3: Explain the Java compilation and execution lifecycle step by step.
**Answer:**  
1. **Source Code (`.java`):** Written by the developer.
2. **Compilation (`javac`):** The Java compiler translates source code into architecture-neutral bytecode (`.class` files). It checks syntax, resolves types, performs constant folding, and compiles annotations.
3. **Class Loading:** The JVM ClassLoader subsystem dynamically loads `.class` files into memory when referenced.
4. **Linking:**
   - *Verification:* Bytecode verifier ensures bytecode adheres to JVM specifications, checks for stack overflow/underflow, and validates type conversions.
   - *Preparation:* Allocates static memory for class variables and initializes them to default values (e.g., `0`, `null`).
   - *Resolution:* Symbolic references in the constant pool are converted to direct memory references.
5. **Initialization:** Static blocks and static variable explicit initializers run in textual order (`<clinit>`).
6. **Execution (Hybrid Engine):**
   - *Interpreter:* Starts executing bytecode instruction by instruction immediately for fast startup.
   - *JIT (Just-In-Time) Compiler:* Hot spots (frequently executed loops/methods) are compiled into native machine code (C1 client compiler for quick compilation, C2 server compiler for aggressive optimizations like loop unrolling, inlining, and escape analysis).

---

### Q4: What is Bytecode, and why is it important?
**Answer:**  
Bytecode is an intermediate, non-machine-specific instruction set executed by the JVM. Every bytecode instruction consists of a 1-byte opcode followed by optional operands.

- Every valid Java `.class` file begins with the 4-byte magic number `0xCAFEBABE`.
- You can inspect generated bytecode using `javap -c -v ClassName`.

```java
// Java code
public int add(int a, int b) {
    return a + b;
}
```
**Compiled Bytecode (`javap -c`):**
```text
Code:
   0: iload_1      // push local variable 1 (a) onto operand stack
   1: iload_2      // push local variable 2 (b) onto operand stack
   2: iadd         // pop both, add them, push result
   3: ireturn      // return int result from top of stack
```

---

### Q5: What is the role of the ClassLoader, and what is the Parent Delegation Model?
**Answer:**  
The ClassLoader dynamically loads compiled class files into JVM memory at runtime.

#### The Three Built-in ClassLoaders:
1. **Bootstrap ClassLoader:** Written in native C/C++. Loads core JDK classes (`java.base`, `java.lang.*`) from the runtime image (`lib/modules`).
2. **Platform / Extension ClassLoader:** Loads platform extension classes (formerly `jre/lib/ext`).
3. **Application / System ClassLoader:** Loads application classes found on the `CLASSPATH` or `--module-path`.

#### Parent Delegation Mechanism:
When a classloader is requested to load a class:
1. It first checks if the class is already loaded in its cache.
2. If not, it delegates the request upwards to its **parent classloader**.
3. This delegation recurses until the Bootstrap ClassLoader is reached.
4. If the parent cannot find the class, the child classloader attempts to find and load it itself via `findClass()`.

```
        Bootstrap ClassLoader
                 ^
                 | (delegates up)
        Platform ClassLoader
                 ^
                 | (delegates up)
       Application ClassLoader
                 ^
                 | (delegates up)
       Custom ClassLoader
```

**Why Parent Delegation Matters:**
- **Security:** Prevents malicious code from overriding core Java classes (e.g., writing a custom `java.lang.String` to steal passwords will never be loaded because Bootstrap always loads the trusted JDK version).
- **Uniqueness:** Guarantees a class is not loaded multiple times within the same classloader hierarchy.

---

### Q6: What is the difference between PATH and CLASSPATH?
**Answer:**  
- **`PATH`:** An Operating System environment variable used by the shell/command prompt to locate executables (e.g., `java`, `javac`, `mvn`).
- **`CLASSPATH`:** A Java-specific environment variable or command-line option (`-cp` / `--class-path`) used by the JVM and `javac` to locate user-defined classes, packages, and third-party JAR dependencies.

---

### Q7: Why is `main()` declared as `public static void main(String[] args)`?
**Answer:**  
- **`public`:** The JVM launcher runs outside the package of the application class; `public` ensures the JVM can invoke the entry point from anywhere without access restriction.
- **`static`:** The JVM invokes this method before creating any instance of the class. If it were non-static, the JVM wouldn't know which constructor to call (especially if constructors require arguments).
- **`void`:** The method returns nothing to the JVM. Program exit status is communicated via `System.exit(int status)` if required.
- **`String[] args`:** Command-line parameters passed to the program are received as an array of Strings. Varargs syntax (`String... args`) is also fully valid.

#### Edge Cases & Interview Traps:
- **Can we change the return type to `int`?** No, the program will compile, but the JVM will throw `NoSuchMethodError: main` at runtime because the entry-point specification strictly demands `void`.
- **Can we overload `main()`?** Yes, you can define `main(int x)` or `main(String s)`. However, the JVM launcher only calls the standard `public static void main(String[] args)` signature.
- **Can we declare `main` as `final` or `synchronized`?** Yes! `public static final void main(String[] args)` is completely valid.

---

### Q8: What are the Java Access Modifiers and their scope?
**Answer:**  

| Modifier | Same Class | Same Package | Subclass (Different Pkg) | Global (Anywhere) |
|---|:---:|:---:|:---:|:---:|
| **`private`** | Yes | No | No | No |
| **`default` (package-private)** | Yes | Yes | No | No |
| **`protected`** | Yes | Yes | Yes (via inheritance) | No |
| **`public`** | Yes | Yes | Yes | Yes |

> **Gotcha with `protected`:** A subclass in a different package can access a `protected` member *only* through inheritance on instances of that subclass or its descendants, not on a raw instance of the superclass!

---

### Q9: What is the difference between `==` and `.equals()`?
**Answer:**  
- **`==` Operator:** Compares **memory references** (object identity) for objects, and **literal values** for primitives. It tests whether both references point to the exact same memory address on the heap.
- **`.equals()` Method:** Defined in `java.lang.Object`. By default, `Object.equals()` simply performs `this == obj`. However, it is intended to be overridden to test for **logical/value equality** (e.g., `String`, `Integer`, `LocalDate`).

```java
String s1 = new String("tech");
String s2 = new String("tech");

System.out.println(s1 == s2);      // false (different heap objects)
System.out.println(s1.equals(s2));  // true  (same logical character sequence)
```

---

### Q10: What are Packages, Imports, and JAR files?
**Answer:**  
- **Package:** A namespace mechanism that groups related classes/interfaces, prevents naming collisions, and enforces access control boundaries (e.g., `com.company.service`).
- **Import:** A compile-time convenience statement that allows classes to be referred to by their simple name instead of their Fully Qualified Class Name (FQCN). `import java.util.*` does *not* load all classes at runtime and does *not* affect runtime performance or binary size.
- **Static Import (`import static ...`):** Allows accessing static members directly without qualifying them with their class name (e.g., `import static java.lang.Math.PI;`).
- **JAR (Java ARchive):** A zip-compressed archive containing `.class` files, metadata, and resources. An executable JAR contains a `META-INF/MANIFEST.MF` file with a `Main-Class: com.example.Main` attribute.

---

### Q11: Java Keywords & Reserved Words
**Answer:**  
Java has 50+ keywords (e.g., `class`, `interface`, `extends`, `implements`, `static`, `final`, `volatile`, `transient`, `instanceof`).
- **Reserved words not currently used:** `const`, `goto` (cannot be used as identifiers).
- **Literals often mistaken for keywords:** `true`, `false`, `null` are boolean and null literals, not keywords.
- **Contextual keywords (Java 9+):** `var`, `yield`, `record`, `sealed`, `permits` act as keywords only in specific syntax contexts to maintain backward compatibility.

---

## 3. Summary Cheatsheet for Revision

| Topic | Key Takeaway |
|---|---|
| **JVM** | Abstract computing machine that executes bytecode; manages heap, stack, and GC. |
| **Class Loading** | Parent delegation order: Bootstrap -> Platform -> Application. Prevents security overrides. |
| **`main` Method** | Must be `public static void main(String[] args)` (or varargs). Overloadable, but JVM only runs SAM. |
| **Modifiers** | `private` < default (package) < `protected` (subclasses) < `public`. |
| **`==` vs `equals`** | `==` checks reference identity; `equals` checks logical value equality. |
