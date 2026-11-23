# Section 05: Arrays

> **Level:** L1 (Core / Foundational Screening)  
> **Target Experience:** 0–4 Years (Evaluated from a 10+ YOE Senior Developer Perspective)  
> **Key Focus:** Memory Layout, Array Covariance Pitfalls, Jagged Arrays, Copying Mechanisms, Sorting Algorithms (Dual-Pivot Quicksort vs TimSort).

---

## 1. Executive Summary & Senior Engineer Perspective

Arrays are the fundamental building block of almost all collection data structures in Java (`ArrayList`, `ArrayDeque`, `HashMap`). While seemingly simple, array questions frequently catch candidates off guard on **array covariance vs generic invariance, native memory copying (`System.arraycopy`), and time complexities of sorting primitives vs reference types**.

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: How are Arrays represented in Java memory?
**Answer:**  
In Java, **an array is an Object**. Even primitive arrays (e.g., `int[]`) are instances of a dynamically generated class (`[I` in bytecode).

- **Contiguous Allocation:** Array elements are allocated in contiguous memory blocks on the **Heap**.
- **Fixed Size:** Once allocated, the size of an array cannot change.
- **Length Property:** Array length is an immutable `final` property (`arr.length`), not a method call (`str.length()`).
- **Default Values:** Elements automatically receive default values when instantiated (`0` for numeric primitives, `false` for booleans, `null` for objects).

---

### Q2: What is Array Covariance, and why is it dangerous?
**Answer:**  
Arrays in Java are **covariant**: if class `Sub` extends `Super`, then `Sub[]` is considered a subtype of `Super[]`.

```java
// Legal at compile-time:
Object[] objArray = new String[5];

// Catastrophic runtime failure!
objArray[0] = Integer.valueOf(42); // Throws java.lang.ArrayStoreException at runtime!
```
**Why was covariance permitted?**  
Java 1.0 needed generic algorithms like `Arrays.sort(Object[] a)` before Generics existed (Java 5). Array covariance was the only way to write reusable utility methods.  
To prevent type safety violations, the JVM performs a runtime type check on every array store instruction (`aastore`), throwing `ArrayStoreException` if the stored object does not match the actual array type.

> **Comparison with Generics:** Generics are **invariant** (`List<String>` is NOT a `List<Object>`) specifically to eliminate this runtime failure at compile time.

---

### Q3: What is a Jagged (Ragged) Array?
**Answer:**  
In Java, multidimensional arrays are implemented as **arrays of arrays**. Because each inner array is an independent object reference, inner arrays can have **varying lengths**:

```java
int[][] jagged = new int[3][];
jagged[0] = new int[2];
jagged[1] = new int[5];
jagged[2] = new int[3];

System.out.println(jagged[1].length); // 5
```

---

### Q4: How do you copy arrays, and which method is fastest?
**Answer:**  
1. **`System.arraycopy(src, srcPos, dest, destPos, length)` (Fastest / Recommended):**  
   A `native` method implemented directly in C++ via optimized JVM intrinsics (uses direct memory block copying like `memmove`).
2. **`Arrays.copyOf(original, newLength)`:**  
   Internally invokes `System.arraycopy`. Convenient when creating a resized copy of an array.
3. **`array.clone()`:**  
   Produces a **shallow copy**. For object arrays, the references are duplicated, but the underlying objects are shared.

```java
int[] src = {1, 2, 3, 4, 5};
int[] dest = new int[5];
System.arraycopy(src, 0, dest, 0, src.length);
```

---

### Q5: How does `Arrays.sort()` work under the hood?
**Answer:**  
Java uses two different sorting algorithms depending on whether the array contains primitives or objects:
- **For Primitive Arrays (`int[]`, `double[]`, etc.):**  
  Uses **Dual-Pivot Quicksort** (developed by Vladimir Yaroslavskiy).  
  - Time Complexity: Average $O(n \log n)$, Worst-case $O(n^2)$.
  - Does NOT require stability (two identical values of primitive `5` have no identity to preserve).
  - Faster than classical single-pivot Quicksort and avoids heap allocation.
- **For Object Arrays (`Object[]`, `String[]`, etc.):**  
  Uses **TimSort** (a hybrid of MergeSort and InsertionSort).  
  - Time Complexity: Worst-case $O(n \log n)$, Best-case $O(n)$ for partially sorted arrays.
  - **Guaranteed Stable:** Preserves the original relative order of equal elements (essential for business objects).

---

### Q6: `Arrays.asList(array)` vs `List.of(array)` vs `new ArrayList<>(Arrays.asList(array))`
**Answer:**  

```java
String[] arr = {"A", "B", "C"};

// 1. Arrays.asList(arr):
// Fixed-size list backed DIRECTLY by the array. Changes reflect in both!
List<String> list1 = Arrays.asList(arr);
list1.set(0, "Z"); // Mutates arr[0] to "Z"!
// list1.add("D"); // Throws UnsupportedOperationException!

// 2. List.of(arr) (Java 9+):
// Completely IMMUTABLE list. Defensive copy made.
List<String> list2 = List.of(arr);
// list2.set(0, "Z"); // Throws UnsupportedOperationException!
// list2.add("D");    // Throws UnsupportedOperationException!

// 3. Independent Mutable List:
List<String> list3 = new ArrayList<>(Arrays.asList(arr));
list3.add("D"); // Completely safe; operates on independent ArrayList
```

---

## 3. Summary Cheatsheet for Revision

| Topic | Critical Fact |
|---|---|
| **Array Nature** | Objects on the Heap. Fixed size. Length is a `final` field `length`. |
| **Covariance** | `String[]` is an `Object[]`. Storing invalid type throws `ArrayStoreException`. |
| **Fastest Copy** | `System.arraycopy()` (JVM intrinsic / native memory copy). |
| **Sorting** | Primitives use Dual-Pivot Quicksort; Objects use TimSort (stable, $O(n \log n)$). |
