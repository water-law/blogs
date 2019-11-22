# Redis 实现分布式锁

利用 redis 的 setnx 指令， 实现分布式锁， 因为 redis 是单线程的， setnx 指令在键不存在时设置键值并返回 “OK” ，否则忽略新值并返回 null, 设置失败。

#### 在 maven 项目中使用 jedis

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.0.1</version>
</dependency>
```



#### 简单使用

```java
package top.waterlaw.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

public class JedisTest {


    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);
//      setnx: key 不存在时返回 1， 否则返回 0
//        Long i = jedis.setnx("nxketx", "test");
//        System.out.println(i);
        SetParams params = SetParams.setParams().nx().px(4000);
        String s1 = jedis.set("nx", "nxv", params); // 返回 "OK"
        System.out.println(s1);
        String s2 = jedis.set("nx", "nxv2", params); // 返回 null, 设置失败
        System.out.println(s2);


        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        String lock_key = "nx";
        String id = "nxv";
        Object result = jedis.eval(script, Collections.singletonList(lock_key),
                Collections.singletonList(id));
        System.out.println(result);

        String num = jedis.set("num", "0");
        System.out.println(num);
        System.out.println(jedis.incr("num"));
        System.out.println(jedis.eval("redis.call('del', 'xx')"));
        jedis.close();
    }
}

```

#### setnx 指令

jedis 中 setnx 指令为 jedis.set(Key, value, params)

```java
SetParams params = SetParams.setParams().nx().px(10000000); // 单位是毫秒
String status = jedis.set(Key, value, params); // set 成功后返回字符串 "OK"
```

#### 使用 lua 脚本释放锁

jedis 释放锁则需要使用 lua 脚本，将 lockKey = value 的键删除

```java
String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        String value = "";
        
Object result = jedis.eval(script, Collections.singletonList(lockKey),
                        Collections.singletonList(value));     // 删除成功时返回 1   
```

因为可能有多个线程，所以由哪个线程申请的锁，得由该线程释放， 所以我们的类需要一个 ThreadLocal 变量存放线程名字。

#### redis 分布式锁类

我将分布式锁类设置为可重入， 实现 Lock 接口， 成员变量是锁定的 redis 键， 也是分布中多线程竞争的对象，大概实现如下：

```java
package top.waterlaw.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class RedisDistributedLock implements Lock {

    private ThreadLocal<String> exclusiveOwnerThread = new ThreadLocal<>();
    private String lockKey;
    private long internalLockLeaseTime; // 锁过期时间
    private long time = 1000; // 获取锁超时时间

    public RedisDistributedLock(String lockKey, long internalLockLeaseTime) {
        this.lockKey = lockKey;
        this.internalLockLeaseTime = internalLockLeaseTime;
    }

    public void lock() {
        while (!tryLock()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock() {
        try {
            return tryLock(time, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        Jedis jedis = new Jedis("localhost", 6379);
        String value = "";
        Long start = System.currentTimeMillis();
        // 获取分布式锁
        Thread t = Thread.currentThread();
        exclusiveOwnerThread.set(t.getName());
        SetParams params = SetParams.setParams().nx().px(this.internalLockLeaseTime);
        try {
            for(;;) {
                String status = jedis.set(this.lockKey, value, params);
                if("OK".equals(status)) {
                    System.out.println("tryLock SUCCESS "+exclusiveOwnerThread.get());
                    return true;
                }
                long l = System.currentTimeMillis() - start;
                if(l >= time) {
                    System.out.println("tryLock FAILED "+exclusiveOwnerThread.get());
                    return false;
                }
                Thread.sleep(100);
            }
        }finally {
            jedis.close();
        }
    }

    public void unlock() {
        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        String value = "";
        // 删除分布式锁
        // 需要判断所得所有者
        Thread t = Thread.currentThread();
        if(exclusiveOwnerThread.get().equals(t.getName())) {
            Jedis jedis = new Jedis("localhost", 6379);
            // 删除键值
            try {
                Object result = jedis.eval(script, Collections.singletonList(lockKey),
                        Collections.singletonList(value));
                if("1".equals(result.toString())){
                    // 成功
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                jedis.close();
            }
        }
    }

    public Condition newCondition() {
        return null;
    }
}

```

#### redis 锁测试

我们来测试下吧，

```java
package top.waterlaw.redis;

import redis.clients.jedis.Jedis;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class LockTest {
    static int thread_num = 1000;
    static CountDownLatch downLatch = new CountDownLatch(thread_num);
    static AtomicInteger fail = new AtomicInteger(0);

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.set("num", "0");
        RedisDistributedLock rdl = new RedisDistributedLock("lock", 50);
        for (int i = 0; i < thread_num; i++) {
            new Thread(() -> {
                boolean hasLock = rdl.tryLock();
                if(hasLock) {
                    try {
                        jedis.incr("num"); // 此函数执行时间和锁的过期时间应该要接近
                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    finally {
                        rdl.unlock();
                    }
                }
                else {
                    fail.incrementAndGet();
                }
                downLatch.countDown();
            }).start();
        }
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("---------------------------------");
        System.out.println(fail.get());
        try {
            System.out.println(jedis.get("num"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("---------------------------------");
        jedis.close();
    }
}

```

#### redis 锁优劣分析

jedis 实现的分布式锁比较不成熟， 生产环境中可以使用 redisson,  也是 tryLock, unlock

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.11.1</version>
</dependency>
```

由 redis 实现的分布式锁具有先到先得的特点， 因此在实际中适用于抢购特定数量的优惠券，用于抽奖活动这类的应用场景可能效果不是很好。

#### 代码位置

[github]( https://github.com/water-law/javasonar/tree/master/src/main/java/top/waterlaw/redis )