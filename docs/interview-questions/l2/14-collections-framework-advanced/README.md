# Section 14: Collections Framework Advanced Internals

> **Level:** L2 (Senior / Deep Technical Architecture)  
> **Target Experience:** 5–10+ Years (Senior Engineer / Tech Lead / Architect Perspective)  
> **Key Focus:** `HashMap` Bitwise Mathematics & Treeification, `ConcurrentHashMap` CAS + Synchronized Bins, Power-of-Two Resizing, LRU Cache with `LinkedHashMap`, `BlockingQueue` Lock Architectures.

---

## 1. Executive Summary & Senior Engineer Perspective

`HashMap` and `ConcurrentHashMap` internals are the universal benchmark in Java technical interviews. A 10-year Java developer can explain the exact bitwise hash spreading function, the mathematical proof behind power-of-two table sizing, the exact conditions for Red-Black treeification, and how Java 8 eliminated Java 7’s Segment array in `ConcurrentHashMap` to achieve lock-free reads and CAS-based writes.

```
HashMap Bucket Array (Capacity = 16)
[0]  ---> Node(K1, V1) ---> Node(K2, V2)  (Linked list for small collisions)
[1]  ---> null
...
[7]  ---> TreeNode(Red-Black Tree, O(log N) search when count >= 8 & cap >= 64)
...
[15] ---> Node(Kn, Vn)
```

---

## 2. Core Interview Questions & In-Depth Answers

### Q1: Detail the Internal Working of `HashMap` in Java 8+.
**Answer:**  

1. **Backing Array:**  
   `transient Node<K,V>[] table` (initialized lazily on the first `put()` invocation).
2. **Key Constants:**
   - Default Initial Capacity: `16` (`1 << 4`)
   - Maximum Capacity: `2^30` (`1 << 30`)
   - Default Load Factor: `0.75`
   - Threshold = $\text{Capacity} \times \text{LoadFactor}$ ($16 \times 0.75 = 12$)
   - `TREEIFY_THRESHOLD = 8`: Collision count in a single bucket to convert list to Red-Black Tree.
   - `UNTREEIFY_THRESHOLD = 6`: Shrinks tree back to list during resize or removal.
   - `MIN_TREEIFY_CAPACITY = 64`: Table must have at least 64 buckets before treeifying; if bucket has 8 items but table $< 64$, it **resizes instead of treeifying**!

---

### Q2: Why does `HashMap` use `(h = key.hashCode()) ^ (h >>> 16)`?
**Answer:**  
In Java, `hashCode()` returns a 32-bit signed integer. The bucket index is calculated as:
$$\text{index} = (n - 1) \& \text{hash}$$
When the table capacity $n$ is small (e.g., $16$), $n-1 = 15$ (`0x0000000F` in binary).  
A bitwise AND with `15` considers **only the lowest 4 bits** of the hash code, completely ignoring the upper 28 bits!

If keys have hash codes that differ only in higher bits (e.g., `0x00100001` vs `0x00200001`), they will all collide at bucket `1`!

#### The Perturbation Solution:
```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```
By unsigned right-shifting the hash code by 16 bits (`h >>> 16`) and XORing it with the original hash, the upper 16 bits are mixed into the lower 16 bits. This ensures that variation in the upper bits contributes to bucket placement, drastically reducing hash collisions.

---

### Q3: Why MUST `HashMap` Capacity Always Be a Power of Two ($2^k$)?
**Answer:**  

1. **Fast Bitwise Modulo:**  
   To map a hash code to an index between $0$ and $n-1$, mathematical modulo is required: $\text{hash} \pmod n$.  
   On hardware CPUs, division and modulo (`%`) take up to 20–50 CPU clock cycles.  
   When $n$ is a power of two, $\text{hash} \pmod n$ is mathematically identical to:
   $$\text{index} = (n - 1) \& \text{hash}$$
   A bitwise AND (`&`) takes **only 1 CPU clock cycle**!
2. **Even Bit Distribution:**  
   If $n$ is a power of two, $n-1$ is a sequence of all binary ones (`16-1 = 15 = 0b01111`). Every bit position can contribute to the index. If $n$ were an odd number or non-power-of-two (e.g., $15$, so $n-1 = 14 = 0b01110$), the lowest bit is `0`. Consequently, **odd bucket indices would never be chosen**, wasting 50% of the buckets!

---

### Q4: How does `HashMap` Resizing Work without Recalculating Hash Codes?
**Answer:**  
When the number of entries exceeds `threshold`, `resize()` doubles the capacity (`newCap = oldCap << 1`).

Because the capacity is doubled, $n-1$ gains one new high-order bit.  
To determine the new index for each entry, `HashMap` simply checks:
```java
if ((e.hash & oldCap) == 0) {
    // Stays at original index: j
} else {
    // Moves to: j + oldCap
}
```
**No costly re-hashing or modulo operations are performed.** Elements are either kept in place or shifted by `oldCap`, dividing collided chains evenly.

---

### Q5: How does `ConcurrentHashMap` Work in Java 8+? (CAS + Synchronized)
**Answer:**  

```
ConcurrentHashMap Table Array:
[0] ---> Empty  ---> CAS (Unsafe / VarHandle) to insert first node without ANY lock!
[1] ---> Node   ---> synchronized(headNode) { lock ONLY bucket 1 }
[2] ---> TreeBin -> synchronized(treeBin)   { lock ONLY bucket 2 }
```

#### Major Architectural Shifts in Java 8:
- **Java 7:** Used an array of `Segment<K,V>` locks (ReentrantLocks). Concurrency level was fixed at creation (default 16 segments).
- **Java 8+:** Removed `Segment` entirely. Uses a flat `Node<K,V>[] table`.
  1. **Lock-Free Insertions for Empty Bins:** If the target bucket is empty, it uses **CAS (Compare-And-Swap)** to insert the first node without acquiring any lock!
  2. **Fine-Grained Bin Locking:** If the bucket is already populated, it locks **ONLY the first head node** of that specific bucket using `synchronized(node)`. Threads updating other buckets proceed with zero contention.
  3. **Lock-Free Reads (`get()`):** `get()` is **100% lock-free**. It uses volatile reads on `table`, `node.val`, and `node.next` to guarantee memory visibility without blocking writers.
  4. **Cooperative Resizing:** Resizing is multi-threaded. When resizing is in progress, worker threads encountering a `ForwardingNode` (`hash = -1`) help transfer chunks (strides) of buckets to the new table.

#### Why are `null` Keys and Values Forbidden in `ConcurrentHashMap`?
In single-threaded `HashMap`, `map.get(key) == null` can be disambiguated by calling `map.containsKey(key)` (distinguishing between "key maps to null" vs "key does not exist").  
In a concurrent environment, the map's state can change between the `get()` and `containsKey()` calls, making null-handling fundamentally ambiguous and prone to race conditions.

---

### Q6: How do you build an LRU (Least Recently Used) Cache using `LinkedHashMap`?
**Answer:**  
`LinkedHashMap` maintains a doubly linked list running through all its entries. By enabling **access-order** and overriding `removeEldestEntry()`, it becomes a production-ready LRU Cache:

```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxCapacity;

    public LRUCache(int maxCapacity) {
        // initialCapacity, loadFactor, accessOrder = true (order by access, not insertion)
        super(maxCapacity, 0.75f, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity; // Evicts oldest entry when size exceeds limit
    }
}
```

---

### Q7: `ArrayBlockingQueue` vs `LinkedBlockingQueue` (Concurrency Bottlenecks)
**Answer:**  

| Metric | `ArrayBlockingQueue` | `LinkedBlockingQueue` |
|---|---|---|
| **Data Structure** | Bounded circular array. | Optionally bounded singly-linked nodes. |
| **Locking Strategy** | **Single Lock (`ReentrantLock`):** Producers and Consumers compete for the **SAME lock**! | **Two Separate Locks (`putLock` and `takeLock`):** Producers and Consumers operate concurrently! |
| **Throughput** | Lower under high simultaneous produce/consume contention. | **Higher throughput** due to decoupled locks. |
| **GC Overhead** | **Zero GC overhead** (reusable array slots). | Allocates a `Node` object on every `put()`, causing GC pressure. |

---

## 3. Summary Cheatsheet for Revision

| Topic | Critical Fact |
|---|---|
| **`HashMap` Treeify** | Bucket converts to Red-Black tree when items $\ge 8$ AND table capacity $\ge 64$. |
| **Hash Spreading** | `(h) ^ (h >>> 16)` mixes top 16 bits to prevent collisions on low-modulo indexing. |
| **`ConcurrentHashMap`** | CAS on empty bucket; `synchronized` on bucket head; 100% lock-free `get()`. |
| **No Nulls in CHM** | Null keys/values disallowed to prevent race conditions during `containsKey` checks. |
| **LRU Cache** | `LinkedHashMap(cap, 0.75f, true)` + override `removeEldestEntry()`. |
