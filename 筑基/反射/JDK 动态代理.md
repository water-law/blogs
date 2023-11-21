## JDK 动态代理

### Animal 接口

```java
public interface Animal {
    void call();
}
```

### Cat 实现类

```java
public class Cat implements Animal {

    @Override
    public void call() {
        System.out.println("喵喵喵 ~");
    }
}
```



### 实现 InvocationHandler 接口

定义一个 InvocationHandler 的实现类，重写 invoke 方法



```java
public class TargetInvoker implements InvocationHandler {
    // 代理中持有的目标类
    private Object target;

    public TargetInvoker(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("jdk 代理执行前");
        Object result = method.invoke(target, args);
        System.out.println("jdk 代理执行后");
        return result;
    }
}
```



### 创建 JDK 动态代理类

```java
public class DynamicProxyAnimal {

    public static Object getProxy(Object target) throws Exception {
        Object proxy = Proxy.newProxyInstance(
                target.getClass().getClassLoader(), // 指定目标类的类加载
                target.getClass().getInterfaces(),  // 代理需要实现的接口，可指定多个，这是一个数组
                new TargetInvoker(target)   // 代理对象处理器
        );
        return proxy;
    }
}
```

`Proxy#newProxyInstance`中的三个参数（ClassLoader loader、Class<?>[] interfaces、InvocationHandler h）：

- loader 加载代理对象的类加载器
- interfaces 代理对象实现的接口，与目标对象实现同样的接口
- h 处理代理对象逻辑的处理器，即上面的 InvocationHandler 实现类

### 

### JdkMain

```java
public class JdkMain{
    public static void main(String[] args) throws Exception {
        Cat cat = new Cat();
        Animal proxy = (Animal) DynamicProxyAnimal.getProxy(cat);
        proxy.call();
    }
}
```

