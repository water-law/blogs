---
title: RocketMq
date: 2019-04-09 22:10:13
categories:
  - 消息队列
tags:
  - RocketMq
---

了解一个常用消息中间件如 RocketMq 的实现：如何保证高可用和高吞吐、消息顺序、重复消费、事务消息、延迟消息、死信队列

<!--more-->

#### 常见问题

- RocketMq 如何保证高可用的？
- RocketMq 如何保证高吞吐的？
- RocketMq 的消息是有序的吗？
- RocketMq 的消息局部顺序是如何保证的?
- RocketMq 事务消息的实现机制？
- RocketMq 会有重复消费的问题吗？如何解决？
- RocketMq 支持什么级别的延迟消息？如何实现的？
- RocketMq 是推模型还是拉模型？
- Consumer 的负载均衡是怎么样的？