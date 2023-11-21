## 源码

Gradle 版本最好高一点，如 6.9.3

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

