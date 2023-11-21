## getComponentType

获取数组对应的元素的类，如果不是数组则返回 null

```java
Class<?> clazz = String[].class.getComponentType();
```

```java
java.lang.String
```



## getSimpleName

获取类的简单名称

```java
User.class.getSimpleName()
```

输出字符串 User

### Comparable<?>

继承自 Comparable 接口的某个类的实例的修饰符

### forName

方法返回与给定字符串名的类或接口的Class对象，使用给定的类加载器。

name: 类的字符串名, 如 com.example.demo.broker.jdk.Animal

initialize：这说明这个类是否必须初始化

loader: 类加载器

```java
Class.forName(String name, boolean initialize, ClassLoader loader)
```

### invoke

invoke(Object obj, Object... args) 是 method 类中的方法，这个方法是一个native 方法

 obj: 调用类的实例对象

 args:调用发方法的参数，是可变长度的
