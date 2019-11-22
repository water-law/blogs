---
title: MySQL 存储引擎
date: 2019-04-09 21:41:11
categories:
  - 数据库
tags:
  - MySQL
---

### 插件式存储引擎

<!--more-->

 MyISAM，Innodb，NDB Cluster，Maria，Falcon，Memory ， Archive ， Merge ， Federated 等， 其中最著名而且使用最为广泛的 MyISAM 和 Innodb
两种存储引擎。

#### MyISAM 存储引擎简介

MyISAM 支持以下三种类型的索引：B-Tree 索引、R-Tree 索引、Full-text 索引

适合读多写少的情况。

#### Innodb 存储引擎简介



1. 支持事务安装

2. 数据多版本读取

   Innodb 在事务支持的同时，为了保证数据的一致性已经并发时候的性能，通过对 undo 信息，实现了数据的多版本读取。

3. 锁定机制的改进

   Innodb 改变了 MyISAM 的锁机制，实现了行锁。

4. 实现外键

5. 物理存储

   表数据和索引数据是存放在一起的。

