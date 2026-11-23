# Section 06: Collections Framework Basics

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** Hierarchy (`Collection` vs `Map`), `ArrayList` vs `LinkedList`, `HashSet` vs `TreeSet`, Fail-Fast vs Fail-Safe, `Comparable` vs `Comparator`.

---

## 1. Executive Summary & Senior Engineer Perspective

Collections are tested in 100% of Java technical interviews. Senior interviewers evaluate whether you select data structures based on **algorithmic time complexity, memory footprint, cache locality, and concurrency safety**, rather than defaulting to `ArrayList` and `HashMap` for everything.

```
                         Iterable<E>
                              |
                        Collection<E>
                 /            |             \
            List<E>        Set<E>         Queue<E>
            /     \           |               |
     ArrayList   LinkedList  HashSet       Deque<E>
                              |               |
                         TreeSet          ArrayDeque

              Map<K, V> (Separate Hierarchy!)
              /       \
          HashMap    TreeMap
             |
        LinkedHashMap
```

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: Why does `Map` not inherit from `Collection`?
**Answer:**  
- `Collection` works with **individual elements** (`Collection<E>`), defining operations like `add(E e)` and `contains(Object o)`.
- `Map` works with **key-value pairs** (`Map<K, V>`), defining operations like `put(K key, V value)` and `get(Object key)`.
- If `Map` extended `Collection`, it would violate the **Interface Segregation Principle (ISP)** and Liskov Substitution Principle (e.g., what would `collection.add(x)` mean for a key-value mapping?).
- `Map` provides collection views of its data via `keySet()`, `values()`, and `entrySet()`.

---

### Q2: `ArrayList` vs `LinkedList` (Deep Comparison & Myths)
**Answer:**  

| Metric | `ArrayList` | `LinkedList` |
|---|---|---|
| **Data Structure** | Dynamic resizable array (`Object[] elementData`). | Doubly-linked list (`Node<E>` with `prev`, `next`, `item`). |
| **Random Access (`get(i)`)** | **$O(1)$** direct memory offset. | **$O(n)$** sequential traversal from nearest end. |
| **Append (`add(e)`)** | **Amortized $O(1)$**. $O(n)$ on resize. | **$O(1)$** (updates tail pointer). |
| **Insert/Delete at middle** | **$O(n)$** due to `System.arraycopy` element shifting. | **$O(n)$** to find node + $O(1)$ pointer update. |
| **Memory Footprint** | Extremely compact (inlined array). | High overhead: 24+ bytes per node wrapper object! |
| **CPU Cache Locality** | **Superior** (contiguous memory in L1/L2 cache). | **Poor** (scattered heap nodes cause cache misses). |

> **10 YOE Senior Insight:** Textbook dogma claims "use `LinkedList` for frequent insertions/deletions". In modern hardware architectures, **`ArrayList` almost always outperforms `LinkedList` even for inserts/deletes** unless dealing with enormous collections inserted strictly at index 0. The CPU cache-line prefetching for contiguous arrays easily beats node pointer chasing on the heap.

#### `ArrayList` Growth Formula:
When the backing array is full, `ArrayList` grows by **$50\%$**:
$$\text{newCapacity} = \text{oldCapacity} + (\text{oldCapacity} \gg 1)$$

---

### Q3: How does `HashSet` ensure uniqueness?
**Answer:**  
Under the hood, **`HashSet` is backed by a `HashMap`**:

```java
// Simplified JDK HashSet source code:
public class HashSet<E> implements Set<E> {
    private transient HashMap<E, Object> map;
    private static final Object PRESENT = new Object(); // Dummy value

    public HashSet() {
        map = new HashMap<>();
    }

    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }
}
```
When you call `set.add(element)`:
1. It inserts `element` as the **Key** and a static dummy object `PRESENT` as the **Value** into the backing `HashMap`.
2. `HashMap.put()` returns `null` if the key was absent (successful addition). If the key already existed, it returns the previous value, and `add()` returns `false`.
3. Uniqueness is therefore strictly enforced by the `hashCode()` and `equals()` implementation of the element type.

---

### Q4: `HashSet` vs `LinkedHashSet` vs `TreeSet`
**Answer:**  

| Feature | `HashSet` | `LinkedHashSet` | `TreeSet` |
|---|---|---|---|
| **Backing Structure** | `HashMap` (Hash table) | `LinkedHashMap` (Hash table + Doubly linked list) | `TreeMap` (Red-Black Tree) |
| **Ordering** | **No order** guaranteed | **Insertion order** preserved | **Sorted / Natural order** (or via `Comparator`) |
| **Null Elements** | Allows 1 `null` | Allows 1 `null` | **Disallows `null`** (throws `NullPointerException` on comparison) |
| **Time Complexity** | $O(1)$ add/contains | $O(1)$ add/contains | **$O(\log n)$** add/contains |

---

### Q5: What is Fail-Fast vs Fail-Safe Iteration?
**Answer:**  

#### Fail-Fast Iterators:
- Used by standard collections (`ArrayList`, `HashMap`, `HashSet`).
- Maintain an internal modification counter: `modCount`.
- When an iterator is created, it records `expectedModCount = modCount`.
- On every call to `next()` or `remove()`, if `modCount != expectedModCount`, it immediately throws **`ConcurrentModificationException`**.
- **Safe modification during iteration:** You can remove elements safely **only via `iterator.remove()`**, which updates `expectedModCount` synchronously.

```java
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
for (String s : list) {
    if ("B".equals(s)) {
        list.remove(s); // THROWS ConcurrentModificationException!
    }
}

// SAFE ALTERNATIVE:
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if ("B".equals(it.next())) {
        it.remove(); // Completely safe!
    }
}
```

#### Fail-Safe / Weakly Consistent Iterators:
- Used by concurrent collections (`CopyOnWriteArrayList`, `ConcurrentHashMap`).
- Operate on a **clone/snapshot** or weakly consistent view of the backing array.
- **Never throw `ConcurrentModificationException`**, even if modified concurrently by another thread.

---

### Q6: `Comparable` vs `Comparator` (Comprehensive Comparison)
**Answer:**  

| Feature | `Comparable<T>` | `Comparator<T>` |
|---|---|---|
| **Package** | `java.lang` | `java.util` |
| **Method** | `public int compareTo(T o)` | `public int compare(T o1, T o2)` |
| **Location** | Implemented **inside** the domain class itself. | Implemented as a **separate class, lambda, or static factory**. |
| **Purpose** | Defines **Natural Sorting Order** for the class. | Defines **Custom / Multiple Sorting Sequences**. |
| **Modification** | Requires modifying source code of domain class. | Does not require modifying domain class. |
| **Modern Java 8+** | Typically static method `compareTo`. | Fluent chaining: `Comparator.comparing(...).thenComparing(...)`. |

```java
// Modern Java 8+ Comparator Chaining:
List<Employee> employees = getEmployees();
employees.sort(
    Comparator.comparing(Employee::getDepartment)
              .thenComparing(Employee::getSalary, Comparator.reverseOrder())
              .thenComparing(Employee::getName)
);
```

---

### Q7: Why is `ArrayDeque` preferred over `Stack` and `LinkedList` for Queues/Stacks?
**Answer:**  
- `java.util.Stack` is a legacy class from JDK 1.0 that extends `Vector`. Every method (`push`, `pop`, `peek`) is `synchronized`, adding massive unnecessary lock contention overhead in single-threaded contexts.
- `LinkedList` allocates a new `Node` object for every pushed element, causing heap fragmentation and GC pressure.
- `ArrayDeque` uses a **circular resizable array**. It is unsynchronized, allocates no node objects, has superior cache locality, and is significantly faster as both a LIFO Stack and a FIFO Queue.

---

## 3. Summary Cheatsheet for Revision

| Collection | Time Complexity | Order | Thread-Safe? |
|---|---|---|---|
| **`ArrayList`** | $O(1)$ read, amortized $O(1)$ append | Insertion | No |
| **`HashSet`** | $O(1)$ add/contains | None | No |
| **`TreeSet`** | $O(\log n)$ add/contains | Sorted | No |
| **`ArrayDeque`** | $O(1)$ amortized push/pop/poll | FIFO / LIFO | No |
| **`ConcurrentModificationException`** | Thrown when collection is structurally modified during iteration without `iterator.remove()`. |
