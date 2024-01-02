# 文件查看类

### cat

查看文件内容

```bash
cat -n xxx.txt
```

### tail

实时查看日志

```bash
tail -f xxx.log
```

### df

```bash
df [选项]... [FILE]...
```

- 文件-a, --all 包含所有的具有 0 Blocks 的文件系统
- 文件--block-size={SIZE} 使用 {SIZE} 大小的 Blocks
- 文件-h, --human-readable 使用人类可读的格式(预设值是不加这个选项的...)
- 文件-H, --si 很像 -h, 但是用 1000 为单位而不是用 1024
- 文件-i, --inodes 列出 inode 资讯，不列出已使用 block
- 文件-k, --kilobytes 就像是 --block-size=1024
- 文件-l, --local 限制列出的文件结构
- 文件-m, --megabytes 就像 --block-size=1048576
- 文件--no-sync 取得资讯前不 sync (预设值)
- 文件-P, --portability 使用 POSIX 输出格式
- 文件--sync 在取得资讯前 sync
- 文件-t, --type=TYPE 限制列出文件系统的 TYPE
- 文件-T, --print-type 显示文件系统的形式
- 文件-x, --exclude-type=TYPE 限制列出文件系统不要显示 TYPE
- 文件-v (忽略)
- 文件--help 显示这个帮手并且离开
- 文件--version 输出版本资讯并且离开

df 查看的是已挂载的文件/硬盘

### grep

过滤文件内容

```bash
grep "2021-01-01" catalina.out > 01.log
```

### fdisk

```bash
fdisk [必要参数][选择参数]
```

**必要参数：**

- -l 列出素所有分区表
- -u 与"-l"搭配使用，显示分区数目

**选择参数：**

- -s<分区编号> 指定分区
- -v 版本信息

**菜单操作说明**

- m ：显示菜单和帮助信息
- a ：活动分区标记/引导分区
- d ：删除分区
- l ：显示分区类型
- n ：新建分区
- p ：显示分区信息
- q ：退出不保存
- t ：设置分区号
- v ：进行分区检查
- w ：保存修改
- x ：扩展应用，高级功能

# URL 相关类

### wget

根据 URL 下载文件

```bash
wget http://xxx.html
```

### curl

构造 URL 请求

```bash
curl -L http://xxx.com
```

# 进程相关类

### ps

查看进程

```bash
ps -ef | grep xxx
```

### netstat

查看端口占用

```bash
netstat -lnp | grep 8080
```

### top

任务管理器

```
ctrl + P ctrl + M
```

按 CPU 和内存排行

升级版工具： **htop**

# 磁盘相关类

### du

```bash
du -sh *
```

查看当前目录下的文件大小

# 防火墙类

### firewall-cmd

```bash
firewall-cmd --zone=public --add-port=8080/tcp --permanent
firewall-cmd --reload
```

[CentOS7 启动防火墙](https://blog.csdn.net/qq_29369653/article/details/86577326)

CentOS7 里面防火墙是用 firewalld (相关介绍介绍请点击)来管理防火墙的

（--permanent 没有此参数重启后失效）

### iptables

CentOS6 里面防火墙是用 iptables

查看规则列表

```bash
iptables -L -n
```

放行内网端口

```bash
iptables -I INPUT -p tcp --dport 8080 -j ACCEPT
service iptables save
service iptables restart
```

# 服务器时间类

### ntpdate

ntpdate 时间同步

### date

查看当前时间

# 会话管理类

### nohup

**nohup命令及其输出文件**                                                                                             

使用nohup命令可以保证程序能在后台一直执行，不会因为退出会话而终止。

使用方式：nohup ./start-dishi.sh >output 2>&1 &

**现对上面的命令进行下解释**

用途：不挂断地运行命令。

语法：nohup Command [ Arg ... ] [　& ]

描述：nohup 命令运行由 Command 参数和任何相关的 Arg 参数指定的命令，忽略所有挂断（SIGHUP）信号。

在注销后使用 nohup 命令运行后台中的程序。要运行后台中的 nohup 命令，添加 & （ 表示“and”的符号）到命

令的尾部。

操作系统中有三个常用的流：
　　0：标准输入流 stdin
　　1：标准输出流 stdout
　　2：标准错误流 stderr

一般当我们用 > console.txt，实际是 1>console.txt的省略用法；< console.txt ，实际是 0 < console.txt的省

略用法。

  

**下面步入正题：**

\>nohup ./start-dishi.sh >output 2>&1 &

解释：

1. 带&的命令行，即使terminal（终端）关闭，或者电脑死机程序依然运行（前提是你把程序递交到服务器上)； 

2. 2>&1的意思 

　　这个意思是把标准错误（2）重定向到标准输出中（1），而标准输出又导入文件output里面，所以结果是标

准错误和标准输出都导入文件output里面了。 至于为什么需要将标准错误重定向到标准输出的原因，那就归结为

标准错误没有缓冲区，而stdout有。这就会导致 >output 2>output 文件output被两次打开，而stdout和stderr将

会竞争覆盖，这肯定不是我门想要的. 

　　这就是为什么有人会写成： nohup ./command.sh >output 2>output出错的原因了 

==================================================================

最后谈一下/dev/null文件的作用，这是一个无底洞，任何东西都可以定向到这里，但是却无法打开。 所以一般很

大的stdou和stderr当你不关心的时候可以利用stdout和stderr定向到这里>./command.sh >/dev/null 2>&1 

### screen

多个会话管理器
常用命令： 
- screen -S [name]: 创建一个会话
- screen -x [name]: 进入一个会话
- screen -d [name | id]: 暂时终止会话
- screen -r [name | id]: 重启会话
- quit： 退出会话
- screen A N: 在新的窗口继续当前会话
- screen -X -S [name] quit:  删除 Detached 的会话

Screen 操作快捷键

- ctrl-a c：创建一个新的 Shell
- ctrl-a ctrl-a：在 Shell 间切换
- ctrl-a n：切换到下一个 Shell
- ctrl-a p：切换到上一个 Shell
- ctrl-a 0…9：同样是切换各个 Shell
- ctrl-a d：退出 Screen 会话

# 系统服务启动类

### systemctl

[Systemd 入门教程：命令篇](http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-commands.html)

[Systemd 入门教程：实战篇](http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-part-two.html)

### crontab

定时器

```bash
crontab -e
```

### docker

运行容器, 挂载目录 /home/zjp/Projects/novel 到 /home/novel/code

```bash
docker run -ti --net host -v /etc/localtime:/etc/localtime:ro
-v /home/zjp/Projects/novel:/home/novel/code <image_id> /bin/bash
```

进入容器

~~~bash
docker exec -it <container_id> /bin/bash
~~~

查看容器状况

```bash
docker inspect <contain_id>
```
