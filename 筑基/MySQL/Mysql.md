---
title: Mysql
date: 2019-04-09 22:12:14
tags:
---

事务隔离级别、锁、索引的数据结构、聚簇索引和非聚簇索引、最左匹配原则、查询优化（ explain 等命令）

<!--more-->

推荐文章： <http://hedengcheng.com/?p=771>

<https://tech.meituan.com/2014/06/30/mysql-index.html>

<http://hbasefly.com/2017/08/19/mysql-transaction/>



#### 常见问题

- Mysql(innondb 下同) 有哪几种事务隔离级别？
- 不同事务隔离级别分别会加哪些锁？
- mysql 的行锁、表锁、间隙锁、意向锁分别是做什么的？
- 说说什么是最左匹配？
- 如何优化慢查询？
- mysql 索引为什么用的是 b+ tree 而不是 b tree、红黑树
- 分库分表如何选择分表键
- 分库分表的情况下，查询时一般是如何做排序的？