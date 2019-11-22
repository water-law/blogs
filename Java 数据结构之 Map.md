<!--more-->

#### 集合

##### Map

HashMap：了解其数据结构、hash 冲突如何解决（链表和红黑树）、扩容时机、扩容时避免 rehash 的优化

- 底层数组+链表实现，可**以存储 null 键和 null 值**，线程**不安全**
- 初始 size 为 **16**，扩容：newsize = oldsize*2，size 一定为2的n次幂
- 扩容针对整个 Map，每次扩容时，原来数组中的元素依次重新计算存放位置，并重新插入
- 插入元素后才判断该不该扩容，有可能无效扩容（插入后如果扩容，如果没有再次插入，就会产生无效扩容）
- 当 Map 中元素总数超过 Entry 数组的 75%，触发扩容操作，为了减少链表长度，元素分配更均匀
- 计算 index 方法：index = hash & (tab.length – 1)

HashMap 的初始值还要考虑加载因子:

- **哈希冲突**：若干 Key 的哈希值按数组大小取模后，如果落在同一个数组下标上，将组成一条 Entry 链，对 Key 的查找需要遍历 Entry 链上的每个元素执行 equals() 比较。

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

##### ConcurrentHashMap

了解实现原理、扩容时做的优化、与 HashTable 对比。

ConcurrentHashMap 是使用了锁分段技术来保证线程安全的。

**JDK1.7: 锁分段技术**：首先将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。 

ConcurrentHashMap 提供了与 Hashtable 和 SynchronizedMap 不同的锁机制。Hashtable 中采用的锁机制是一次锁住整个hash表，从而在同一时刻只能由一个线程对其进行操作；而 ConcurrentHashMap 中则是一次锁住一个桶。

ConcurrentHashMap 默认将 hash 表分为16个桶，诸如 get、put、remove 等常用操作只锁住当前需要用到的桶。这样，原来只能一个线程进入，现在却能同时有16个写线程执行，并发性能的提升是显而易见的。

**JDK1.8**: 主要使用了 Unsafe类的 CAS 自旋赋值+synchronized 同步+LockSupport 阻塞等手段实现的高效并发

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


