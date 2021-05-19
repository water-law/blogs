---
title: Zookeeper 注册中心使用
date: 2019-04-19 19:27:11
categories:
  - 注册中心
tags:
  - Zookeeper
---

### 下载解压

<!--more-->

[Zookeeper](https://archive.apache.org/dist/zookeeper/) 是 Apacahe Hadoop 的子项目，是一个树型的目录服务，支持变更推送，适合作为 Dubbo 服务的注册中心，工业强度较高，可用于生产环境，并推荐使用 [[1\]](http://dubbo.apache.org/zh-cn/docs/user/references/registry/zookeeper.html#fn1)。

### zkServer 和 zkCli

启动 bin 目录下的 zkServer.cmd, 报如下错误

```powershell
Caused by: java.lang.IllegalArgumentException: E:\Dev\zookeeper-3.4.11\bin\..\conf\zoo.cfg file is missing
        at org.apache.zookeeper.server.quorum.QuorumPeerConfig.parse(QuorumPeerConfig.java:140)
        ... 2 more
Invalid config, exiting abnormally
```

在 conf 目录下复制 zoo_sample.cfg 副本， 重命名为 zoo.cfg, 修改 dataDir 为 ../data

启动 zkCli.cmd

