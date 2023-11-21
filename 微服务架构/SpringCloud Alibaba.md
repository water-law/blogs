### Nacos 安装与使用

```sh
wget https://github.com/alibaba/nacos/releases/download/2.1.1/nacos-server-2.1.1.tar.gz
```

###  nacos数据库

注意：Nacos 目前只支持MySQL数据库，请安装MySQL8.0版本，以免出现其他错误。

新建数据库nacos_config，并运行【conf/nacos-mysql.sql】文件，初始化数据库即可

###  修改Nacos的配置文件

```properties
# 填自己的ip地址，本地填127.0.0.1就行
nacos.inetutils.ip-address=127.0.0.1

spring.datasource.platform=mysql
db.num=1
#填自己的数据库连接和密码
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user.0=root
db.password.0=root
```

###  启动Nacos

```sh
Windows：
startup.cmd -m standalone

Linux: 
sh startup.sh -m standalone
```

### Docker

https://nacos.io/zh-cn/docs/quick-start-docker.html

# mac-m1-docker 安装 nacos 异常

https://blog.csdn.net/yang_zzu/article/details/127421532

```
docker build -t nacos/nacos-server:v2.1.1 .
```

本地下载了一个 nacos2.1.1,  在 /Users/zjp/Projects/nacos 目录， 修改 /Users/zjp/Projects/nacos/conf/application.properties 中 mysql 连接 ip:port

```properties
host.docker.internal:3306
```



```sh
docker run -itd -e MODE=standalone --add-host host.docker.internal:host-gateway -p 8848:8848 -p 9848:9848 -m 2048m --memory-swap=2312m -v /Users/zjp/Projects/nacos/conf/application.properties:/home/nacos/conf/application.properties --name nacos nacos-server:v2.1.1
```

### 端口

7848：实现 Nacos 集群通信，一致性选举，心跳检测等功能

8848：Nacos 主端口，对外提供服务的Http端口

9848:   客户端 gRPC 请求服务端端口，用于客户端向服务端发起连接和请求，该端口的配置为：主端口（8848）+ 1000 偏移量

9849：服务端 gRPC 请求服务端端口，用于服务间同步等，该端口的配置为：主端口 + 1001偏移量



### Redis

```sh
docker run -d -p 6379:6379 --name redis redis
```

