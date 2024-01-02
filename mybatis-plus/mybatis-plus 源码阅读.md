## 源码

我这次从最开始的版本开始看，还是使用 Maven,  用 Gradle 版本最好高一点，如 6.9.3

```sh
git clone https://github.com/baomidou/mybatis-plus.git
```

### 插件

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>2.0.7</version>
</dependency>
```

### MySQL 8.0

jdbc 驱动类：com.mysql.jdbc.Driver 改成 com.mysql.cj.jdbc.Driver

```xml
	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<mybatis.version>3.5.3</mybatis.version>
		<druid.version>1.1.6</druid.version>
		<mysql-connector-java.version>8.0.17</mysql-connector-java.version>
	</properties>
		<dependencies>
		<dependency>
			<groupId>org.mybatis</groupId>
			<artifactId>mybatis</artifactId>
			<version>${mybatis.version}</version>
		</dependency>

		<!-- test begin -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>${druid.version}</version>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>${mysql-connector-java.version}</version>
		</dependency>
		<!-- test end -->
	</dependencies>
```



### Application.properties

```properties
mybatis-plus.mapper-locations=classpath:mybatis/mapper/*.xml
```

### 数据库配置

```properties
jdbc.url=jdbc:mysql://localhost:3306/mybatis_plus?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
jdbc.password=rl6174zjp
```

### mybatisplus

com.baomidou.mybatisplus.test.mysql.config.DBConfig: 测试数据库配置



### mybatis-plus-core

com.baomidou.mybatisplus.core



InjectorResolver: 继承自 mybatis 的 MethodResolver

封装了解析器和方法, 因此这是一个具有了方法解析功能的方法包装类

```java
public class InjectorResolver extends MethodResolver {

    private final MybatisMapperAnnotationBuilder annotationBuilder;

    public InjectorResolver(MybatisMapperAnnotationBuilder annotationBuilder) {
        super(annotationBuilder, null);
        this.annotationBuilder = annotationBuilder;
    }

    @Override
    public void resolve() {
        annotationBuilder.parserInjector();
    }
}
```

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



## Mybatis 中的常用知识

### 打开资源管理器

```java
		/**
		 * 自动打开生成文件的目录
		 * <p>
		 * 根据 osName 执行相应命令
		 * </p>
		 */
		try {
			String osName = System.getProperty("os.name");
			if (osName != null) {
				if (osName.contains("Mac")) {
					Runtime.getRuntime().exec("open " + config.getSaveDir());
				} else if (osName.contains("Windows")) {
					Runtime.getRuntime().exec("cmd /c start " + config.getSaveDir());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
```

从这里我们学到了 mac 系统中 open 指令可以打开资源管理器，而 windows 中的指令是 cmd /c start



### File

```java
File mapperFile = new File(this.getFileName("mapper"), mapperName + ".java");
```

File(parent, child): 可以在 parent 目录下创建 child 文件



### 文件写入

```java
BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile), "utf-8"));
bw.write("package " + config.getMapperPackage() + ";");
bw.newLine();
bw.write("}");
bw.flush();
bw.close();
```



### 获取类下的所有字符列表

```java
	/**
	 * 获取该类的所有字符列表
	 * 
	 * @param clazz
	 *            反射类
	 * @return
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		List<Field> result = new LinkedList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			result.add(field);
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass.equals(Object.class)) {
			return result;
		}
		result.addAll(getAllFields(superClass));
		return result;
	}

	/**
	 * 获取该类的所有字符列表，排查 Transient 类型的字段
	 * 
	 * @param clazz
	 *            反射类
	 * @return
	 */
	public static List<Field> getAllFieldsExcludeTransient(Class<?> clazz) {
		List<Field> result = new LinkedList<Field>();
		List<Field> list = getAllFields(clazz);
		for (Field field : list) {
			if (Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			result.add(field);
		}
		return result;
	}
```



# Java中的Type类型

在 Java 编程语言中，`Type`是所有类型的父接口。包括：

1. 原始类型（raw types），对应`Class`实现类
2. 参数化类型（parameterized types），对应`ParameterizedType`接口
3. 泛型数组类型（array types），对应`GenericArrayType`接口
4. 类型变量（type variables），对应`TypeVariable`接口
5. 基本类型（primitive types），对应`Class`实现类
6. 通配符类型（wildcard types），对应`WildcardType`接口



```java
public class Main {

    public static void main(String[] args) throws NoSuchMethodException, SecurityException {
        Method method = Main.class.getMethod("testType",
                List.class, List.class, List.class, List.class, List.class, Map.class);

        // 按照声明顺序返回`Type对象`的数组
        Type[] types = method.getGenericParameterTypes();

        for (int i = 0; i < types.length; i++) {
            // 最外层都是ParameterizedType
            ParameterizedType pType = (ParameterizedType) types[i];
            // 返回表示此类型【实际类型参数】的`Type对象`的数组
            Type[] actualTypes = pType.getActualTypeArguments();
            for (int j = 0; j < actualTypes.length; j++) {
                Type actualType = actualTypes[j];
                System.out.print("(" + i + ":" + j + ")  类型【" + actualType + "】");
                if (actualType instanceof Class) {
                    System.out.println(" -> 类型接口【" + actualType.getClass().getSimpleName() + "】");
                } else {
                    System.out.println(" -> 类型接口【" + actualType.getClass().getInterfaces()[0].getSimpleName() + "】");
                }
            }
        }
    }

    public <T> void testType(List<String> a1,
                             List<ArrayList<String>> a2,
                             List<T> a3,
                             List<? extends Number> a4,
                             List<ArrayList<String>[]> a5,
                             Map<String, Integer> a6) {
    }
}
```



```shell
(0:0)  类型【class java.lang.String】 -> 类型接口【Class】
(1:0)  类型【java.util.ArrayList<java.lang.String>】 -> 类型接口【ParameterizedType】
(2:0)  类型【T】 -> 类型接口【TypeVariable】
(3:0)  类型【? extends java.lang.Number】 -> 类型接口【WildcardType】
(4:0)  类型【java.util.ArrayList<java.lang.String>[]】 -> 类型接口【GenericArrayType】
(5:0)  类型【class java.lang.String】 -> 类型接口【Class】
(5:1)  类型【class java.lang.Integer】 -> 类型接口【Class】
```

[Java中的Type类型更多知识](https://www.jianshu.com/p/a84e485f8077)



### 自定义异常

```java
public class MybatisPlusException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MybatisPlusException(String message) {
		super(message);
	}

	public MybatisPlusException(Throwable throwable) {
		super(throwable);
	}

	public MybatisPlusException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
```



### 工厂模式

定义一个公有的接口

```java
public interface IDialect {

	/**
	 * 组装分页语句
	 * 
	 * @param originalSql
	 *            原始语句
	 * @param offset
	 *            偏移量
	 * @param limit
	 *            界限
	 * @return 分页语句
	 */
	String buildPaginationSql(String originalSql, int offset, int limit);
}
```



实现接口

```java
/**
 * <p>
 * MYSQL 数据库分页语句组装实现
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class MySqlDialect implements IDialect {

	public String buildPaginationSql(String originalSql, int offset, int limit) {
		StringBuilder sql = new StringBuilder(originalSql);
		sql.append(" LIMIT ").append(offset).append(",").append(limit);
		return sql.toString();
	}

}
```



工厂类

```java
public class DialectFactory {

	/**
	 * <p>
	 * 根据数据库类型选择不同分页方言
	 * </p>
	 * 
	 * @param dbtype
	 *            数据库类型
	 * @return
	 * @throws Exception
	 */
	public static IDialect getDialectByDbtype(String dbtype) throws Exception {
		if ("mysql".equalsIgnoreCase(dbtype)) {
			return new MySqlDialect();
		} else if ("oracle".equalsIgnoreCase(dbtype)) {
			return new OracleDialect();
		} else if ("hsql".equalsIgnoreCase(dbtype)) {
			return new HSQLDialect();
		} else if ("sqlite".equalsIgnoreCase(dbtype)) {
			return new SQLiteDialect();
		} else if ("postgre".equalsIgnoreCase(dbtype)) {
			return new PostgreDialect();
		} else {
			return null;
		}
	}

}
```



### 责任链模式

在有些场景下，一个目标对象可能需要经过多个对象的处理。例如，我们要筹办一场校园晚会，需要针对演员进行如下的准备工作。
· 给演员发送邮件，告知晚会的时间、地点，该工作由邮件发送员负责。
· 根据演员性别为其准备衣服，该工作由物资管理员负责。
· 如果演员未成年，则为其安排校车接送，该工作由对外联络员负责。

这一过程，每个演员都要和三个工作人员打交道。

而责任链模式将多个处理器组装成一个链条，被处理对象被放置到链条的起始端后，会自动在整个链条上传递和处理。这样被处理对象不需要和每个处理器打交道，也不需要了解整个链条的传递过程，于是便实现了被处理对象和单个处理器的解耦。
为实现责任链模式，首先创建一个处理器抽象类 Handler。

```java
public abstract class Handler {
    //当前处理器的下一个处理器 private Handler nextHandler;
    /***当前处理器的处理逻辑，交给子类实现*@param performer 被处理对象 ***/
    public abstract void handle(Performer performer);
    /**
    * 触发当前处理器，并在处理结束后将被处理对象传给后续处理器*@param performer被处理对象
    */
    public void triggerProcess(Performer performer){
        handle(performer);
        if(nextHandler!=null){
        nextHandler.triggerProcess(performer);
    }

    /**
    *设置当前处理器的下一个处理器*@param nextHandler 下一个处理器*@return 下一个处理器
    */
    public Handler setNextHandler(Handler nextHandler){
      	this.nextHandler=nextHandler; 
      	return nextHandler;
    }
      
    public static void main(String[] args) {
        Handler handlerChain=new MailSender();
        handlerChain.setNextHandler(new MaterialManager()).setNextHandler(new ContactOfficer())

        //依次处理每个参与者
        for (Performer performer:performerList){
            System.out.println("process"+performer.getName()+":"); handlerChain.triggerProcess(performer); 	
        }
    }
}
```

在调用时，需要先组装好整个责任链，然后将被处理对象交给责任链处理即可。这样，每个演员不需要和工作人员直接打交道，也不需要关心责任链上到底有多少个工作人员。
责任链模式不仅降低了被处理对象和处理器之间的耦合度，还使得我们可以更为灵活地组建处理过程。例如，我们可以很方便地向责任链中增、删处理器或者调整处理器的顺序。



### Mybatis 拦截器

Signature 配置拦截器要拦截的类和方法，Mybatis 中一共只有四个类对象可以被拦截器替换，分别是 ParameterHandler、R esultSetHandler、StatementHandler、Executor。

```java
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * <p>
 * 分页拦截器
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PaginationInterceptor implements Interceptor {

      /* 方言类型 */
      private String dialectType;

      /* 方言实现类 */
      private String dialectClazz;
			public Object intercept(Invocation invocation) throws Throwable {
				Object target = invocation.getTarget();
        if (target instanceof StatementHandler) {
          StatementHandler statementHandler = (StatementHandler) target;
          MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
          RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");

          /* 不需要分页的场合 */
          if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            return invocation.proceed();
          }

          /* 定义数据库方言 */
          IDialect dialect = null;
          if (dialectType != null && !"".equals(dialectType)) {
            dialect = DialectFactory.getDialectByDbtype(dialectType);
          } else {
            if (dialectClazz != null && !"".equals(dialectClazz)) {
              try {
                Class<?> clazz = Class.forName(dialectClazz);
                if (IDialect.class.isAssignableFrom(clazz))
                  dialect = (IDialect) clazz.newInstance();
              } catch (ClassNotFoundException e) {
                throw new MybatisPlusException("Class :" + dialectClazz + " is not found");
              }
            }
          }

          /* 未配置方言则抛出异常 */
          if (dialect == null) {
            throw new MybatisPlusException("The value of the dialect property in mybatis configuration.xml is not defined.");
          }

          /* 禁用内存分页 */
          BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");

          /* 禁用内存分页 */
          String originalSql = (String) boundSql.getSql();
          String paginationSql = dialect.buildPaginationSql(originalSql, rowBounds.getOffset(), 			 rowBounds.getLimit());
          metaStatementHandler.setValue("delegate.boundSql.sql", paginationSql);

          /* 禁用内存分页 */
          metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
          metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

          /* 判断是否需要查询总记录条数 */
          if (rowBounds instanceof Pagination) {
            Pagination pagination = (Pagination) rowBounds;
            if (pagination.getTotal() == 0) {
              MappedStatement mappedStatement = (MappedStatement) metaStatementHandler
                  .getValue("delegate.mappedStatement");
              Connection connection = (Connection) invocation.getArgs()[0];
              count(originalSql, connection, mappedStatement, boundSql, pagination);
            }
          }
        }
        return invocation.proceed();
			}
			
      public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
          return Plugin.wrap(target, this);
        }
        return target;
      }
        
      public void setProperties(Properties prop) {
        String dialectType = prop.getProperty("dialectType");
        String dialectClazz = prop.getProperty("dialectClazz");
        if (dialectType != null && !"".equals(dialectType)) {
          this.dialectType = dialectType;
        }
        if (dialectClazz != null && !"".equals(dialectClazz)) {
          this.dialectClazz = dialectClazz;
        }
      }
}
```



setProperties 方法可以往拦截器类中注入属性，拦截器拦截到目标方法时，会将操作转接到 intercept 方法，plugin: 拦截器类可以选择实现该方法，该方法可以输出一个对象来替换输入参数传入的目标对象，默认调用 Plugin.wrap(target, this) 即可。



MetaObject 是 Mybatis 封装好的反射方法，可以获取到实例的属性，如

```java
RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
```

获取 metaStatementHandler 的实例属性 delegate 中的 rowBounds 属性。



Mybatis 中一共只有四个类对象可以被拦截器替换，分别是 ParameterHandler、ResultSetHandler、StatementHandler、Executor。

1、Executor：mybatis 的内部执行器，作为调度核心负责调用 StatementHandler 操作数据库，并把结果集通过 ResultSetHandler 进行自动映射；

2、StatementHandler： 封装了 JDBC Statement 操作，是 sql 语法的构建器，负责和数据库进行交互执行 sql 语句；

3、ParameterHandler：作为处理 sql 参数设置的对象，主要实现读取参数和对 PreparedStatement 的参数进行赋值;

4、ResultSetHandler：处理 Statement 执行完成后返回结果集的接口对象，mybatis 通过它把 ResultSet 集合映射成实体对象;

在 mybatis 中，不同类型的拦截器按照下面的顺序执行：
**Executor -> StatementHandler -> ParameterHandler -> ResultSetHandler**

以执行 **query** 方法为例对流程进行梳理，整体流程如下：

```java
1、Executor 执行 query() 方法，创建一个 StatementHandler 对象

2、StatementHandler 调用 ParameterHandler 对象的 setParameters() 方法

3、StatementHandler 调用 Statement 对象的 execute() 方法

4、StatementHandler 调用 ResultSetHandler 对象的 handleResultSets() 方法，返回最终结果
```



### 插件注册

```xml
    <plugins>
	    <!-- 
	     | 分页插件配置 
	     | 插件提供二种方言选择：1、默认方言 2、自定义方言实现类，两者均未配置则抛出异常！
	     | dialectType 数据库方言  
	     |             默认支持  mysql  oracle  hsql  sqlite  postgre
	     | dialectClazz 方言实现类
	     |              自定义需要实现 com.baomidou.mybatisplus.plugins.pagination.IDialect 接口
	     | -->
        <plugin interceptor="com.baomidou.mybatisplus.plugins.PaginationInterceptor">
            <property name="dialectType" value="mysql" />
        </plugin>
    </plugins>
```



