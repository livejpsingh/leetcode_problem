# Section 12: Advanced OOP & Software Design

> **Level:** L2 (Senior / Deep Technical Architecture)  
> **Target Experience:** 5–10+ Years (Senior Engineer / Tech Lead / Architect Perspective)  
> **Key Focus:** SOLID Principles in Enterprise Systems, Liskov Substitution Violations, Double-Checked Locking Singleton, Dynamic Proxies vs CGLIB, Shallow vs Deep Cloning.

---

## 1. Executive Summary & Senior Engineer Perspective

Senior engineers are not judged on whether they know what the letter "S" or "O" in SOLID stands for, but on whether they can **identify design violations in code reviews, avoid architectural fragility, prevent concurrency hazards in creational patterns, and enforce clean domain boundaries**.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: Detail the SOLID Principles with Real Enterprise Scenarios.
**Answer:**  

1. **Single Responsibility Principle (SRP):**
   - *A class should have one, and only one, reason to change.*
   - *Violation:* An `OrderService` that calculates discounts, generates PDFs, executes SQL queries, and sends SMS notifications.
   - *Resolution:* Separate into `OrderRepository`, `InvoicePdfGenerator`, and `NotificationService`.
2. **Open/Closed Principle (OCP):**
   - *Software entities should be open for extension, but closed for modification.*
   - *Enterprise implementation:* Strategy pattern, Spring Plugin architecture, or Dependency Injection. Adding a new payment gateway (e.g., ApplePay) requires adding a new `PaymentGateway` implementation class without altering existing `PaymentProcessor` code.
3. **Liskov Substitution Principle (LSP):**
   - *Subtypes must be substitutable for their base types without altering the correctness of the program.*
   - *The Classic Violation (Square-Rectangle Problem):*
     ```java
     class Rectangle {
         protected int width, height;
         public void setWidth(int w) { this.width = w; }
         public void setHeight(int h) { this.height = h; }
         public int getArea() { return width * height; }
     }
     class Square extends Rectangle {
         @Override
         public void setWidth(int w) { this.width = w; this.height = w; }
         @Override
         public void setHeight(int h) { this.width = h; this.height = h; }
     }
     ```
     A caller assuming `rect.setWidth(5); rect.setHeight(10);` expects area 50, but with `Square` it produces 100! This violates the contract of `Rectangle`.
4. **Interface Segregation Principle (ISP):**
   - *Clients should not be forced to depend on methods they do not use.*
   - *Resolution:* Break bloated "god interfaces" into cohesive, role-based interfaces (e.g., `ReadableStream` vs `WritableStream` instead of one combined `Stream`).
5. **Dependency Inversion Principle (DIP):**
   - *High-level modules should not depend on low-level modules; both should depend on abstractions. Abstractions should not depend on details; details should depend on abstractions.*
   - Realized in modern frameworks via **Inversion of Control (IoC)** and **Dependency Injection (DI)**.

---

### Q2: Why is `volatile` mandatory in Double-Checked Locking Singleton?
**Answer:**  

```java
public class SafeSingleton {
    // MUST be declared volatile!
    private static volatile SafeSingleton instance;

    private SafeSingleton() {}

    public static SafeSingleton getInstance() {
        if (instance == null) {                         // 1st Check (Lock-free)
            synchronized (SafeSingleton.class) {
                if (instance == null) {                 // 2nd Check (With lock)
                    instance = new SafeSingleton();     // Vulnerable to reordering!
                }
            }
        }
        return instance;
    }
}
```

#### Why `volatile` is Non-Negotiable:
The statement `instance = new SafeSingleton();` is **not atomic**. At the bytecode level, it compiles into three distinct steps:
1. `allocate()`: Allocate raw memory block for the object on the heap.
2. `ctor()`: Invoke the constructor to initialize fields.
3. `assign()`: Assign the memory reference to the variable `instance`.

Without `volatile`, the JIT compiler and CPU out-of-order execution engine are permitted to reorder steps 2 and 3:
$$\text{allocate()} \to \text{assign()} \to \text{ctor()}$$

If Thread A executes `allocate()` followed immediately by `assign()`, the reference `instance` is **non-null**, but the object constructor has not finished executing!  
Thread B now enters `getInstance()`, sees `instance != null` at the 1st check, and returns a **half-initialized, corrupted object**, leading to catastrophic runtime failures!

> **The `volatile` Fix:** Enforces a memory barrier (StoreStore fence), guaranteeing that step 2 (initialization) strictly happens-before step 3 (publishing reference).

---

### Q3: What is the Bill Pugh Singleton and Enum Singleton?
**Answer:**  

#### 1. Bill Pugh Singleton (Initialization-on-demand Holder):
Leverages the JVM class-loading guarantee: static nested classes are not loaded into memory until explicitly referenced:

```java
public class BillPughSingleton {
    private BillPughSingleton() {}

    private static class InstanceHolder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return InstanceHolder.INSTANCE; // Thread-safe, lazy, zero lock overhead!
    }
}
```

#### 2. Enum Singleton (Joshua Bloch's Recommended Pattern):
```java
public enum EnumSingleton {
    INSTANCE;
    public void doWork() { ... }
}
```
**Why Enum Singleton is Unbeatable:**
- 100% thread-safe; guaranteed by the JVM.
- **Immune to Reflection Attacks:** `Constructor.newInstance()` explicitly checks if the class is an enum and throws `IllegalArgumentException: Cannot reflectively create enum objects`.
- **Immune to Serialization Exploits:** The JVM provides special serialization handling for Enums; it guarantees only the single enum constant is resolved.

---

### Q4: How does JDK Dynamic Proxy differ from CGLIB Proxy?
**Answer:**  
Both generate proxy objects at runtime to intercept method calls (used extensively in Spring `@Transactional`, `@Security`, `@Cacheable`):

| Feature | JDK Dynamic Proxy (`java.lang.reflect.Proxy`) | CGLIB Proxy (`net.sf.cglib`) |
|---|---|---|
| **Mechanism** | Reflection API built into core JDK. | Bytecode generation via ASM library. |
| **Requirement** | Target class **MUST implement an Interface**. | Generates a dynamic **subclass** of the target class. |
| **Classes without Interfaces**| Cannot proxy. | Proxies concrete classes easily. |
| **Limitations** | Only intercepts interface methods. | **Cannot proxy `final` classes or `final` methods** (cannot subclass or override). |
| **Spring Boot Default** | Default up to Spring Boot 1.3. | **Default in Spring Boot 2.0+** (`spring.aop.proxy-target-class=true`). |

---

### Q5: What is the problem with `Cloneable` and `Object.clone()`?
**Answer:**  
Joshua Bloch famously called `Cloneable` one of the greatest design mistakes in Java:
1. **Broken Contract:** `Cloneable` is a marker interface with no methods. Yet `Object.clone()` is `protected`. Implementing `Cloneable` does not provide a public `clone()` method unless you manually override it and cast the result.
2. **Bypasses Constructors:** `Object.clone()` creates an object without invoking any constructor, breaking invariants and object initialization guarantees.
3. **Shallow Copy by Default:** Duplicate references point to the exact same mutable state.

#### Superior Modern Alternatives:
- **Copy Constructor:** `public User(User other)`
- **Static Factory Method:** `public static User copyOf(User other)`
- **Serialization Deep Copy:** Using Jackson / Gson JSON cloning or Java records.

---

## 3. Summary Cheatsheet for Revision

| Topic | Critical Architecture Rule |
|---|---|
| **Liskov (LSP)** | Subtypes must honor supertype invariants. Rectangles cannot be replaced by Squares. |
| **DCL Singleton** | `volatile` is mandatory to prevent publication of half-initialized objects due to instruction reordering. |
| **Enum Singleton** | Best singleton: immune to reflection attacks and serialization cloning. |
| **AOP Proxies** | JDK Proxy requires interfaces; CGLIB subclasses the class (cannot proxy `final` methods). |
| **Cloning** | Never use `Cloneable`. Always prefer copy constructors or factory methods. |
