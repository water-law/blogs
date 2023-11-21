### 环境搭建

Spring5.x+Gradle 4.3.1 + JDK1.8.0_181 + Windows64 + IDEA 2017

Spring 5: 

```bash
git clone -b 2.1.x https://github.com/spring-projects/spring-boot.git
```

推荐先 fork 该项目， 然后 clone 下来， 这样修改可以保存到自己的仓库，也可以向官方提 pull request.

### 源码编译

```bash
./mvnw clean install -DskipTests -DskipChecks -Pfast
mvn clean install -Dmaven.test.skip=true -Dmaven.checkstyle.check.skip=true
```

新建 maven 项目 spring-boot-starter-demo， pom.xml 如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>c
        <artifactId>spring-boot-parent</artifactId>
        <version>${revision}</version>
        <relativePath>../spring-boot-project/spring-boot-parent</relativePath>
    </parent>
    <groupId>top.waterlaw</groupId>
    <artifactId>spring-boot-starter-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>
    <properties>
        <main.basedir>${basedir}/..</main.basedir>
        <m2eclipse.wtp.contextRoot>/</m2eclipse.wtp.contextRoot>
    </properties>
    <dependencies>
        <!-- Compile -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
        </dependency>
        <!-- Provided -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.tomcat.embed</groupId>
            <artifactId>tomcat-embed-jasper</artifactId>
            <scope>provided</scope>
        </dependency>
        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <useSystemClassLoader>false</useSystemClassLoader>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

新建主类  SampleSimpleApplication.java

```java
package top.waterlaw;

import com.sun.javaws.exceptions.ExitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SampleSimpleApplication implements CommandLineRunner {
    @Autowired
    Hello hello;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(this.hello.getMsg());
        if(args.length > 0 && args[0].equals("exitcode")) {
            throw new ExitException("exit", new Throwable());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleSimpleApplication.class, args);
    }
}
```

### spring-boot 注解

org.springframework.boot 下的注解

#### context

org.springframework.boot.context.properties.ConfigurationProperties

org.springframework.boot.context.properties.ConfigurationPropertiesBinding

org.springframework.boot.context.properties.DeprecatedConfigurationProperty

org.springframework.boot.context.properties.EnableConfigurationProperties

org.springframework.boot.context.properties.NestedConfigurationProperty

#### convert

org.springframework.boot.convert.DataSizeUnit

org.springframework.boot.convert.Delimiter

org.springframework.boot.convert.DurationFormat

org.springframework.boot.convert.DurationUnit

#### jackson

org.springframework.boot.jackson.JsonComponent

#### web

org.springframework.boot.web.server.LocalServerPort

#### 根

org.springframework.boot.SpringBootConfiguration

### spring-boot-autoconfigure 注解

#### condition

org.springframework.boot.autoconfigure.condition.ConditionalOnBean

org.springframework.boot.autoconfigure.condition.ConditionalOnClass

org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform

org.springframework.boot.autoconfigure.condition.ConditionalOnExpression

org.springframework.boot.autoconfigure.condition.ConditionalOnJava

org.springframework.boot.autoconfigure.condition.ConditionalOnJndi

org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean

org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass

org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication

org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

org.springframework.boot.autoconfigure.condition.ConditionalOnResource

org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate

org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication

#### data

org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType

#### domain

org.springframework.boot.autoconfigure.domain.EntityScan

#### flyway

org.springframework.boot.autoconfigure.flyway.FlywayDataSource

#### liquibase

org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource

#### quartz

org.springframework.boot.autoconfigure.quartz.QuartzDataSource

#### web

org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean

org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain

#### 根

org.springframework.boot.autoconfigure.AutoConfigurationPackage

org.springframework.boot.autoconfigure.AutoConfigureAfter

org.springframework.boot.autoconfigure.AutoConfigureBefore

org.springframework.boot.autoconfigure.AutoConfigureOrder

org.springframework.boot.autoconfigure.EnableAutoConfiguration

org.springframework.boot.autoconfigure.ImportAutoConfiguration

org.springframework.boot.autoconfigure.SpringBootApplication

### spring-boot-devtools 模块

[使用spring-boot-devtools进行热部署](https://blog.csdn.net/u012190514/article/details/79951258)

### 其他博客

[**SpringBoot2 源码分析**](https://blog.csdn.net/woshilijiuyi/article/details/82219585)