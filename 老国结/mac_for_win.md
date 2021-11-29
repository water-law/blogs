# 1. Mac for Win

mac m1 处理器安装 win10 受限制， 只能安装 arm 版本的 win10, 软件只支持 arm/x86 结构，即是 32 位软件（存疑）

[eclipse](https://ftp.jaist.ac.jp/pub/eclipse/technology/epp/downloads/release/2018-09/R/)

jdk1.6  32bit

Jdk1.8  32bit:  eclipse 需要

maven: 3.0.4

Jboss: 4.2.2-GA



# 2.  Parallela Desktop

一款支持 mac m1 安装 win10 的虚拟机



# 3. eclipse

安装 server 插件， 安装 jboss



# 4. jboss 

修改  server/default/deploy/tfb-ds.xml

# 5. 数据库账号密码

**数据库：**

172.16.97.188    orcl    1521      用户/密码：lcfstfb/lcfstfb

## 6. maven

```xml
<mirror>
    <id>alimaven</id>
		<name>aliyun maven</name>
		<url>http://maven.aliyun.com/nexus/content/groups/public/</url>
		<mirrorOf>central</mirrorOf> 
</mirror>
```

