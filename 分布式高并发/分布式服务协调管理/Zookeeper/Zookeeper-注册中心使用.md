### 下载解压

```shell
wget https://www.apache.org/dyn/closer.lua/zookeeper/zookeeper-3.7.1/apache-zookeeper-3.7.1-bin.tar.gz
```

[Zookeeper](https://archive.apache.org/dist/zookeeper/) 是 Apacahe Hadoop 的子项目，是一个树型的目录服务，支持变更推送，适合作为 Dubbo 服务的注册中心，工业强度较高，可用于生产环境，并推荐使用 [[1\]](http://dubbo.apache.org/zh-cn/docs/user/references/registry/zookeeper.html#fn1)。

### zkServer 和 zkCli

启动 bin 目录下的 zkServer.sh, 报如下错误

```powershell
Caused by: java.lang.IllegalArgumentException: E:\Dev\zookeeper-3.4.11\bin\..\conf\zoo.cfg file is missing
        at org.apache.zookeeper.server.quorum.QuorumPeerConfig.parse(QuorumPeerConfig.java:140)
        ... 2 more
Invalid config, exiting abnormally
```

在 conf 目录下复制 zoo_sample.cfg 副本， 重命名为 zoo.cfg, 修改 dataDir 为 ../data

### 启动服务端

```shell
zkServer.sh start
```

#### 启动客户端

```shell
zkCli.sh
```

### 常用指令

ls、create、delete、get、set 

