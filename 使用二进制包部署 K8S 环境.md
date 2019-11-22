### 注意

k8s 从 1.13 版本不再支持 etcd2, 仅支持 etcd3, 这也是 k8s 默认就支持的。

[参见 kubernetes github](https://github.com/kubernetes/kubernetes/blob/master/CHANGELOG-1.13.md#downloads-for-v1130-rc1)

k8s 相关版本存在安全漏洞， 请升级或者打补丁你的 k8s

### 预备知识

你必须了解新的 Linux systemd 启动方式以及旧的 init.d 的启动方式，

以免在配置文件失效时手足无措。 阮一峰老师在他的博客中对 systemd

已经做了很详细的介绍了， 请自行参考以下博客。

[Systemd 入门教程：命令篇](http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-commands.html)

[Systemd 入门教程：实战篇](http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-part-two.html)

### K8S 环境


主要采用两台阿里云服务器进行环境的部署， 一台作为 master, 另一台作为 node。

详细如下。


| 角色   | IP             | 组件                                                         |
| ------ | -------------- | ------------------------------------------------------------ |
| master | 47.104.230.228 | etcd、kube-apiserver、kube-controller-manager、kube-scheduler |
| node01 | 47.97.160.187  | kubelet、kube-proxy、docker                                  |
|        |                |                                                              |


我这里采用的是 K8S 1.8 版本， 算比较旧。

### Master 节点配置

Master 和 Node 节点的配置**请先参考这篇教程**， [Kubernetes(K8S) 集群管理 Docker 容器（部署篇）](https://blog.csdn.net/zhenliang8/article/details/78611004)， **本文只是针对这篇博客进行补充说明**。提示： 这篇博客的复制黏贴有空格的问题， 我已经将配置托管到 Github 上，
[Github 地址](https://github.com/water-law/k8s-binary-cfg.git)

如果是 etcd2, 必须多配置一个参数， --storage-backend=etcd2， 如下：


```shell
/opt/kubernetes/bin/kube-apiserver --logtostderr=true \

--v=4 \

--etcd-servers=http://47.104.230.228:2379 \

--insecure-bind-address=47.97.160.187 \

--insecure-port=8080 \

--advertise-address=47.104.230.228 \

--allow-privileged=false \

--service-cluster-ip-range=10.10.10.0/24 \

--storage-backend=etcd2 
```

### 题外话：

不要暴露 0.0.0.0,  使用 RBAC。

另外可采用 Rancher 方式部署

### node 节点配置

/opt/kubernetes/cfg/kubelet.kubeconfig


```shell
apiVersion: v1
kind: Config
clusters:
- cluster:
    server: http://47.104.230.228:8080
  name: local
contexts:
- context:
    cluster: local
  name: local
current-context: local

```

/opt/kubernetes/cfg/kubelet


```shell
# 启用日志标准错误
KUBE_LOGTOSTDERR="--logtostderr=true"
# 日志级别
KUBE_LOG_LEVEL="--v=4"
# Kubelet服务IP地址
NODE_ADDRESS="--address=172.16.252.50"
# Kubelet服务端口
NODE_PORT="--port=10250"
# 自定义节点名称
NODE_HOSTNAME="--hostname-override=47.97.160.187"
# kubeconfig路径，指定连接API服务器
KUBELET_KUBECONFIG="--kubeconfig=/opt/kubernetes/cfg/kubelet.kubeconfig"
# 允许容器请求特权模式，默认false
KUBE_ALLOW_PRIV="--allow-privileged=false"
# DNS信息
KUBELET_DNS_IP="--cluster-dns=10.10.10.2"
KUBELET_DNS_DOMAIN="--cluster-domain=cluster.local"
# 禁用使用Swap
KUBELET_SWAP="--fail-swap-on=false"
```

注意： --address 必须使用阿里云服务器的私有 ip, 而不是公网 ip。


阿里云内网 ip: 172.16.252.50

阿里云外网 ip：47.97.160.187


使用外网 ip 会报错：


```shell
I1213 14:14:19.900000   10842 server.go:718] Started kubelet v1.8.3
E1213 14:14:19.900514   10842 kubelet.go:1234] Image garbage
collection failed once. Stats initialization may not have completed 

yet: failed to get imageFs info: unable to find data for container /
I1213 14:14:19.900947   10842 mount_linux.go:600] Directory /var/lib/kubelet is already on a shared mount
I1213 14:14:19.901023   10842 kubelet_node_status.go:280] Setting node annotation to enable volume controller attach/detach
I1213 14:14:19.901409   10842 server.go:128] Starting to listen on 47.97.160.187:10250
I1213 14:14:19.902313   10842 server.go:296] Adding debug handlers to kubelet server.
F1213 14:14:19.903735   10842 server.go:140] listen tcp 47.97.160.187:10250: bind: cannot assign requested address
```

/lib/systemd/system/kubelet.service
```shell
[Unit]
Description=Kubernetes Kubelet
After=docker.service
Requires=docker.service

[Service]
EnvironmentFile=-/opt/kubernetes/cfg/kubelet
ExecStart=/opt/kubernetes/bin/kubelet \
${KUBE_LOGTOSTDERR} \
${KUBE_LOG_LEVEL} \
${NODE_ADDRESS} \
${NODE_PORT} \
${NODE_HOSTNAME} \
${KUBELET_KUBECONFIG} \
${KUBE_ALLOW_PRIV} \
${KUBELET_DNS_IP} \
${KUBELET_DNS_DOMAIN} \
${KUBELET_SWAP}
Restart=on-failure
KillMode=process

[Install]
WantedBy=multi-user.target
```

kubelet.service 相当于执行以下脚本：

```shell
/opt/kubernetes/bin/kubelet --logtostderr=true \

--v=4 \

--address=172.16.252.50 \

--port=10250 \

--hostname-override=47.97.160.187 \

--kubeconfig=/opt/kubernetes/cfg/kubelet.kubeconfig \

--allow-privileged=false \

--cluster-dns=10.10.10.2 \

--cluster-domain=cluster.local \

--fail-swap-on=false
```

/opt/kubernetes/cfg/kube-proxy


```shell
# 启用日志标准错误

KUBE_LOGTOSTDERR="--logtostderr=true"

# 日志级别

KUBE_LOG_LEVEL="--v=4"

# 自定义节点名称

NODE_HOSTNAME="--hostname-override=47.97.160.187"

# API服务地址

KUBE_MASTER="--master=http://47.104.230.228:8080"
```

/lib/systemd/system/kube-proxy.service


```bash
[Unit]
Description=Kubernetes Proxy
After=network.target
[Service]
EnvironmentFile=-/opt/kubernetes/cfg/kube-proxy
ExecStart=/opt/kubernetes/bin/kube-proxy \
${KUBE_LOGTOSTDERR} \
${KUBE_LOG_LEVEL} \
${NODE_HOSTNAME} \
${KUBE_MASTER}
Restart=on-failure
[Install]
WantedBy=multi-user.target

```
### 参考博客

[Kubernetes(K8S) 集群管理 Docker 容器（部署篇）](https://blog.csdn.net/zhenliang8/article/details/78611004)

### K8S 安全漏洞

[来源于公众号：Kubernetes 惊现首个重大安全漏洞，可随意提升 root 权限](https://mp.weixin.qq.com/s?__biz=MzAwNTM5Njk3Mw==&mid=2247485393&idx=1&sn=bd705daa66812cf4b6bea0703553dd09&chksm=9b1c0753ac6b8e45164fd15390d74a06f73c750ed474d61e3c3bc9a158c1e03282513ab1a483&mpshare=1&scene=23&srcid=#rd)

[来源于 InfoQ：Kubernetes 惊现首个重大安全漏洞，可随意提升 root 权限](https://www.infoq.cn/article/k-Nye3qgyJ3EdJaIEsXO)