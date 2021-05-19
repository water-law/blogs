## 事由

原博客于 2018 年 6 月使用 Django + Rest FrameWork 开发， 并使用阿里云和 Nginx 部署。由于学习 Java, 于是用 Spring Cloud 重写了下代码

### Spring Cloud

#### Spring 整合前端项目

重写 WebMvcConfigurer 或者在配置文件中配置

```java
package top.waterlaw.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Resource
    private BaseInterceptor baseInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/dist/", "classpath:/dist/admin/", "classpath:/dist/user/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/user/index.html");
        registry.addRedirectViewController("/admin", "/admin/index.html");
    }
}
```

拦截器

```java
package top.waterlaw.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BaseInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(BaseInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        log.info("访问地址: " + uri);
        //- user
//        if(uri.equals("/")) {
//            response.sendRedirect(request.getContextPath() + "/user/index.html");
//            return true;
//        }
        return true;
    }
}
```

ps: 不同项目的前端的静态文件会不会冲突？

### Ant Design Pro

[Ant Design Pro](https://pro.ant.design/docs/getting-started-cn)

[UMIJS](https://umijs.org/guide/getting-started.html)

[UMIJS-CN](https://umijs.org/zh/guide/umi-ui.html)

[ant design pro 超详细入门教程](https://www.cnblogs.com/freely/p/10874297.html)

#### 安装 umi

需要先安装 umi

```bash
npm install umi -g
```

#### ant-design-pro 模板

clone 项目模板

```bash
git clone --depth=1 https://github.com/ant-design/ant-design-pro.git ant-pro-admin
```

npm start 后有两个端口， 3000 端口是 umi 端口， 8000 端口是网站端口(样板程序，使用 ant-design-pro 模板+typescript 构建 )

umi 创建(ant-design-pro 模板+typescript)项目后启动项目，可以根据 Ant Design Pro 官网进行配置使用。

#### 编译

在 umi 3000 端口号**配置**界面进行项目配置，修改打包后的路径为 

```bash
../webapp/src/main/resources/dist/admin
```

在**任务**界面进行项目构建，打包后的文件集成到 Spring Boot 

