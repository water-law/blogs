---
title: Java 基础
date: 2019-04-09 21:59:16
categories:
  - Java
tags:
  - Java
---

### java 基础

<!--more-->

#### 集合

集合分为两大块：java.util 包下的非线程安全集合和 java.util.concurrent 下的线程安全集合。

##### List

ArrayList 与 LinkedList 的实现和区别

1. ArrayList 的实现是基于数组，LinkedList 的实现是基于双向链表。 
2. 对于随机访问，ArrayList 优于 LinkedList，ArrayList 可以根据下标以 O(1) 时间复杂度对元素进行随机访问。而 LinkedList 的每一个元素都依靠地址指针和它后一个元素连接在一起，在这种情况下，查找某个元素的时间复杂度是 O(n) 
3. 对于插入和删除操作，LinkedList 优于 ArrayList，因为当元素被添加到 LinkedList 任意位置的时候，不需要像 ArrayList 那样重新计算大小或者是更新索引。 
4. . LinkedList 比 ArrayList 更占内存，因为 LinkedList 的节点除了存储数据，还存储了两个引用，一个指向前一个元素，一个指向后一个元素。

##### Map

HashMap：了解其数据结构、hash 冲突如何解决（链表和红黑树）、扩容时机、扩容时避免 rehash 的优化

- 底层数组+链表实现，可**以存储 null 键和 null 值**，线程**不安全**
- 初始 size 为 **16**，扩容：newsize = oldsize*2，size 一定为2的n次幂
- 扩容针对整个 Map，每次扩容时，原来数组中的元素依次重新计算存放位置，并重新插入
- 插入元素后才判断该不该扩容，有可能无效扩容（插入后如果扩容，如果没有再次插入，就会产生无效扩容）
- 当 Map 中元素总数超过 Entry 数组的 75%，触发扩容操作，为了减少链表长度，元素分配更均匀
- 计算 index 方法：index = hash & (tab.length – 1)

HashMap 的初始值还要考虑加载因子:

-  **哈希冲突**：若干 Key 的哈希值按数组大小取模后，如果落在同一个数组下标上，将组成一条 Entry 链，对 Key 的查找需要遍历 Entry 链上的每个元素执行 equals() 比较。

- **加载因子**：为了降低哈希冲突的概率，默认当 HashMap 中的键值对达到数组大小的75%时，即会触发扩容。因此，如果预估容量是 100，即需要设定100/0.75＝134的数组大小。

- **空间换时间**：如果希望加快 Key 查找的时间，还可以进一步降低加载因子，加大初始大小，以降低哈希冲突的概率。*

  

HashMap  非线程安全

JDK1.7 HashMap 在多线程的扩容时确实会出现循环引用，导致下次 get 时死循环的问题，具体可以参考 HashMap 死循环问题。

JDK1.8 的优化已经避免了死循环这个问题，但是会造成数据丢失问题，下面我举个例子：

创建 thread1 和 thread2 去添加数据，此时都在 resize，两个线程分别创建了两个 newTable，并且 thread1 在 table = newTab;处调度到 thread2 (没有给table赋值)，等待 thread2 扩容之后再调度回 thread1，注意，扩容时 oldTab[j] = null; 也就将 oldTable 中都清掉了，当回到 thread1 时，将 table 指向 thread1 的 newTable，但访问 oldTable 中的元素全部为 null，所以造成了数据丢失。

##### **HashTable**

- 底层数组+链表实现，无论 key 还是  value  都**不能为 null**，线程**安全**，实现线程安全的方式是在修改数据时锁住整个 HashTable，效率低，ConcurrentHashMap 做了相关优化

- 初始 size 为 **11**，扩容：newsize = olesize*2+1

- 计算 index 的方法：index = (hash & 0x7FFFFFFF) % tab.length

  

Hashtable 和 HashMap 都实现了 Map 接口，但是 Hashtable的 实现是基于 Dictionary 抽象类的。

Hashtable 是线程安全的，它的方法是同步的，可以直接用在多线程环境中。而 HashMap 则不是线程安全的，在多线程环境中，需要手动实现同步机制。

Hashtable 与 HashMap 另一个区别是 HashMap 的迭代器（Iterator）是 fail-fast 迭代器，而 Hashtable 的 enumerator 迭代器不是 fail-fast 的。所以当有其它线程改变了 HashMap 的结构（增加或者移除元素），将会抛出 ConcurrentModificationException，但迭代器本身的 remove() 方法移除元素则不会抛出ConcurrentModificationException 异常。

LinkedHashMap：了解基本原理、哪两种有序、如何用它实现 LRU

HashMap+双向链表

LinkedHashMap 存储数据是有序的，而且分为两种：插入顺序和访问顺序。

accessOrder   false： 基于插入顺序     true：  基于访问顺序（基于访问的顺序，get一个元素后，这个元素被加到最后(使用了LRU 最近最少被使用的调度算法)）

```java
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
 
public class LRULinkedMap<K, V> {
 
	/**
     * 最大缓存大小
     */
	private int cacheSize;
	
	private LinkedHashMap<K, V> cacheMap;
	
	public LRULinkedMap(int cacheSize){
		this.cacheSize = cacheSize;
		
		cacheMap = new LinkedHashMap(16, 0.75F, true){
 
			@Override
			protected boolean removeEldestEntry(Entry eldest) {
				if(cacheSize + 1 == cacheMap.size()){
					return true;
				}else{
					return false;
				}
			}
		};
	}
	
	public void put(K key, V value){
		cacheMap.put(key, value);
	}
	
	public V get(K key){
		return cacheMap.get(key);
	}
	
	public Collection<Map.Entry<K, V>> getAll(){
		return new ArrayList<Map.Entry<K, V>>(cacheMap.entrySet());
	}
	
	public static void main(String[] args) {
		LRULinkedMap<String, Integer> map = new LRULinkedMap<>(3);
		map.put("key1", 1);
		map.put("key2", 2);
		map.put("key3", 3);
		
		for (Map.Entry<String, Integer> e : map.getAll()){
			System.out.println(e.getKey()+"====>"+e.getValue());
		}
		System.out.println("\n");
		map.put("key4", 4);
		for (Map.Entry<String, Integer> e : map.getAll()){
			System.out.println(e.getKey()+"====>"+e.getValue());
		}
		
	}
	 
}
```

TreeMap：了解数据结构、了解其 key 对象为什么必须要实现 Compare 接口、如何用它实现一致性哈希

TreeMap 是一个**有序的 key-value 集合**，它是通过[红黑树](http://www.cnblogs.com/skywang12345/p/3245399.html)实现的。

其 key 对象必须要实现 Compare 接口, 以保证元素按照 key 值有序存储。

**一致性哈希**：由于一般的哈希函数返回一个 int（32bit）型的 hashCode。

**一致性哈希算法**：先构造一个长度为 2^32 的整数环（这个环被称为一致性 Hash 环），根据节点名称的 Hash 值（其分布为[0, 2^32-1]）将服务器节点放置在这个Hash 环上，然后根据数据的 Key 值计算得到其 Hash 值（其分布也为[0, 2^32-1]），接着在 Hash 环上顺时针查找距离这个 Key 值的 Hash 值最近的服务器节点，完成 Key 到服务器的映射查找。

1. 排序 +List
2. 遍历 + List
3. 红黑树 TreeMap

使用一致性 Hash 算法，尽管增强了系统的伸缩性，但是也有可能导致负载分布不均匀(ip 连续性)，解决办法就是使用**虚拟节点代替真实节点**。

解决这个问题的办法是引入虚拟节点，其工作原理是：**将一个物理节点拆分为多个虚拟节点，并且同一个物理节点的虚拟节点尽量均匀分布在 Hash 环上**。采取这样的方式，就可以有效地解决增加或减少节点时候的负载不均衡的问题。

1、一个真实结点如何对应成为多个虚拟节点？

2、虚拟节点找到后如何还原为真实结点？

这两个问题其实有很多解决办法，我这里使用了一种简单的办法，给每个真实结点后面根据虚拟节点加上后缀再取Hash值，比如”192.168.0.0:111″就把它变成”192.168.0.0:111&&VN0″到”192.168.0.0:111&&VN4″，VN就是Virtual Node的缩写，还原的时候只需要从头截取字符串到”&&”的位置就可以了。

```java
  public class ConsistentHashingWithVirtualNode
  {
      /**
       * 待添加入Hash环的服务器列表
       */
     private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
             "192.168.0.3:111", "192.168.0.4:111"};

     /**
      * 真实结点列表,考虑到服务器上线、下线的场景，即添加、删除的场景会比较频繁，这里使用LinkedList会更好
      */
     private static List<String> realNodes = new LinkedList<>();
     
     /**
      * 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
      */
     private static SortedMap<int, String> virtualNodes = 
             new TreeMap<>();

     /**
      * 虚拟节点的数目，这里写死，为了演示需要，一个真实结点对应5个虚拟节点
      */
     private static final int VIRTUAL_NODES = 5;

     static
     {
         // 先把原始的服务器添加到真实结点列表中
         for (int i = 0; i < servers.length; i++)
             realNodes.add(servers[i]);

         // 再添加虚拟节点，遍历LinkedList使用foreach循环效率会比较高
         for (String str : realNodes)
         {
             for (int i = 0; i < VIRTUAL_NODES; i++)
             {
                 String virtualNodeName = str + "&VN" + String.valueOf(i);
                 int hash = getHash(virtualNodeName);
                 System.out.println("虚拟节点[" + virtualNodeName + "]被添加, hash值为" + hash);
                 virtualNodes.put(hash, virtualNodeName);
             }
         }
         System.out.println();
     }

     /** 
      * 使用 FNV1_32_HASH 算法计算服务器的Hash值,这里不使用重写 hashCode 的方法，最终效果没区别 
      */
     private static int getHash(String str)
     {
         final int p = 16777619;
         int hash = (int)2166136261L;
         for (int i = 0; i )
             hash = (hash ^ str.charAt(i)) * p;
         hash += hash ;
         hash ^= hash >> 7;
         hash += hash ;
         hash ^= hash >> 17;
         hash += hash ;

         // 如果算出来的值为负数则取其绝对值
         if (hash )
             hash = Math.abs(hash);
         return hash;
     }

     /**
      * 得到应当路由到的结点
      */
     private static String getServer(String node)
     {
         // 得到带路由的结点的Hash值
         int hash = getHash(node);
         // 得到大于该Hash值的所有Map
         SortedMap subMap = 
                 virtualNodes.tailMap(hash);
         // 第一个Key就是顺时针过去离node最近的那个结点
         Integer i = subMap.firstKey();
         // 返回对应的虚拟节点名称，这里字符串稍微截取一下
         String virtualNode = subMap.get(i);
         return virtualNode.substring(0, virtualNode.indexOf("&"));
     }

     public static void main(String[] args)
     {
         String[] nodes = {"127.0.0.1:1111", "221.226.0.1:2222", "10.211.0.1:3333"};
         for (int i = 0; i )
             System.out.println("[" + nodes[i] + "]的hash值为" + 
                     getHash(nodes[i]) + ", 被路由到结点[" + getServer(nodes[i]) + "]");
     }
 }
```



##### Set

Set 基本上都是由对应的 map 实现，简单看看就好

#### 常见问题

- hashmap 如何解决 hash 冲突，为什么 hashmap 中的链表需要转成红黑树？

  将 Key 的哈希值按数组大小取模后， 如果落在同一数组下标，JDK1.7 采用数组 + 链表， JDK1.8 采用数组 + 链表/红黑树的方法解决 hash 冲突。

  当某个桶对应的链表超过一定长度后， 通过链表的查询效率将会降低（get/put 方法都需要查询链表）， 采用红黑树将会有更低的时间复杂度。

- hashmap 什么时候会触发扩容？

  map 的元素总数超过 Entry 数组的 75 %。

- jdk1.8 之前并发操作 hashmap 时为什么会有死循环的问题？

- hashmap 扩容时每个 entry 需要再计算一次 hash 吗？

  JDK1.8 不用

- hashmap 的数组长度为什么要保证是 2 的幂？

  长度 16 或者其他 2 的幂，Length-1 的值是所有二进制位全为 1，这种情况下，index 的结果等同于 HashCode 后几位的值。只要输入的 HashCode 本身分布均匀，Hash 算法的结果就是均匀的。

- 如何用 LinkedHashMap 实现 LRU ？

- 如何用 TreeMap 实现一致性 hash ？

#### 线程安全的集合

##### Collections.synchronized

了解其实现原理

Collections.SynchronizedMap 底层实现原理：Collections 定义了一个SynchronizedMap 的内部类，并返回这个类的实例。SynchronizedMap 这个类实现了 Map 接口，**在调用方法时使用 synchronized 来保证线程同步**

Collections.synchronizedList： 返回一个带锁的数据结构。

##### CopyOnWriteArrayList

了解写时复制机制、了解其适用场景、思考为什么没有 ConcurrentArrayList

- 实现了 List 接口

- 内部持有一个 ReentrantLock lock = new ReentrantLock();

- 底层是用 volatile transient 声明的数组 array

- 读写分离，写时复制出一个新的数组，完成插入、修改或者移除操作后将新数组赋值给 array

  

使用场景

但在多线程中，由于在 notify 执行过程中 observers 数组的内容可能会发生改变，导致遍历失效.即使 observers 本身是线程安全的也于是无补。notify 的同步导致另外一个问题,即活跃性问题.当 observers 中有很多元素或者每一个元素的 notify 方法调用需要很久时，此方法将长时间持有锁.导致其他任何想修改 observers 的行为阻塞.最后严重影响程序性能。

CopyOnWriteArrayList 即在这种场景下使用.一个需要在多线程中操作，并且频繁遍历。

CopyOnWriteArrayList 只有在增删改是需要加锁， 读不需要加锁。

ArrayList 的底层是数组，所以查询的时候直接根据索引可以很快找到对应的元素，改也是如此，找到 index 对应元素进行替换。而增加和删除就涉及到数组元素的移动，所以会比较慢。

##### ConcurrentHashMap

了解实现原理、扩容时做的优化、与 HashTable 对比。

ConcurrentHashMap 是使用了锁分段技术来保证线程安全的。

**JDK1.7: 锁分段技术**：首先将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。 

ConcurrentHashMap 提供了与 Hashtable 和 SynchronizedMap 不同的锁机制。Hashtable 中采用的锁机制是一次锁住整个hash表，从而在同一时刻只能由一个线程对其进行操作；而 ConcurrentHashMap 中则是一次锁住一个桶。

ConcurrentHashMap 默认将 hash 表分为16个桶，诸如 get、put、remove 等常用操作只锁住当前需要用到的桶。这样，原来只能一个线程进入，现在却能同时有16个写线程执行，并发性能的提升是显而易见的。

**JDK1.8**: 主要使用了 Unsafe类的 CAS 自旋赋值+synchronized 同步+LockSupport 阻塞等手段实现的高效并发

##### BlockingQueue

了解 LinkedBlockingQueue、ArrayBlockingQueue、DelayQueue、SynchronousQueue

LinkedBlockingQueue：一个阻塞的线程安全的队列，底层采用链表实现。

ArrayBlockingQueue： 一个由数组结构组成的有界阻塞队列。

DelayQueue：一个使用优先级队列实现的无界阻塞队列。 

SynchronousQueue：一个不存储元素的阻塞队列。 

#### 常见问题

- ConcurrentHashMap 是如何在保证并发安全的同时提高性能？

  JDK1.7: 使用分离锁，减小了请求同一个锁的频率。
  通过 HashEntery 对象的不变性。

  及对同一个 Volatile 变量的读 / 写来协调内存可见性，使得 读操作大多数时候不需要加锁就能成功获取到需要的值。由于散列映射表在实际应用中大多数操作都是成功的 读操作，所以 2 和 3 既可以减少请求同一个锁的频率，也可以有效减少持有锁的时间。

- ConcurrentHashMap 是如何让多线程同时参与扩容？

  [深入分析ConcurrentHashMap1.8的扩容实现](<https://www.jianshu.com/p/f6730d5784ad>)

  JDK8: 主要使用了 Unsafe 类的 CAS 自旋赋值+synchronized 同步+LockSupport 阻塞等手段实现的高效并发

  JDK8 里面，去掉了分段锁，将锁的级别控制在了更细粒度的 table 元素级别，也就是说只需要锁住这个链表的 head节点，并不会影响其他的 table元素的读写，好处在于并发的粒度更细，影响更小，从而并发效率更好，但不足之处在于并发扩容的时候，由于操作的table都是同一个，好在 Doug lea 大神对扩容做了优化，本来在一个线程扩容的时候，如果影响了其他线程的数据，那么其他的线程的读写操作都应该阻塞，但 Doug lea 说你们闲着也是闲着，不如来一起参与扩容任务，这样人多力量大，办完事你们该干啥干啥，别浪费时间，于是在JDK8的源码里面就引入了一个 ForwardingNode 类，在一个线程发起扩容的时候，就会改变 sizeCtl 这个值，其含义如下：

  ~~~java
  ```  
  sizeCtl ：默认为0，用来控制table的初始化和扩容操作，具体应用在后续会体现出来。  
  -1 代表table正在初始化  
  -N 表示有N-1个线程正在进行扩容操作  
  其余情况：  
  1、如果table未初始化，表示table需要初始化的大小。  
  2、如果table初始化完成，表示table的容量，默认是table大小的0.75倍  
  ```  
  ~~~

  扩容时候会判断这个值，如果超过阈值就要扩容，首先根据运算得到需要遍历的次数i，然后利用 tabAt 方法获得i位置的元素 f，初始化一个 forwardNode 实例fwd，如果 f == null，则在 table 中的i位置放入 fwd，否则采用头插法的方式把当前旧 table 数组的指定任务范围的数据给迁移到新的数组中，然后 
  给旧table原位置赋值fwd。直到遍历过所有的节点以后就完成了复制工作，把table指向nextTable，并更新sizeCtl为新数组大小的0.75倍 ，扩容完成。在此期间如果其他线程的有读写操作都会判断head节点是否为forwardNode节点，如果是就帮助扩容。

- LinkedBlockingQueue、DelayQueue 是如何实现的？

- CopyOnWriteArrayList 是如何保证线程安全的？

  增删改的时候加锁

#### 并发

##### synchronized

了解偏向锁、轻量级锁、重量级锁的概念以及升级机制、以及和 ReentrantLock 的区别

[浅谈Java里的三种锁：偏向锁、轻量级锁和重量级锁](<https://blog.csdn.net/u012722531/article/details/78244786>)

**公平锁/非公平锁** ReentrantLock
公平锁和非公平锁的区别就在于，公平锁是 FIFO 机制，谁先来谁就在队列的前面，就能优先获得锁。非公平锁支持抢占模式，先来的不一定能得到锁。

##### CAS

了解 AtomicInteger 实现原理、CAS 适用场景、如何实现乐观锁

AtomicInteger 实现原理: 自旋锁+CAS 原子操作

CAS: 当要更新的变量（V）的实际值等于变量的预期值（E）时，将V的值设置为新的值（U）；如果V和E的值不同，则说明已经有其他的线程对变量做了更新操作，则当前线程更新失败。

**1. 获取 volatile 修饰的变量，最新的主存值**

**2. value+1 作为自增值**

**3. compare value 是否就是主存值，是，set next，return next；否，循环下一次**

```java
public final int incrementAndGet() {
    //自旋锁
    for (;;) {
        //获取volatitle修饰的变量，最新的主存值
        int current = get();
        //理论上自增值
        int next = current + 1;        
        if (compareAndSet(current, next))
            return next;
    }
}
 
 
public final boolean compareAndSet(int expect, int update) {
    return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}
```

优缺点

AtomicInteger 的优点

1.乐观锁，性能较强，利用 CPU 自身的特性保证原子性，即 CPU 的指令集封装 compare and swap两个操作为一个指令来保证原子性。

2.适合读多写少模式

但是缺点明显

1.自旋，消耗 CPU 性能，所以写的操作较多推荐 sync

2.仅适合简单的运算，否则会产生 ABA 问题，自旋的时候，别的线程可能更改 value，然后又改回来，此时需要加版本号解决，JDK 提供了AtomicStampedReference 和 AtomicMarkableReference 解决ABA问题，提供基本数据类型和引用数据类型版本号支持

使用 CAS 在线程冲突严重时，会大幅降低程序性能；CAS只适合于线程冲突较少的情况使用。

如何实现乐观锁: 自旋锁+CAS 原子操作

CAS 问题：

-  *ABA问题：*JDK 提供了AtomicStampedReference 和 AtomicMarkableReference 解决ABA问题
- **循环时间长开销大**：自旋 CAS（不成功，就一直循环执行，直到成功）如果长时间不成功，会给 CPU带来非常大的执行开销。
- **只能保证一个共享变量的原子操作**：Java1.5开始JDK提供了 **AtomicReference** 类来保证引用对象之间的原子性，你可以把多个变量放在一个对象里来进行CAS操作

##### AQS

了解 AQS 内部实现、及依靠 AQS 的同步类比如 ReentrantLock、Semaphore、CountDownLatch、CyclicBarrier 等的实现

[AQS实现原理](<https://blog.csdn.net/ym123456677/article/details/80381354>)

##### ThreadLocal

了解 ThreadLocal 使用场景和内部实现

##### ThreadPoolExecutor

了解线程池的工作原理以及几个重要参数的设置

#### 常见问题

- synchronized 与 ReentrantLock 的区别？

  1）Lock 是一个接口，而 synchronized 是 Java 中的关键字，synchronized 是内置的语言实现；

  2）synchronized 在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象发生；而 Lock 在发生异常时，如果没有主动通过 unLock() 去释放锁，则很可能造成死锁现象，因此使用 Lock 时需要在 finally 块中释放锁；

  3）Lock 可以让等待锁的线程响应中断，而 synchronized 却不行，使用 synchronized 时，等待的线程会一直等待下去，不能够响应中断；

  4）通过 Lock 可以知道有没有成功获取锁，而 synchronized 却无法办到。

  5）Lock 可以提高多个线程进行读操作的效率。

  

- 乐观锁和悲观锁的区别？

  锁从宏观上分类，分为悲观锁与乐观锁

  **乐观锁** 
  　　乐观锁是一种乐观思想，即认为读多写少，遇到并发写的可能性低，每次去拿数据的时候都认为别人不会修改，所以不会上锁，但是在更新的时候会判断一下在此期间别人有没有去更新这个数据，采取在写时先读出当前版本号，然后加锁操作（比较跟上一次的版本号，如果一样则更新），如果失败则要重复读-比较-写的操作。 
  　　java中的乐观锁基本都是通过 CAS 操作实现的，CAS 是一种更新的原子操作，比较当前值跟传入值是否一样，一样则更新，否则失败。

  **悲观锁**

  　　悲观锁是就是悲观思想，即认为写多，遇到并发写的可能性高，每次去拿数据的时候都认为别人会修改，所以每次在读写数据的时候都会上锁，这样别人想读写这个数据就会 block 直到拿到锁。java 中的悲观锁就是 Synchronized

- 如何实现一个乐观锁？

  自旋锁+CAS 原子操作

- AQS 是如何唤醒下一个线程的？

- ReentrantLock 如何实现公平和非公平锁是如何实现？

- CountDownLatch 和 CyclicBarrier 的区别？各自适用于什么场景？

- 适用 ThreadLocal 时要注意什么？比如说内存泄漏?

- 说一说往线程池里提交一个任务会发生什么？

- 线程池的几个参数如何设置？

- 线程池的非核心线程什么时候会被释放？

- 如何排查死锁？

推荐文章：

[死磕 Synchronized 底层实现--概论](https://github.com/farmerjohngit/myblog/issues/12)（比较深入）

#### 引用

了解 Java 中的软引用、弱引用、虚引用的适用场景以及释放机制

#### 常见问题

- 软引用什么时候会被释放
- 弱引用什么时候会被释放

推荐文章：

[Java 引用类型原理剖析](https://github.com/farmerjohngit/myblog/issues/10)（比较深入）

#### 类加载

了解双亲委派机制

[通俗易懂的双亲委派机制](<https://blog.csdn.net/codeyanbao/article/details/82875064>)

#### 常见问题

- 双亲委派机制的作用？

  防止系统级类被修改

- Tomcat 的 classloader 结构

  [Tomcat源码分析之ClassLoader部分的设计详细分析](<https://blog.csdn.net/fjslovejhl/article/details/21328347>)

- 如何自己实现一个 classloader 打破双亲委派

  重写 classloader 的 findClass()方法

#### IO

了解 BIO 和 NIO 的区别、了解多路复用机制

#### 常见问题

- 同步阻塞、同步非阻塞、异步的区别？

  同步阻塞： **执行一个操作之后，等待结果**，不能处理其他请求

  同步非阻塞：执行一个操作之后，等待结果，继续处理其他请求

- select、poll、eopll 的区别？

- java NIO 与 BIO 的区别？

- refactor 线程模型是什么?

