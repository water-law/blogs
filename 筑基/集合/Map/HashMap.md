## JDK 1.7

### HashMap

HashMap：了解其数据结构、hash 冲突如何解决（链表和红黑树）、扩容时机、扩容时避免 rehash 的优化

HashMap 可以**存储 null 键和 null 值**, 

代码分析

```java
package top.waterlaw.array;

import java.util.HashMap;

public class HashMapTest {


    public static void main(String[] args) {
        HashMap<String, String> m = new HashMap<>(8);
        m.put(null, null);
        System.out.println(m.get(null));
    }
}
```

1.7 版本的 hashmap 结构如下

```java
 * @author  Doug Lea
 * @author  Josh Bloch
 * @author  Arthur van Hoff
 * @author  Neal Gafter
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     TreeMap
 * @see     Hashtable
 * @since   1.2
 */

public class HashMap<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable
{
    // 默认数组大小
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    // 最大数组大小
    static final int MAXIMUM_CAPACITY = 1 << 30;
    // 默认加载因子，put 时判断数组长度超过初始长度的比例即开始扩容
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 不扩容时为空数组
    static final Entry<?,?>[] EMPTY_TABLE = {};
    // 存放 Map 元素的数组, Entry 是一个链表， 有 next 指针
    transient Entry<K,V>[] table = (Entry<K,V>[]) EMPTY_TABLE;
    // 当前数组长度，每次 put 会加 1
    transient int size;
    // 下次扩容时的数组大小
    int threshold;
    // 实际的加载因子
    final float loadFactor;
    // 数组的修改次数
    transient int modCount;
    // 扩容后数组最大的大小
    static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = Integer.MAX_VALUE;
    // 未知， 先不管， 对初始容量不超过 Integer.MAX_VALUE 没影响
    transient int hashSeed = 0;
    // 存放元素的数组， 这个结构就是个链表
    static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;
        Entry<K,V> next;
        int hash;

        /**
         * Creates new entry.
         */
        Entry(int h, K k, V v, Entry<K,V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }
       // ...
    }
}
```

我们来分析下原始的三行代码，

```java
HashMap<String, String> m = new HashMap<>(8);
```

构造函数有三个， 实际调用的是最后一个  public HashMap(int initialCapacity, float loadFactor)

```java
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
	public HashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);

        this.loadFactor = loadFactor;
        threshold = initialCapacity;
        init(); // 空函数
    }
```

可见这个构造函数除了设置初始参数 loadFactor 和 threshold， 什么都没干。

再来看 put 方法

```java
    public V put(K key, V value) {
        if (table == EMPTY_TABLE) {
            inflateTable(threshold);
        }
        if (key == null)
            return putForNullKey(value);
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }
```

如果 table 为空， table 默认就是空(EMPTY_TABLE), 初始化一个数组，inflateTable(threshold);

```java
    private void inflateTable(int toSize) {
        // Find a power of 2 >= toSize
        // 寻找一个大于等于 toSize 的 2 的 n 次方
        int capacity = roundUpToPowerOf2(toSize);

        threshold = (int) Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
        table = new Entry[capacity];
        initHashSeedAsNeeded(capacity);
    }
```

hashmap 采用数组加链表的方法， 将 key 的 hashcode 值一样的元素放在同一个链表中，相当于把数组比较平均地分成 table.length 个。

对 key 为 null 做特别处理...



```java
int hash = hash(key); // 这行代码利用到扰动技术，防止 hash 冲突频繁
int i = indexFor(hash, table.length); // 根据 key 的 hash 值寻找对应的链表
```

indexFor 就一行代码， hashcode & table.length -1, 因为 table.length = 2^n, 所以相当于

hashcode & 0x111111(n 个 1) 也即取 hashcode 二进制中的最后 n 位低位。

```java
    static int indexFor(int h, int length) {
        // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
        return h & (length-1);
    }
```

for 循环则遍历， 如果存在 hash 值一样且 key 内容一样的元素， 则更新该元素对应的 value 值。

否则， 添加一个新元素

```java
addEntry(hash, key, value, i);
```

addEntry 方法会先判断当前 size 是否大于等于 threshold， 如果是则进行 2 倍扩容 resize(2 * table.length)。

```java
    void addEntry(int hash, K key, V value, int bucketIndex) {
        if ((size >= threshold) && (null != table[bucketIndex])) {
            resize(2 * table.length);
            hash = (null != key) ? hash(key) : 0;
            bucketIndex = indexFor(hash, table.length);
        }

        createEntry(hash, key, value, bucketIndex);
    }
```

扩容后再添加新元素  createEntry(hash, key, value, bucketIndex)

resize 则是新建一个 2 倍原来长度的数组， 然后 transfer 函数将原来的数组 copy 到新数组。

```java
    void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable, initHashSeedAsNeeded(newCapacity));
        table = newTable;
        threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }
```

这里 transfer 用到的是将旧链表上的元素添加到新数组对应链表中， 这里使用的是反转原来的链表

```java
    void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e : table) {
            while(null != e) {
                Entry<K,V> next = e.next;
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                int i = indexFor(e.hash, newCapacity);
                // 这个是反转的写法 start, jdk1.7 默认的实现
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
                // 反转 end
                // 这个是不反转的写法 start， 不反转效率低
                Entry<K,V> newEntry = newTable[i];
                e.next = null;
                if(newEntry != null){
                    while(true){
                        if(newEntry.next == null){
                            newEntry.next = e;
                            break;
                        }
                        newEntry = newEntry.next;
                    }
                 }else{
                     newTable[i] = e;
                 } 
                e = next;
                // 不反转 end
            }
        }
    }
```



最重要的 transfer 方法在多线程环境中会带来死循环的问题，如 map 中已有一条链, 扩容后为两条链

A-->B-->C

正常扩容后

B-->A

C

线程 A 和线程 B 同时 put 到达扩容点， A, B 同时进行扩容, 由于是对旧的 table 进行操作， 且两个线程都未能更新旧的 table 时，

A 线程: 

```
e = A
next = B
==================
newTable[i]=> null
```

B 线程:

```
e = A
next = B
==================
newTable[i]=> null
```

假设 B 线程获得 CPU 执行时间

```
e = B
next = C
==================
newTable[i]=> A
```

当 B 线程在如下这部卡住时,  B.next = A, A.next = null

```
e = C
next = null
==================
newTable[i]=> B->A
```

如果此时 A 线程获得 CPU 执行时间

```
e = B
next = A
=================
newTable[i]=>A
```

```
e = A
next = null
=================
newTable[i]=>B->A
```

```
e = null
next = null
=================
newTable[i]=>A->B->A
```

最后数组中一条链表变成  A->B->A, 当执行 get 遍历 map 时正好卡在该链表时， 会出现死循环。

我们来看下 get 函数

```java
    public V get(Object key) {
        if (key == null)
            return getForNullKey();
        Entry<K,V> entry = getEntry(key);

        return null == entry ? null : entry.getValue();
    }
```

先根据 key 的 hash 值定位到链表， 然后判断元素的 hash 和 key 是否与给定的一样

```java
    final Entry<K,V> getEntry(Object key) {
        if (size == 0) {
            return null;
        }

        int hash = (key == null) ? 0 : hash(key);
        for (Entry<K,V> e = table[indexFor(hash, table.length)];
             e != null;
             e = e.next) {
            Object k;
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        return null;
    }
```

如果扩容时出现 A->B->A, 那么 for 循环就会卡住， 无法中止。

### 新元素添加到链表头部

void createEntry(int hash, K key, V value, int bucketIndex) 的实现是将新元素加到链表的头部，将新元素加到链表的头部会导致死循环问题，这一点在 JDK1.8 改进为添加新元素到链表尾部。

```java
    void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K,V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<>(hash, key, value, e);
        size++;
    }
```



### ConcurrentHashMap

ConcurrentHashMap **不可以存储 null 键和 null 值**

JDK7 分段锁技术 Segment

```java
static final class Segment<K,V> extends ReentrantLock implements Serializable
```

jdk7 的 ConcurrentHashMap 源代码写的太不雅观了， jdk 8 写的比较好。

# JDK1.8



### HashMap

HashMap 可以**存储 null 键和 null 值**, 

代码分析

```java
package top.waterlaw.array;

import java.util.HashMap;

public class HashMapTest {


    public static void main(String[] args) {
        HashMap<String, String> m = new HashMap<>(8);
        m.put(null, null);
        System.out.println(m.get(null));
    }
}
```

1.8 版本的 hashmap 结构如下, 接着我们只讲和 jdk7 不同的部分， 当然相同部分也会提及

```java
 * @author  Doug Lea
 * @author  Josh Bloch
 * @author  Arthur van Hoff
 * @author  Neal Gafter
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     TreeMap
 * @see     Hashtable
 * @since   1.2
 */
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 数组元素个数超过此值则使用红黑树
    static final int TREEIFY_THRESHOLD = 8;
    static final int MIN_TREEIFY_CAPACITY = 64;
    // 和 jdk7 同样的链表结构，当数组元素小于 TREEIFY_THRESHOLD 时使用链表，跟 jdk7 没区别
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
        //...
    }
    
}
```

我们来分析下原始的三行代码，

```java
HashMap<String, String> m = new HashMap<>(8);
```

构造函数有三个， 实际调用的是最后一个  public HashMap(int initialCapacity, float loadFactor)

```java
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
```

tableSizeFor(initialCapacity) 和 JDK7 中 put 方法中的 inflateTable(threshold) 功能一样， 显然 threshold 始终为 2 的 n 次方， 这点 JDK8 的代码给人的感觉更清晰， 直接在构造方法中算最接近 initialCapacity 的 2 ^n

resize 部分代码

```java
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
            Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
```



我们只关心下面这部分代码

```java
                        do {
                            next = e.next;
                            // oldCap 为扩容前数组长度
                            if ((e.hash & oldCap) == 0) {
                                // 如果扩容前数组有n位, e 的 hashcode 第n+1位为0则进入此代码块
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                // 如果扩容前数组有n位, e 的 hashcode 第n+1位不为0则进入此代码块
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            // 元素的 hashcode 第n+1位为0，保持原来位置不变
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            // 元素的 hashcode 第n+1位为1，在原来位置上再移动 oldCap=2^n
                            newTab[j + oldCap] = hiHead;
                        }
```



我们来看下 put 部分的代码吧

```java
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        //- 新元素加到链表尾部
                        p.next = newNode(hash, key, value, null);
                        // 元素大于 8 个转为红黑树
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```

可以看到新元素是添加到链表尾部的，这一点和 JDK1.7 是不同的， 而且若链表长度大于 8, 则转为红黑树存储。

而且 JDK1.8 是将元素添加到链表后才会扩容，这点和 JDK1.7 先判断容量是否扩容再添加新元素是不一样的。

### ConcurrentHashMap

ConcurrentHashMap **不可以存储 null 键和 null 值**

JDK1.8 的 put 使用 synchronized 关键字和 CAS 操作以实现扩容， 同时还支持多线程扩容。

先来看下 ConcurrentHashMap 类结构



```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V>
    implements ConcurrentMap<K,V>, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final int DEFAULT_CAPACITY = 16;
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    // 并发级别， 允许同时多少个线程同时访问
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private static final float LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;    
    static final int UNTREEIFY_THRESHOLD = 6;
    static final int MIN_TREEIFY_CAPACITY = 64;    
    private static final int MIN_TRANSFER_STRIDE = 16;    
    private static int RESIZE_STAMP_BITS = 16;
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

    static final int MOVED     = -1; // hash for forwarding nodes
    static final int TREEBIN   = -2; // hash for roots of trees
    static final int RESERVED  = -3; // hash for transient reservations
    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

    /** Number of CPUS, to place bounds on some sizings */
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val; // volatile 属性
        volatile Node<K,V> next; // volatile 属性

        Node(int hash, K key, V val, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }
        //..
    }
    static final class ForwardingNode<K,V> extends Node<K,V> {
        final Node<K,V>[] nextTable; // table 的一个拷贝
        ForwardingNode(Node<K,V>[] tab) {
            // hash 值默认为 MOVED(-1)
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }

        Node<K,V> find(int h, Object k) {
            // loop to avoid arbitrarily deep recursion on forwarding nodes
            outer: for (Node<K,V>[] tab = nextTable;;) {
                Node<K,V> e; int n;
                if (k == null || tab == null || (n = tab.length) == 0 ||
                    (e = tabAt(tab, (n - 1) & h)) == null)
                    return null;
                for (;;) {
                    int eh; K ek;
                    if ((eh = e.hash) == h &&
                        ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    if (eh < 0) {
                        if (e instanceof ForwardingNode) {
                            tab = ((ForwardingNode<K,V>)e).nextTable;
                            continue outer;
                        }
                        else
                            return e.find(h, k);
                    }
                    if ((e = e.next) == null)
                        return null;
                }
            }
        }
    }    
    transient volatile Node<K,V>[] table;
    // 扩容时放到这里
    private transient volatile Node<K,V>[] nextTable;
    private transient volatile long baseCount;
    // 扩容标志
    /* 
     * Hash表的初始化和调整大小的控制标志。为负数，Hash 表正在初始化或者扩容;
     * (-1表示正在初始化,-N 表示有 N-1 个线程在进行扩容)
     * 否则，当表为null时，保存创建时使用的初始化大小或者默认0;
     * 初始化以后保存下一个调整大小的尺寸。
     */
    private transient volatile int sizeCtl;

    /**
     * The next table index (plus one) to split while resizing.
     */
    private transient volatile int transferIndex;    
```

Node的一个子类 ForwardingNodes 也是一个重要的结构，它主要作为一个标记，在处理并发时起着关键作用，有了 ForwardingNodes，也是 ConcurrentHashMap 有了分段的特性，提高了并发效率。

#### 构造方法

```java
    public ConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        if (initialCapacity < concurrencyLevel)   // Use at least as many bins
            initialCapacity = concurrencyLevel;   // as estimated threads
        long size = (long)(1.0 + (long)initialCapacity / loadFactor);
        int cap = (size >= (long)MAXIMUM_CAPACITY) ?
            MAXIMUM_CAPACITY : tableSizeFor((int)size);
        // sizeCtl 在构造方法中表示最接近 initialCapacity / loadFactor 的 2^x 
        this.sizeCtl = cap;
    }
```

sizeCtl 这个参数起到一个控制标志的作用，在 ConcurrentHashMap 初始化和扩容都有用到。 ConcurrentHashMap 构造函数只是设置了一些参数，并没有对 Hash 表进行初始化。当在从插入元素时，才会初始化 Hash 表。在开始初始化的时候，首先判断 sizeCtl 的值，如果 sizeCtl < 0，说明有线程在初始化，当前线程便放弃初始化操作。否则，将 SIZECTL 设置为-1，Hash 表进行初始化。初始化成功以后，将 sizeCtl 的值设置为下次扩容时机的容量。

```java
    private final Node<K,V>[] initTable() {
        Node<K,V>[] tab; int sc;
        while ((tab = table) == null || tab.length == 0) {
            // sizeCtl 小于0，正在初始化
            if ((sc = sizeCtl) < 0)
                // 调用 yield()函数，使线程让出 CPU 资源
                Thread.yield(); // lost initialization race; just spin
            // 设置 SIZECTL 为-1，表示正在初始化
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        // 区别于HashMap：n 为最接近 initialCapacity / loadFactor 的 2^x
                        // sizeCtl 变量做了临时工，帮忙设置初始化数组大小
                        @SuppressWarnings("unchecked")
                        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                        table = tab = nt;
                        // n-(1/4)n,即默认的容量(n * loadFactor)                        
                        sc = n - (n >>> 2);
                    }
                } finally {
                    // 初始化成功以后，将 sizeCtl 的值设置为当前的容量值
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }
```

#### put 方法

如果 key 或者 value为 null，则抛出空指针异常；

如果 table 为 null 或者 table 的长度为0，则初始化 table，调用 initTable() 方法。

计算当前键值的索引位置，如果 Hash 表中当前节点为 null，则将元素直接插入。(注意，这里使用的就是前面锁说的 CAS 操作)

如果当前位置的节点元素的 hash 值为-1，说明这是一个 ForwaringNodes 节点，即正在进行扩容。那么当前线程加入扩容。

当前节点不为 null，对当前节点加锁，将元素插入到当前节点。在 Java8 中，当节点长度大于8时，就将节点转为树的结构。

```java
    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                // f 所在的链表不存在，则使用 CAS 将新元素设置为链表的头结点
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                // f 节点所在的链表正在扩容， 帮助其扩容
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        // 判断是否需要扩容
        addCount(1L, binCount);
        return null;
    }
```

#### 扩容机制

```java
    private final void addCount(long x, int check) {
        // ...
        if (check >= 0) {
            Node<K,V>[] tab, nt; int n, sc;
            while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
                   (n = tab.length) < MAXIMUM_CAPACITY) {
                int rs = resizeStamp(n);
                if (sc < 0) {
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                        transferIndex <= 0)
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                }
                else if (U.compareAndSwapInt(this, SIZECTL, sc,
                                             (rs << RESIZE_STAMP_SHIFT) + 2))
                    //- 开始扩容
                    transfer(tab, null);
                s = sumCount();
            }
        }
    }
```

当 ConcurrentHashMap 中元素的数量达到 cap * loadFactor 时，就需要进行扩容。扩容主要通过 transfer() 方法进行，当有线程进行 put 操作时，如果正在进行扩容，可以通过 helpTransfer() 方法加入扩容。也就是说，ConcurrentHashMap 支持多线程扩容，多个线程处理不同的节点。

开始扩容，首先计算步长，也就是每个线程分配到的扩容的节点数(默认是16)。这个值是根据当前容量和 CPU 的数量来计算 (stride = (NCPU > 1) ? (n >>> 3) / NCPU : n)，最小是16。

接下来初始化临时的 Hash 表 nextTable，如果 nextTable 为 null，初始化 nextTable 长度为原来的2倍；

通过计算出的步长开始遍历 Hash 表，其中坐标是通过一个原子操作(compareAndSetInt)记录。通过一个 while 循环，如果在一个线程的步长内便跳过此节点。否则转下一步；

如果当前节点为空，之间将此节点在旧的Hash表中设置为一个 ForwardingNodes 节点，表示这个节点已经被处理过了。

如果当前节点元素的 hash 值为 MOVED(f.hash == -1)，表示这是一个 ForwardingNodes 节点，则直接跳过。否则，开始重新处理节点；

对当前节点进行加锁，在这一步的扩容操作中，重新计算元素位置的操作与 HashMap 中是一样的，即当前元素键值的 hash 与长度进行&操作，如果结果为0则保持位置不变，为1位置就是 i+n。其中进行处理的元素是最后一个符合条件的元素，所以扩容后可能是一种倒序，但在 Hash 表中这种顺序也没有太大的影响。

最后如果是链表结构直接获得高位与低位的新链表节点，如果是树结构，同样计算高位与低位的节点，但是需要根据节点的长度进行判断是否需要转化为树的结构。

```java
	/**
     * Moves and/or copies the nodes in each bin to new table. See
     * above for explanation.
     */
    private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
        int n = tab.length, stride;
        // 根据长度和 CPU 的数量计算步长，最小是 16
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE; // subdivide range
        if (nextTab == null) {            // initiating
            try {
                @SuppressWarnings("unchecked")
                // 初始化新的 Hash 表，长度为原来的2倍
                Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;
            transferIndex = n;
        }
        int nextn = nextTab.length;
        // 初始化 ForwardingNodes 节点
        ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
        boolean advance = true; // 是否跨过节点的标记
        boolean finishing = false; // to ensure sweep before committing nextTab
        for (int i = 0, bound = 0;;) {
            Node<K,V> f; int fh;
            // 根据步长判断是否需要跨过节点
            while (advance) {
                int nextIndex, nextBound;
                // 到达没有处理的节点下标
                if (--i >= bound || finishing)
                    advance = false;
                else if ((nextIndex = transferIndex) <= 0) {
                	// 所有节点都已经接收处理
                    i = -1;
                    advance = false;
                }
                else if (U.compareAndSetInt
                         (this, TRANSFERINDEX, nextIndex,
                          nextBound = (nextIndex > stride ?
                                       nextIndex - stride : 0))) {
                	// 更新下表 transferIndex,在步长的范围内都忽略
                    bound = nextBound;
                    i = nextIndex - 1;
                    advance = false;
                }
            }
            // 所有节点都被接收处理或者已经处理完毕
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                // 处理完毕
                if (finishing) {
                    nextTable = null;
                    table = nextTab;
                    // 更新 sizeCtl
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                // 判断所有节点是否全部被处理
                if (U.compareAndSetInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        return;
                    finishing = advance = true;
                    i = n; // recheck before commit
                }
            }
            // 如果节点为 null,直接标记为已接收处理
            else if ((f = tabAt(tab, i)) == null)
                advance = casTabAt(tab, i, null, fwd);
            // 键值的 hash 为-1，表示这是一个 ForwardingNodes 节点，已经被处理
            else if ((fh = f.hash) == MOVED)
                advance = true; // already processed
            else {
	            // 对当前节点进行加锁
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        Node<K,V> ln, hn;
                        if (fh >= 0) {
	                        // 索引位置是否改变的标志
                            int runBit = fh & n;
                            Node<K,V> lastRun = f; // 最后一个元素
                            for (Node<K,V> p = f.next; p != null; p = p.next) {
                                int b = p.hash & n;
                                // 重新计算更新直到最后一个元素
                                if (b != runBit) {
                                    runBit = b;
                                    lastRun = p;
                                }
                            }
                            // runBit = 0,保持位置不变
                            if (runBit == 0) {
                                ln = lastRun;
                                hn = null;
                            }
                            // runBit = 1,位置时i+n
                            else {
                                hn = lastRun;
                                ln = null;
                            }
                            // 重新遍历节点元素
                            for (Node<K,V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash; K pk = p.key; V pv = p.val;
                                // 构建低位(位置不变)新的链表
                                if ((ph & n) == 0)
                                    ln = new Node<K,V>(ph, pk, pv, ln);
                                // 构建高位(i+n)新的链表
                                else
                                    hn = new Node<K,V>(ph, pk, pv, hn);
                            }
                            // 将新的链表设置到新的 Hash 表中相应的位置
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            // 将原来的 Hash 表中相应位置的节点设置为 ForwardingNodes 节点
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                        // 如果节点是树的结构
                        else if (f instanceof TreeBin) {
                            TreeBin<K,V> t = (TreeBin<K,V>)f;
                            TreeNode<K,V> lo = null, loTail = null;
                            TreeNode<K,V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            for (Node<K,V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                TreeNode<K,V> p = new TreeNode<K,V>
                                    (h, e.key, e.val, null, null);
                                // 同样的方式计算新的索引位置
                                if ((h & n) == 0) {
	                                // 构建新的链表结构
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                }
                                else {
                                // 构建新的链表结构
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            // 判断是否需要转化为树
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                (hc != 0) ? new TreeBin<K,V>(lo) : t;
                            // 判断是否需要转化为树
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                (lc != 0) ? new TreeBin<K,V>(hi) : t;
                            // 将新的链表设置到新的 Hash 表中相应的位置
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            // 将原来的 Hash 表中相应位置的节点设置为 ForwardingNodes 节点
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                    }
                }
            }
        }
    }
```



# 总结

总结下大概如下：

### 两个版本 HashMap 共同点

- 底层数组+链表/底层数组+链表+红黑树 实现
- 初始 size 为 **16**，扩容：newsize = oldsize*2，size 一定为 2 的 n 次幂
- 当 Map 中元素总数超过 Entry 数组的 75%，触发扩容操作，为了减少链表长度，元素分配更均匀
- 计算 index 方法：index = hash & (tab.length – 1)

HashMap 的初始值还要考虑加载因子:

- **哈希冲突**：若干 Key 的哈希值按数组大小取模后，如果落在同一个数组下标上，将组成一条 Entry 链，对 Key 的查找需要遍历 Entry 链上的每个元素执行 equals() 比较。
- **加载因子**：为了降低哈希冲突的概率，默认当 HashMap 中的键值对达到数组大小的75%时，即会触发扩容。因此，如果预估容量是 100，即需要设定100/0.75＝134的数组大小。
- **空间换时间**：如果希望加快 Key 查找的时间，还可以进一步降低加载因子，加大初始大小，以降低哈希冲突的概率。

### 两个版本 HashMap 区别

#### JDK1.7

- 底层数组+链表

- 扩容针对整个 Map，每次扩容时，原来数组中的元素依次重新计算存放位置，并重新插入，需要 **rehash**
- 插入元素前就判断该不该扩容
- 新元素插入在链表头部，扩容时会造成其他线程 get 死循环
- hash 函数效率较低

#### JDK1.8

- 底层数组+链表+红黑树

- 扩容针对整个 Map，每次扩容时，原来数组中的元素依次重新计算存放位置，并重新插入, **不需要 rehash**, 若扩容后数组大小为 2^(N+1), 则原来 hash 值第 N+1 位为 0 的元素不需要动，N+1位为1的右移 2^N 个位置
- 插入元素后才判断该不该扩容，有可能无效扩容
- 新元素插入在链表尾部，扩容时数据可能会丢失
- 改进 hash 函数

### 两个版本 ConccurentHashMap 区别

#### JDK1.7

- 采用 **segment** 分段锁(**ReentrantLock**)实现线程安全

#### JDK1.8

- **采用 node(Node + ForwardingNode)，锁住 node 来实现减小锁粒度**，使用 **synchronized + CAS 操作来确保 node 的一些操作的原子性**
- 设计了 MOVED 状态，支持多线程扩容，当 resize 的中过程中 线程2 还在 put 数据，线程2会帮助 resize
- sizeCtl 的不同值来代表不同含义，起到了控制的作用

### 两个版本 HashMap 和 ConccurentHashMap 区别

#### JDK1.7

- HashMap 线程不安全， ConccurentHashMap 线程不安全
- HashMap 使用 Entry 存放元素，ConccurentHashMap 使用 Segment 数组存放元素

#### JDK1.8

- HashMap 线程不安全， ConccurentHashMap 线程不安全
- HashMap 初始化数组大小为最接近 initialCapacity 的 2 的 N 次方, ConccurentHashMap 初始化数组大小为最接近 initialCapacity/loadFactor + 1 的 2 的 N 次方