### 环境搭建

Spring5.x+Gradle 4.3.1 + JDK1.8.0_181 + Windows64 + IDEA 2017

Spring 5: 

```bash
git clone -b 5.0.x https://github.com/spring-projects/spring-framework.git
```

[Jdk ](https://www.oracle.com/technetwork/java/javase/downloads/index.html )

[Gradle](https://services.gradle.org/distributions/)

[IDEA](https://www.jetbrains.com/idea/ )

#### JDK 设置

windows 上 javac 默认使用 gbk 编码， 可以设置环境变量   JAVA_TOOL_OPTIONS， 值为：

```bash
-Dfile.encoding=UTF-8  
```

#### Gradle 设置

环境变量 GRADLE_HOME， 值为 Gradle 根目录

环境变量 GRADLE_USER_HOME，表示下载的 jar 包放在那个仓库， 可以指定文件夹路径

### 常用 Gradle 指令

--stacktrace: 打印异常信息

-x：排除某个 task

--task: 查看当前 build.gradle 有哪些 task

javadocJar: 一个生成 doc 和 doc Jar 包的 task

```bash
gradle javadocJar --stacktrace -x :spring-beans:javadoc
```

### 编译

#### IOC 功能

新建 gradle 项目，在 build.gradle 的 dependencies 中加入 

```groovy
compile project(':spring-context')
```

执行项目的 build,  此时可能 spring-aop 会报错， 解决办法如下：

- 先执行 spring-core 的 compileTestJava 任务

- 由于 spring-context 依赖 spring-aop 模块， spring-aop 模块用到了 commons-logging 日志，
  
  ```groovy
  compile group: 'commons-logging', name: 'commons-logging', version: '1.1.1'
  ```
  
  可以将此依赖复制到项目的 build.gradle 下

再次执行新建项目的 build， 这次成功了

```java
package top.waterlaw.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("top.waterlaw")
public class AppConfig {
}
```

Test 类：

```java
public class Test {
    /**
     * 把类扫描出来
     * 把 bean 实例化
     */
    public static void main(String[] args) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext =
                new AnnotationConfigApplicationContext(AppConfig.class);

        /**
         *
         */
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClassName("xxxx");
        beanDefinition.setBeanClass(UserDao.class);
        beanDefinition.setLazyInit();

        UserDao userDao = (UserDao) annotationConfigApplicationContext.getBean("userDao");
        userDao.query();

    }
}
```

### 其他博客

[Spring 5 源码编译](https://blog.csdn.net/baomw/article/details/83956300)