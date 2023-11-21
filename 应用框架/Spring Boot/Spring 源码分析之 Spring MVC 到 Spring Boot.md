### Spring MVC 简化

Spring MVC 到 Spring Boot, [官方文档 web.html](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html) ， 

### web.xml

```xml
<web-app>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/app-context.xml</param-value>
    </context-param>

    <servlet>
        <servlet-name>app</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value></param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>app</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>

</web-app>
```

这个配置被 ServletContext 的 java 代码所取代

```java
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletCxt) {

        // Load Spring web application configuration
        AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
        ac.register(AppConfig.class);
        ac.refresh();

        // Create and register the DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(ac);
        ServletRegistration.Dynamic registration = servletCxt.addServlet("app", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/app/*");
    }
}
```

web.xml 在 tomcat 启动时加载，那么 onStartup 方法如何被 tomcat 加载的呢？

猜测 tomcat 内部方法中有一个 List<WebApplicationInitializer> list

```java
    public static void main(String[] args) {
        ServletContext servletCxt = ..; // tomcat 内部有一个 ServletContext
        List<WebApplicationInitializer> list = ..; // WebApplicationInitializer 列表
        for (WebApplicationInitializer init: list) {
            init.onStartup(servletCxt);
        }
    }
```

这是不可能的， WebApplicationInitializer 是 Spring 的接口， tomcat 无法识别， 猜测不成立。

tomcat 是一款 web 容器， nginx 不是 web 容器， 这是因为 tomcat 支持 servlet 规范， nginx 不支持 servlet 规范。

### Servlet 规范

tomcat 7： < servlet 3.0

tomcat 8:    > servlet 3.0

#### Java SPI 规范

[SPI 规范讲解](https://www.jianshu.com/p/46b42f7f593c)

#### 规范 1

如果在项目的 classpath 路径下提供 META-INF/services 文件夹， 并且在此文件夹下提供了名为 javax.servlet.ServletContainerInitializer 的文件， 文件内容中的类实现 javax.servlet.ServletContainerInitializer 接口， 那么容器在启动时会调用实现类中的 onStartup 方法。

#### 规范 2

如果 规范 1 中的实现类中有一个注解 javax.servlet.annotation.HandlesTypes， 那么会将所有注解接口的所有实现类传给 onStartup 方法。

比如 spring-web 模块中的 resources/META-INF/services 下， javax.servlet.ServletContainerInitializer 的文件内容为 

```
org.springframework.web.SpringServletContainerInitializer
```

```java
package org.springframework.web;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

@HandlesTypes(WebApplicationInitializer.class)
public class SpringServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
            throws ServletException {

        List<WebApplicationInitializer> initializers = new LinkedList<>();

        if (webAppInitializerClasses != null) {
            for (Class<?> waiClass : webAppInitializerClasses) {
                // Be defensive: Some servlet containers provide us with invalid classes,
                // no matter what @HandlesTypes says...
                if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
                        WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
                    try {
                        initializers.add((WebApplicationInitializer)
                                ReflectionUtils.accessibleConstructor(waiClass).newInstance());
                    }
                    catch (Throwable ex) {
                        throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
                    }
                }
            }
        }

        if (initializers.isEmpty()) {
            servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
            return;
        }

        servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
        AnnotationAwareOrderComparator.sort(initializers);
        for (WebApplicationInitializer initializer : initializers) {
            initializer.onStartup(servletContext);
        }
    }

}
```

将 WebApplicationInitializer.class 所有的实现类传给 onStartup 方法，在 onStartup 方法中有个 for 循环调用 WebApplicationInitializer 实现类 的 onStartup 方法 。

### 源码构建

在源码工程中新建 gradle 项目， 添加如下依赖

```groovy
dependencies {
    provided group: 'javax.servlet', name: 'javax.servlet-api', version: '4.0.1'
    compile group: 'org.apache.tomcat.embed', name: 'tomcat-embed-core', version: '8.5.40'
    compile project(':spring-context-support')
    compile project(':spring-webmvc')
    compile project(':spring-oxm')
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```

tomcat-embed-core 是使用 jar 包的方式调用 tomcat

定义一个上述的 MyWebApplicationInitializer 类， AppConfig 类

```java
package top.waterlaw.app;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan("top.waterlaw")
public class AppConfig {
}
```

#### 使用 Tomcat

写个 SpringApplication 类， 启动 tomcat

```java
package top.waterlaw;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class SpringApplication {
    public static void run() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8888);
        try {
            // contextPath 不要为 /, 否则无法识别，项目起不来，确保 E:/webapps/ 目录存在
            tomcat.addWebapp("", "E:/webapps/");
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
```

Test 测试下

```java
package top.waterlaw;

public class Test {
    public static void main(String[] args) {
        SpringApplication.run();
    }
}
```

启动会报错，提示没有视图解析器

```bash
严重: Servlet [jsp] in web application [] threw load() exception
java.lang.ClassNotFoundException: org.apache.jasper.servlet.JspServlet
```

tomcat 默认使用 jsp, 去 https://mvnrepository.com/ 搜下 tomcat jasper， 会看到 [org.apache.tomcat](https://mvnrepository.com/artifact/org.apache.tomcat) » [tomcat-jasper](https://mvnrepository.com/artifact/org.apache.tomcat/tomcat-jasper)

这个先不管， 写个 controller 试下，UserController

```java
package top.waterlaw.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping(value = "/index") // 全路径为 /app/index
    public String index() {
        return "index";
    }
}
```

视图解析器配置较为简单，我们来配置个 json 解析器吧， [View Technologies](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-view)

#### 配置 json 解析器

按官方文档， 重写下 APPConfig 类， 引入 gson 包(如果是手动添加 jar 包则需要在对应的 module 添加依赖)

```java
package top.waterlaw.app;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@ComponentScan("top.waterlaw")
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new GsonHttpMessageConverter());
    }
}
```

视图解析器完成了，接下来在 controller 返回个 Map 吧

```java
package top.waterlaw.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @GetMapping(value = "/app/index")
    @ResponseBody
    public Map<String, String> index() {
        Map map = new HashMap<String, String>();
        map.put("name", "spring");
        return map;
    }
}
```

启动 Test 类还是报错， 把 MyWebApplicationInitializer 类中的 ac.refresh(); 注释掉

请求 http://localhost:8888/app/index 返回

```json
{
"name": "spring"
}
```
