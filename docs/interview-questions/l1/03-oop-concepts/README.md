# Section 03: Object-Oriented Programming (OOP) Concepts

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** 4 Pillars, Overloading vs Overriding Rules, Abstract Classes vs Interfaces, `equals()` & `hashCode()` Contract, `static` vs `final`.

---

## 1. Executive Summary & Senior Engineer Perspective

In senior and technical lead interviews, OOP is where many candidates fail because they repeat abstract academic analogies (e.g., "Dog extends Animal") instead of demonstrating **software engineering principles, encapsulation boundaries, polymorphism mechanics, and the `equals`/`hashCode` contract**. 

A 10-year Java developer designs for **low coupling, high cohesion, immutability, and substitutability (Liskov Substitution Principle)**.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: What are the 4 Pillars of OOP and how are they implemented in Java?
**Answer:**  

1. **Encapsulation:**
   - Bundling state (data) and behavior (methods) into a single unit (class), while hiding internal state using `private` fields.
   - Enforces invariants and business validation via getters and business methods.
2. **Abstraction:**
   - Hiding internal implementation complexities and exposing only essential functional contracts to callers.
   - Implemented via `interface` and `abstract class`.
3. **Inheritance:**
   - A mechanism where a child class acquires the properties and methods of a parent class (`extends`), modeling an **"IS-A"** relationship.
   - Enables code reuse, but tight coupling makes excessive inheritance an architectural liability (prefer composition).
4. **Polymorphism:**
   - Ability of an entity to take multiple forms.
   - *Compile-Time (Static):* Method Overloading.
   - *Runtime (Dynamic):* Method Overriding via Dynamic Method Dispatch.

---

### Q2: Method Overloading vs Method Overriding (Comprehensive Comparison)
**Answer:**  

| Feature | Method Overloading | Method Overriding |
|---|---|---|
| **Pillar** | Compile-time polymorphism (Early binding). | Runtime polymorphism (Late / Dynamic dispatch). |
| **Location** | Within the same class (or inherited). | In a subclass redefining parent method. |
| **Method Name** | **Must be identical**. | **Must be identical**. |
| **Parameters** | **Must differ** (by count, type, or order). | **Must be identical**. |
| **Return Type** | Can be anything. Does NOT distinguish overloads. | Must be identical or a **Covariant subtype**. |
| **Access Modifier** | Can be anything. | **Cannot reduce visibility** (e.g., `protected` -> `public` is OK, but `public` -> `private` is illegal). |
| **Exceptions** | Can throw any checked/unchecked exceptions. | **Cannot throw broader or new checked exceptions** (can throw narrower, fewer, or unchecked). |
| **`private`/`final`/`static`** | Can overload `private`, `final`, or `static` methods. | **Cannot override** `private` or `final`. Re-declaring `static` causes **Method Hiding**, not overriding. |

#### Covariant Return Types Example:
```java
class SuperService {
    public CharSequence getPayload() { return "payload"; }
}
class SubService extends SuperService {
    @Override
    public String getPayload() { return "specialized payload"; } // Valid: String is a subtype of CharSequence
}
```

---

### Q3: Abstract Class vs Interface (Java 8, 9, and Modern Evolution)
**Answer:**  

| Feature | Abstract Class | Interface |
|---|---|---|
| **Inheritance** | Single inheritance (`extends`). | Multiple inheritance (`implements A, B, C`). |
| **State / Fields** | Can have instance variables, static fields, and constructors. | Only `public static final` constants. **No instance state**. |
| **Constructors** | **Yes** (to initialize abstract class state when subclass is instantiated). | **No constructors**. |
| **Method Types** | Abstract, concrete, `final`, `static`, `private`, `protected`. | Abstract (public), `default` (Java 8), `static` (Java 8), `private` (Java 9). |
| **Access Modifiers** | Any (`public`, `protected`, `package-private`, `private`). | Implicitly `public` (except `private` helper methods). |
| **Design Intent** | Represents identity & shared state (**"IS-A"**). | Represents capabilities or contracts (**"CAN-DO"**). |

> **Why did Java 8 introduce `default` methods in interfaces?**  
> To enable **backward compatibility** in standard libraries. Without `default` methods, adding `stream()` to `java.util.Collection` would have broken every existing custom collection class implemented in the world!

---

### Q4: How is the Diamond Problem resolved with Interface Default Methods?
**Answer:**  
If a class implements two interfaces that provide the same default method signature, the compiler rejects the class with a duplicate default methods error. The implementing class **must explicitly resolve the ambiguity**:

```java
interface Left {
    default void execute() { System.out.println("Left"); }
}
interface Right {
    default void execute() { System.out.println("Right"); }
}

class Service implements Left, Right {
    @Override
    public void execute() {
        // Explicitly choose one or provide custom logic:
        Left.super.execute(); // or Right.super.execute();
    }
}
```

---

### Q5: Can we override `static` methods in Java? What is Method Hiding?
**Answer:**  
**No**, static methods cannot be overridden. They are resolved at **compile time** based on the declared reference type, not at runtime based on the actual object instance.

If a subclass defines a static method with the same signature as a superclass static method, it is **Method Hiding**, not overriding:

```java
class Parent {
    public static void greet() { System.out.println("Parent"); }
}
class Child extends Parent {
    public static void greet() { System.out.println("Child"); }
}

Parent p = new Child();
p.greet(); // Prints "Parent" because reference type is Parent!
```

---

### Q6: What is the `equals()` and `hashCode()` contract, and why must both be overridden together?
**Answer:**  
The contract is defined in `java.lang.Object`:
1. **Reflexive:** `x.equals(x)` must return `true`.
2. **Symmetric:** `x.equals(y)` returns `true` if and only if `y.equals(x)` returns `true`.
3. **Transitive:** If `x.equals(y)` is `true` and `y.equals(z)` is `true`, then `x.equals(z)` must be `true`.
4. **Consistent:** Repeated invocations of `x.equals(y)` consistently return `true` or `false` provided no fields are modified.
5. **Non-nullity:** `x.equals(null)` must always return `false`.

#### The Golden Contract with `hashCode()`:
- **If two objects are equal according to `equals()`, they MUST produce the SAME `hashCode()`.**
- If two objects produce different hash codes, they must NOT be equal.
- (If two objects have the same hash code, they are NOT necessarily equal—this is a hash collision).

#### What happens if you override `equals()` but NOT `hashCode()`?
If used as a key in `HashMap` or stored in a `HashSet`, **the collection breaks**:
```java
Employee e1 = new Employee(101, "Alice");
Employee e2 = new Employee(101, "Alice"); // e1.equals(e2) is true

Map<Employee, String> map = new HashMap<>();
map.put(e1, "Developer");

// If hashCode() is not overridden, e2 produces a different default Object identity hash code.
// The map looks in the wrong hash bucket and returns NULL!
System.out.println(map.get(e2)); // Returns null!
```

---

### Q7: What are `this` and `super` keywords?
**Answer:**  
- **`this`:** Reference variable pointing to the **current object instance**. Used to:
  - Disambiguate instance variables from shadowed parameters (`this.id = id`).
  - Chain constructors within the same class (`this(args)` — must be the first statement).
  - Pass current object as a parameter to other methods.
- **`super`:** Reference variable referring to the **immediate parent class**. Used to:
  - Invoke parent class constructors (`super(args)` — must be the first statement).
  - Access parent class methods or fields hidden by subclass overrides (`super.doWork()`).

---

### Q8: The `final` Keyword: Variables, Methods, and Classes
**Answer:**  
- **`final` Variable:** Cannot be reassigned once initialized.
  > **Crucial 10 YOE Insight:** A final object reference cannot be reassigned to point to another object, but the **internal state of the referenced object is still mutable** (e.g., `final List<String> list = new ArrayList<>(); list.add("data");` is completely valid).
- **`final` Method:** Cannot be overridden by any subclass. Ensures immutable behavior or security contracts.
- **`final` Class:** Cannot be extended/subclassed (e.g., `java.lang.String`, `Integer`). Prevents security exploits and guarantees immutability.

---

### Q9: Composition vs Inheritance: Why "Favor Composition Over Inheritance"?
**Answer:**  
Inheritance (`extends`) exposes parent implementation details to the child class (**breaks encapsulation**, "White-Box Reuse"). If the parent changes its internal methods, subclasses can break unexpectedly.

**Composition** ("Black-Box Reuse") models a **"HAS-A"** relationship: the class contains references to other components and delegates tasks to them via interfaces.

**Advantages of Composition:**
- Components can be swapped dynamically at runtime (Strategy Pattern).
- Enhances testability through mocking.
- Prevents fragile base class problems.

---

### Q10: Coupling vs Cohesion
**Answer:**  
- **Cohesion:** Measures how focused and closely related the responsibilities inside a single class or module are. High cohesion is desirable (Single Responsibility Principle).
- **Coupling:** Measures the degree of direct dependency between different classes or modules. Low (loose) coupling is desirable so that changes to one class do not ripple across the codebase.

---

## 3. Summary Cheatsheet for Revision

| Topic | Critical Interview Point |
|---|---|
| **Overloading** | Compile-time; must differ in parameter types/count. Return type does NOT matter. |
| **Overriding** | Runtime; same signature; covariant return allowed; cannot reduce access; no new checked exceptions. |
| **Static Methods** | Cannot be overridden; they are hidden based on reference type. |
| **`equals` & `hashCode`** | Equal objects must have identical hash codes. Always override both together using immutable fields. |
| **Interfaces** | Java 8 added default/static methods; Java 9 added private methods. No state. |
