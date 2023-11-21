## Mybatis 中的常用知识

### 读取 properties 文件

```java
	private static Properties getInputStream( String cfg ) {
		return getInputStream(TestDBConnection.class.getClassLoader().getResourceAsStream(cfg));
	}


	private static Properties getInputStream( InputStream in ) {
		Properties p = null;
		try {
			p = new Properties();
			p.load(in);
		} catch ( Exception e ) {
			logger.severe(" kisso read config file error. ");
			e.printStackTrace();
		}
		return p;
	}
	String DB_CONFIG = "dbconfig.properties";
	getInputStream(DB_CONFIG);
```



### 获取 bean 实体类信息

bean 实体类

```java
package com.baomidou.mybatisplus.test;


public class TestUser {

	private Long id;

	private String name;

	private int age;


	public Long getId() {
		return id;
	}


	public void setId( Long id ) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName( String name ) {
		this.name = name;
	}


	public int getAge() {
		return age;
	}


	public void setAge( int age ) {
		this.age = age;
	}


}
```





```java
package com.baomidou.mybatisplus.test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanTest {


    public static void main(String[] args) {
        Class<?> cls = TestUser.class;
        TestUser bean = new TestUser();
        bean.setId(90L);
        bean.setAge(12);
        bean.setName("zjp");
        BeanInfo beanInfo = null;
        // 获取 bean 的属性
        try {
            beanInfo = Introspector.getBeanInfo(cls, Object.class);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for ( PropertyDescriptor pd : pds ) {
                // 获取属性的 get 方法
                Method getter = pd.getReadMethod();
                String name = pd.getName();
                Object value = getter.invoke(bean);
                // 获取属性的 set 方法
                // Method setter = pd.getWriteMethod();
                // setter.invoke(bean, "12");
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
```



### 获取数据库查询结果集的第 N 列

```java
ResultSetHandler<T> rs = stmt.executeQuery()
ResultSetMetaData rsmd = rs.getMetaData();
// 传 1 代表第一列
String columnName = rsmd.getColumnLabel(1);
```



### 非基本类型判断

基本类型包含 char int byte short long float double boolean

```java
Class.isPrimitive()
```



### Enum 方法

```java
package com.baomidou.mybatisplus.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumTest {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> enumType = Enum.class;
        java.lang.Enum<?>[] elements = (java.lang.Enum<?>[]) enumType.getMethod("values")
                .invoke(enumType);
        System.out.println(elements[0]);
        System.out.println(String.valueOf(elements[0].ordinal()));
        System.out.println(elements[0].toString());
        Method readMethod = enumType.getMethod("name");
        System.out.println(readMethod.invoke(elements[0]));
    }
}


enum Enum {
    ONE("23", 12),
    TWO("33", 13),
    THREE("2444", 15);
    String type;
    Integer value;
    Enum(String type, Integer value) {
        this.value = value;
    }
}
```

输出结果如下：

```shell
ONE
0
ONE
ONE
```

### values(), ordinal() 和 valueOf() 方法

enum 定义的枚举类默认继承了 java.lang.Enum 类，并实现了 java.lang.Serializable 和 java.lang.Comparable 两个接口。

values(), ordinal() 和 valueOf() 方法位于 java.lang.Enum 类中：

- values() 返回枚举类中所有的值。
- ordinal()方法可以找到每个枚举常量的索引，就像数组索引一样。
- valueOf()方法返回指定字符串值的枚举常量。

name() 方法为 java.lang.Enum 类方法。



### 驼峰和下划线转换

```java
	// 驼峰转下划线
  public String camel2underscore(String camel) {
		camel = camel.replaceAll("([a-z])([A-Z])", "$1_$2");
		return camel.toLowerCase();
	}
	
  // 下划线转驼峰
	public String underscore2camel(String underscore) {
		if (!underscore.contains("_")) {
			return underscore;
		}
		StringBuffer buf = new StringBuffer();
		underscore = underscore.toLowerCase();
		Matcher m = Pattern.compile("_([a-z])").matcher(underscore);
		while (m.find()) {
			m.appendReplacement(buf, m.group(1).toUpperCase());
		}
		return m.appendTail(buf).toString();
	}
```

appendReplacement(StringBuffer sb, String replaceContext)

appendReplacement 方法：sb 是一个 StringBuffer，replaceContext 待替换的字符串，这个方法会把匹配到的内容替换为replaceContext，并且把从上次替换的位置到这次替换位置之间的字符串也拿到，然后，加上这次替换后的结果一起追加到 StringBuffer 里（假如这次替换是第一次替换，那就是追加替换后的字符串及其匹配之前的所有字符）。

appendReplacement(StringBuffer sb)

appendTail 方法：sb 是一个 StringBuffer，这个方法是把最后一次匹配到内容之后的字符串追加到 StringBuffer 中。