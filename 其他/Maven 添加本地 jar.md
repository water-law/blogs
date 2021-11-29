# Maven 添加本地 jar 包 

将 jar 添加到 maven 仓库，可以自定义 groupId, artifactId, version,   如 groupId：google artifactId: concurrent, version:1.3.4

则在 maven 仓库中新建 google/concurrent 目录

在 concurrent  目录下新建  “1.3.4 ” 文件夹, 该文件夹下放置 jar 包和 pom 文件, 文件名中间以 “-” 即减号隔开

jar 包文件名格式为 <artifactId>-<version>.jar

pom 文件名格式为 <artifactId>-<version>.pom

如 concurrent-1.3.4.pom：

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>google</groupId>
  <artifactId>concurrent</artifactId>
  <version>1.3.4</version>
  <name>Dough Lea's util.concurrent package</name>
</project>
```

在 pom.xml 中引入

```xml
<dependency>
  <modelVersion>4.0.0</modelVersion>
  <groupId>google</groupId>
  <artifactId>concurrent</artifactId>
  <version>1.3.4</version>
</dependency>
```

