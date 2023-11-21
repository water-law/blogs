## newInstance

新建 String 数组，长度为 10

```java
Array.newInstance(String.class, 10);
```

## set

```java
Array.set(obj, i, value);
```

```java
        Object obj = Array.newInstance(String.class, 10);
        for(int i = 0; i < 10; i++) {
            Array.set(obj, i, i+"");
        }
```



### get

```java
Array.get(obj, i);
```



### getLength

```java
Array.getLength(obj);
```

