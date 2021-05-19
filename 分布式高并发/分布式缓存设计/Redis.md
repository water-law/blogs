---
title: Redis
date: 2019-04-09 22:10:55
categories:
  - 缓存
tags:
  - Redis
---

redis 工作模型、redis 持久化、redis 过期淘汰机制、redis 分布式集群的常见形式、分布式锁、缓存击穿、缓存雪崩、缓存一致性问题

<!--more-->

推荐书籍：《*Redis* 设计与实现》

推荐文章：

<https://github.com/farmerjohngit/myblog/issues/1>

<https://github.com/farmerjohngit/myblog/issues/2>

<https://github.com/farmerjohngit/myblog/issues/5>



#### 常见问题

- redis 性能为什么高?

  1、完全基于内存，绝大部分请求是纯粹的内存操作，非常快速。数据存在内存中，类似于HashMap，HashMap的优势就是查找和操作的时间复杂度都是O(1)；

  2、数据结构简单，对数据操作也简单，Redis中的数据结构是专门进行设计的；

  3、采用单线程，避免了不必要的上下文切换和竞争条件，也不存在多进程或者多线程导致的切换而消耗 CPU，不用去考虑各种锁的问题，不存在加锁释放锁操作，没有因为可能出现死锁而导致的性能消耗；

  4、使用多路I/O复用模型，非阻塞IO；

  5、使用底层模型不同，它们之间底层实现方式以及与客户端之间通信的应用协议不一样，Redis直接自己构建了VM 机制 ，因为一般的系统调用系统函数的话，会浪费一定的时间去移动和请求；

- 单线程的 redis 如何利用多核 cpu 机器？

  开启多个实例

- redis 的缓存淘汰策略？

- redis 如何持久化数据？

- redis 有哪几种数据结构？

  [redis的五种数据结构及其使用场景](https://www.cnblogs.com/ottll/p/9470480.html)

- redis 集群有哪几种形式？

- 有海量 key 和 value 都比较小的数据，在 redis 中如何存储才更省内存？

- 如何保证 redis 和 DB 中的数据一致性？

  使用消息队列更新缓存， 消费端保证只有一个线程顺序消费消息即可

- 如何解决缓存穿透和缓存雪崩？

  [缓存穿透、缓存击穿、缓存雪崩区别和解决方案](<https://blog.csdn.net/kongtiao5/article/details/82771694>)

- 如何用 redis 实现分布式锁？