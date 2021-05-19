# 转载：Java引用

[Java 引用](https://www.cnblogs.com/wyq178/p/9427987.html)

前言：

**在平时的开发中,我们每天要 new 无法的对象，这些对象存在于 jvm 的堆内存中，而他们的生老病死生命周期全部归JVM控制。不同的对象引用的，其生命周期也有显著的不同，如何通过其他不用强度的引用来避免 jvm 的最大隐患：out of Memory？本篇博客将会介绍 java 的四种不同的引用类型，来看一下在GC的前后他们将会经历什么样的变化？经历怎样的事情？**

**目录**

**java 引用强度图**

**一：虚引用**

**二：弱引用**

**三：软引用**

**四：强引用**

**java引用强度图**





**1：虚引用**

 1.1 简介：虚引用是所有引用中强度最弱的，它完全类似于没有引用，在 java.reflact.PhantomReference 类中实现。虚引用对象本身没有太大影响，对象甚至感觉不到虚引用的存在。如果一个对象存在虚引用，那么它和没有引用的效果大致相同，虚引用无法引用任何堆中的对象

作用：虚引用主要用于跟踪对象被 JVM 垃圾回收的状态，可以通过它来收集 GC 的行为。可以通过检查与虚引用关联的引用队列中是否已经包含指定的虚引用，从而了解虚引用所引用的对象是否被回收。

注意：虚引用无法单独使用，虚引用必须和引用队列（ReferenceQueue）联合使用.被虚引用所引用对象被垃圾回收后，虚引用将被添加到引用队列中。

```java
public static void  main(String[] args) {
        
        //引用队列
        ReferenceQueue<String> rq = new ReferenceQueue<>();

        PhantomReference<String>  phantomReference= new PhantomReference<>(new String("test"), rq);

        System.out.println("GC前:"+phantomReference.get());

        System.gc();

        System.runFinalization();

        Reference<? extends String> reference = rq.poll();
        
        System.out.println(reference==phantomReference);

    }
```

