# IOC

ioc 是控制反转，俗称依赖注入，是 Spring Framework 的核心功能， 由 BeanFactory, BeanDefinition, BeanDefinitionMap, Spring 缓存池组成。

说说 aop 和 ioc 关系，可以独立实现没有关系， 但 Spring 在实现 ioc 的时候使用到了 aop 的功能。

# AOP

aop 是面向切面编程，使用切点和增强的概念非侵入式切入代码。aop 主要是实现有静态编译， Java 运行时动态代理，cglib 

- aop 是什么，有哪些实现方式？

- aop 里面的 cglib 原理是什么？

- aop 切方法的方法的时候，哪些方法是切不了的？为什么？

- 同类调用为什么无法切？怎么样解决（AOPContext）?

# Bean

bean 作用域有哪些，说一下各种使用场景？

bean 的各种作用域是怎么样实现的？

工具类中如何注入 bean？具体使用场景？   @PostConstruct

注入的 bean 存在多份的时候有哪些解决办法？

有没有用过 BeanFactory？场景？

 [`ApplicationContext`](https://docs.spring.io/spring-framework/docs/5.2.1.RELEASE/javadoc-api/org/springframework/context/ApplicationContext.html) is a sub-interface of `BeanFactory`. 

IOC 中的 ApplicationContext 是一个实现 BeanFactory 接口的子类。

# Spring MVC

### 项目结构

- java

- resources

- webapp
  
  - WEB-INF
    - xxx-servlet.xml
    - web.xml
  - index.jsp

主要是 web.xml 和 servlet.xml 配置

web.xml 包括配置 spring 核心监听器 , 过滤器，DispatcherServlet

### Spring 核心监听器

Spring 核心监听器默认会以 /WEB-INF/applicationContext.xml作为配置文件, 

```java
public class ContextLoaderListener extends ContextLoader implements ServletContextListener
```

 ContextLoader 可以指定在 Web 应用程序启动时载入 Ioc 容器, ServletContextListener 监听 ServletContext，

ContextLoaderListener   启动 Web 容器时，读取在 contextConfigLocation 中定义的 xml 文件，自动装配ApplicationContext 的配置信息，并产生 WebApplicationContext 对象，然后将这个对象放置在 ServletContext的属性里。

#### IOC

```html
数据源，sessionFactory，component-scan，配置事务管理器, AOP 配置提供事务增强, 配置事务的传播特性。
```

### DispatcherServlet

```html
component-scan, 启动Spring MVC的注解功能，完成请求和注解POJO的映射 

AnnotationMethodHandlerAdapter, 静态资源访问, 视图解析器, 上传文件，国际化文件和异常处理。
```

### 过滤器

容器编码、登录认证、静态资源拦截， 在 DispatcherServlet 前后进行处理

### 拦截器

拦截器是什么，什么场景使用？

AOP 实现事务管理、日志打印、时间耗时统计、权限认证

说说 DispatcherServlet 做了什么

### 日志框架

Java 日志统一接口

**jcl**: Jakarta Commons-logging 是 apache 最早提供的日志的门面接口,  也是 spring 默认使用的日志框架。

 Commons-logging 的目的是为 “所有的 Java 日志实现” 提供一个统一的接口，它自身的日志功能平常弱（只有一个简单的SimpleLog?），所以一般不会单独使用它。Log4j 的功能非常全面强大，是目前的首选。 

[apache commons-logging](http://commons.apache.org/proper/commons-logging/guide.html#Quick_Start) 

[log4j]( https://logging.apache.org/log4j/2.x/ )

**slf4j**：Simple Logging Facade for Java，为 Java 提供的简单日志 Facade。Facade 门面，更底层一点说就是接口。它允许用户以自己的喜好，在工程中通过 slf4j 接入不同的日志系统。

因此 **slf4j** 入口就是众多接口的集合，它不负责具体的日志实现，只在编译时负责寻找合适的日志系统进行绑定。具体有哪些接口，全部都定义在 slf4j-api 中。

[maven 仓库：slf4j-log4j12]( https://mvnrepository.com/artifact/org.slf4j/slf4j-log4j12 ) 

[10分钟搞定--混乱的 Java 日志体系](https://www.jianshu.com/p/39ced06944a2)

[SpringMVC 配置 log4j]( https://blog.csdn.net/ljheee/article/details/76679315 )

### 异常处理

你之前项目中异常/国际化如何处理？

首先看看 Spring MVC 处理异常的 3 中方式,进行比较,最终选用一个比较合适的方式。

     Spring MVC 提供的简单异常处理器 SimpleMappingExceptionResolver;
    
     Spring MVC 异常处理接口 HandlerExceptionResolver 自定义自己的异常处理器;
    
     @ExceptionHandler 注解实现异常处理;

### 国际化

你之前项目中异常/国际化如何处理？

使用 ResourceBundleMessageSource， 在 i18n/messages 加上对应翻译文件，根据请求头 lang 加载翻译文件

```xml
    <bean id="messageSource"
          class="org.springframework.context.support.ResourceBundleMessageSource"
          p:basename="i18n/messages" />
```

### 常用注解

常见的使用多的注解问几个（requestbody, responsebody, ModelAttribute 等，

### 应用

返回视图和 json 对象

ModelAndView,  @ResponseBody: 获取 reponse.getWriter(), redirect 传参数放在 ModelAndView

# 主要扩展点

spring 的主要扩展点有哪些？（最重要最有用的应该就是bpp了）