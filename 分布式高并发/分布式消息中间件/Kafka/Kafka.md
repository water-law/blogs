## 安装 Scala



```shell
wget https://downloads.lightbend.com/scala/2.12.14/scala-2.12.14.tgz
```

配置下 PATH 环境变量

## 安装 Kafka

```shell
wget https://archive.apache.org/dist/kafka/3.1.1/kafka_2.12-3.1.1.tgz
```



https://www.jianshu.com/p/2425b9b34165

https://www.jianshu.com/p/898ad61c59fd

## 启动 zookeeper

```
bin/zookeeper-server-start.sh config/zookeeper.properties &
```

### 启动 Kafka

```
bin/kafka-server-start.sh config/server.properties &
```

