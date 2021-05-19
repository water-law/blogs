---
title: Spring
date: 2019-04-09 22:09:15
categories:
  - 框架
tags:
  - Spring
---

bean 的生命周期、循环依赖问题、spring cloud （如项目中有用过）、AOP 的实现、spring 事务传播

<!--more-->

[Spring 事务传播行为详解](https://segmentfault.com/a/1190000013341344)

#### 常见问题

- java 动态代理和 cglib 动态代理的区别（经常结合 spring 一起问所以就放这里了）
  静态代理	简单，代理模式，是动态代理的理论基础。常见使用在代理模式
  jdk动态代理	需要有顶层接口才能使用，但是在只有顶层接口的时候也可以使用，常见是mybatis的mapper文件是代理。

  使用反射完成。使用了动态生成字节码技术。

  cglib 动态代理	可以直接代理类，使用字节码技术，不能对 final 类进行继承。使用了动态生成字节码技术。

  

- spring 中 bean 的生命周期是怎样的？

  [JAVA 面试题：Spring 中 bean 的生命周期](https://www.cnblogs.com/kenshinobiy/p/4652008.html)

- 属性注入和构造器注入哪种会有循环依赖的问题？

  Spring 容器对构造函数配置 Bean 进行实例化有一个前提，即 *Bean 构造函数入参引用的对象必须已经准备就绪*。由于这个机制，如果两个 Bean 都相互引用，都采用构造函数注入方式，就会发生类似于线程死锁的循环依赖问题。

  如何解决这种问题？*将相互依赖的两个 Bean 中的其中一个 Bean 采用 Setter 注入的方式即可。*