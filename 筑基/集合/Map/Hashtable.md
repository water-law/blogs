# Hashtable

初始大小 11， 键和值都不允许为 null, 因为没有特殊处理

```java
int index = (hash & 0x7FFFFFFF) % tab.length;
```