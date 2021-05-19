---
title: MySQL 基本介绍及架构组成
date: 2019-04-09 20:32:51
categories:
  - 数据库
tags:
  - MySQL
---

### 与其他数据库的区别

<!--more-->

| 数据库 |                            MySQL                             |                 Postgres                 |                       Oracle                       |
| ------ | :----------------------------------------------------------: | :--------------------------------------: | :------------------------------------------------: |
| 功能   | 基本实现了 ANSI SQL 92 的大部分标准，第三方插件式存储引擎，可编程支持方面不足 | 字段类型支持方面最完整，支持四种隔离级别 | 仅实现了其中的两种（Serializable和Read Commited ） |
| 易用性 |                           简单易用                           |                                          |                                                    |
| 性能   |      在性能和功能方面，MySQL 第一考虑的要素主要还是性能      |                                          |                      性能较好                      |
| 可靠性 |                                                              |                                          |                                                    |

### MySQL Server 系统架构



MySQL 可以看成二层架构， 

1. 第一层我们通常叫做 SQL Layer，在 MySQL 数据库系统处理底层数据之前的所有工作都是在这一层完成的，包括权限判断，sql 解析，执行计划优化，query cache 的处理等等；
2. 第二层就是存储引擎层，我们通常叫做 StorageEngine Layer，也就是底层数据存取操作实现部分，由多种存储引擎共同组成。

{% asset_img "SQL 逻辑层.png" "SQL 逻辑层" %}



### MySQL 自带工具使用介绍

1. mysql
2. mysqladmin: 提供的功能都是与 MySQL 管理相关的各种功能。
3. mysqldump
4. mysqlimport
5. mysqlbinlog
6. mysqlcheck
7. myisamchk
8. myisampack
9. mysqlhotcopy