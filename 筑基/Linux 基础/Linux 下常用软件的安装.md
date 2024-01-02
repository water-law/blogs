# 查看进程、端口、防火墙

```shell
# 查询端口是否开放
firewall-cmd --query-port=8080/tcp
# 开放80端口
firewall-cmd --permanent --add-port=80/tcp
# 移除端口
firewall-cmd --permanent --remove-port=8080/tcp
#重启防火墙(修改配置后要重启防火墙)
firewall-cmd --reload
```

 

step1 查找ES进程号

```shell
ps -ef | grep elastic
```

```shell
ps -ef | grep tomcat
```

 

查看端口是否占用

```shell
netstat -anp|grep 65511
```

```shell
lsof -i:65511
```

​	

杀死进程

```shell
kill -9 3250
```

 

清理缓存

```shell
sync
echo 3 >/proc/sys/vm/drop_caches
```



# JDK

vi /etc/profile

```shell
export JAVA_HOME=/usr/java/jdk1.8.0_131
export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$PATH:$JAVA_HOME/bin
```

激活环境变量

```shell
source /etc/profile
```



# Redis安装步骤

**1、** 上传服务器：

将jemalloc-3.6.0-1.el6.art.x86_64.rpm和redis-3.2.10-2.el6.x86_64.rpm包上传到服务器上;

**2、** 先安装jemalloc：

输入命令rpm –ivh jemalloc-3.6.0-1.el6.art.x86_64.rpm即可;

**3、** 然后再安装redis:

输入命令rpm -ivh redis-3.2.10-2.el6.x86_64.rpm便可安装完成。

**4、** 修改Redis的配置文件:

使用命令vi /etc/redis.conf 来编辑redis的配置文件，该文件是安装Redis后便存在的。将bind行修改为：bind 0.0.0.0，然后将原来的bind 127.0.0.1使用#注释掉。

**5、** 设置密码：

requirepass xxxx! ，另外Redis默认的端口号为6379,可根据需要选择是否修改。

**6、** 启动redis服务：

使用redis-server命令可启动Redis，这是linux上的服务端，启动后的页面如下。

**7、** 将Redis设置为开机自启动：

使用命令chkconfig redis on，启动完成后可使用命令chkconfig --list redis查看。



启动后再看看状态：systemctl start redis

重启 redis

```sh
systemctl restart redis.service
```



# MongoDB安装步骤

**1、** 上传服务器：

将MongoDB安装rpm包上传到服务器上;

**2、** 安装MongoDB：

```sh
rpm –ivh mongodb-org-mongos-4.2.6-1.el7.x86_64.rpm

rpm –ivh mongodb-org-server-4.2.6-1.el7.x86_64.rpm

rpm –ivh mongodb-org-shell-4.2.6-1.el7.x86_64.rpm

rpm –ivh mongodb-org-tools-4.2.6-1.el7.x86_64.rpm
```

**3、** 修改配置文件

vim /etc/mongod.conf

net:bindIp: 127.0.0.1 改为 0.0.0.0



**4、** 启动并开机自启

```sh
systemctl start mongod       #启动

systemctl enable mongod      #配置开机自启

systemctl restart mongod   #重启
```

 

**5、** 连接数据库

```sh
mongo --host 127.0.0.1:27017
```

 

**6、** 查看所有数据库

show dbs

 

**7、** 进入admin数据库

use admin

 

**8、** 设置超级管理员

```sh
//db.addUser(“weitu2019”,”weitu2019!”)

db.createUser(

 {

  user: "xxxx",

  pwd: "xxxx!",

  roles: [ { role: "userAdminAnyDatabase", db: "admin" }, "readWriteAnyDatabase" ]

 }

)
```

 

```sh
db.createUser(

 {

  user: "xxxx",

  pwd: "xxxx!",

  roles: [ { role: "readWrite", db: "local" }]

 }

)
```

 

**9、** 验证用户名密码

```sh
db.auth("weitu2020", "xxxx!")
```



# Nginx 安装步骤

**1、** 上传服务器：

将Nginx安装rpm包上传到服务器上;

**2、** 安装Nginx：

进入到 Nginx文件目录下，运行 install.sh文件；

**3、** 配置Nginx

配置文件的默认路径在 /etc/nginx/ 下：vim /etc/nginx/nginx.conf

也可在 /etc/nginx/conf.d/下新增.conf文件 



请求返回 413 Request Entity Too Large

Nginx 限制上传文件只能有1M， 在 http{} 中加入 client_max_body_size 10m;

 

**4、** 重启Nginx

修改完后重启Nginx： /usr/sbin/nginx -s reload 



# Mysql8.0安装步骤

**1、** 上传服务器：

将Mysql安装rpm包上传到服务器上;

**2、** 安装Mysql：

进入到Mysql文件目录下，运行install.sh文件；

```sh
rpm -ivh mysql-community-common-8.0.20-1.el7.x86_64.rpm

rpm -ivh mysql-community-libs-8.0.20-1.el7.x86_64.rpm

rpm -ivh mysql-community-libs-compat-8.0.20-1.el7.x86_64.rpm

rpm -ivh mysql-community-embedded-compat-8.0.20-1.el7.x86_64.rpm

rpm -ivh mysql-community-client-8.0.20-1.el7.x86_64.rpm

rpm -ivh mysql-community-server-8.0.20-1.el7.x86_64.rpm
```

 

如果报错：file /usr/share/mysql/charsets/swe7.xml from install of mysql-community-common-5.7.27-1.el7.x86_64 conflicts with file from package mariadb-libs-1:5.5.56-2.el7.x86_64

 

则执行 yum remove mysql-libs

 

**3、** 修改配置文件：

修改/etc/my.cnf文件，在文件的最后面加入下面信息：

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION

 

关闭服务，修改mysql配置文件:

```sh
systemctl stop mysqld.service
```

修改配置文件

vi /etc/my.cnf

mysqld下面添加skip-grant-tables 保存退出启动服务(如图)。

重启服务：

```sh
systemctl start mysqld.service
```

修改root密码：

```sh
mysql -u root  # 刚才添加了skip，这种情况下不需要密码直接回车

use mysql  # 指定库

update user set authentication_string=password('sfj#12348') where user='root' and host='localhost';

flush privileges; # 清空权限表缓存

exit;
```

vi /etc/my.cnf #把 skip-grant-tables  # 把这句删除保存退出重启mysql服务，恢复密码登录。

重启服务

```sh
systemctl restart mysqld.service
```

 

查看密码等级：

```sh
show variables like 'validate_password%';

set global validate_password_policy=0;

flush privileges;

 

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'sfj#12348' 
```

 

创建数据库：

登入：

```sh
mysql -h localhost -u root –p
```

输入密码：

weitu@123456

创建数据库

```sh
create database ermsDb;
```

创建用户

```sh
create user 'haiguan'@'%' identified by 'WT@weitu2020';
```

 

```sh
ALTER USER 'haiguan'@'%' IDENTIFIED WITH mysql_native_password BY 'WT@weitu2020';
```

 

给用户赋值权限

```sh
grant all privileges on *.* to 'haiguan'@'%' identified by 'WT@weitu2020' with grant option;

GRANT ALL ON *.* TO 'root'@'%';
FLUSH PRIVILEGES ;
```

 

另，给普通用户远程连接的权限：
1、授权 myuser 用户对指定库的所有表，所有权限并设置远程访问

```sh
GRANT ALL ON *.* TO haiguan@'%';

FLUSH PRIVILEGES ;



update user set authentication_string=password('weitu@123456') where user='root' and host='localhost';

 

update user set authentication_string=password("weitu@123456") where user='root' and host='localhost';

 

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'weitu@123456';

 

SET PASSWORD FOR 'root'@'localhost'= "weitu@123456"
```

 

出现大小写敏感时要修改，修改配置文件vi /etc/my.cnf

vi /etc/my.cnf

最开始增加：

[mysql]

```ini
# 设置mysql客户端默认字符集

default-character-set=utf8

# 在[mysqld]里面增加：

# 服务端使用的字符集默认为8比特编码的latin1字符集

character-set-server=utf8

# 创建新表时将使用的默认存储引擎

default-storage-engine=INNODB

lower_case_table_names=1

max_allowed_packet=16M
```



# MySQL7.0 安装步骤

**1、** 上传服务器：

将Mysql安装rpm包上传到服务器上;

**2、** 安装Mysql：

进入到Mysql文件目录下，运行install.sh文件；

```sh
rpm -ivh mysql-community-common-5.7.27-1.el7.x86_64.rpm

rpm -ivh mysql-community-libs-5.7.27-1.el7.x86_64.rpm

rpm -ivh mysql-community-libs-compat-5.7.27-1.el7.x86_64.rpm

rpm -ivh mysql-community-embedded-compat-5.7.27-1.el7.x86_64.rpm

rpm -ivh mysql-community-client-5.7.27-1.el7.x86_64.rpm

rpm -ivh mysql-community-server-5.7.27-1.el7.x86_64.rpm
```

 

如果报错：file /usr/share/mysql/charsets/swe7.xml from install of mysql-community-common-5.7.27-1.el7.x86_64 conflicts with file from package mariadb-libs-1:5.5.56-2.el7.x86_64

 

则执行 yum remove mysql-libs

 

**3、** 修改配置文件：

修改/etc/my.cnf文件，在文件的最后面加入下面信息：

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION

关闭服务，修改mysql配置文件:

```sh
systemctl stop mysqld.service
```

修改配置文件

vi /etc/my.cnf

mysqld下面添加skip-grant-tables 保存退出启动服务(如图)。

重启服务：

```sh
systemctl start mysqld.service
```

 

修改root密码：

```sh
mysql -u root  # 刚才添加了skip，这种情况下不需要密码直接回车

use mysql  # 指定库

update user set authentication_string=password('sfj#12348') where user='root' and host='localhost';

flush privileges; # 清空权限表缓存

exit;
```

vi /etc/my.cnf #把 skip-grant-tables  # 把这句删除保存退出重启mysql服务，恢复密码登录。

重启服务

```sh
systemctl restart mysqld.service
```

 

查看密码等级：

```sh
show variables like 'validate_password%';

set global validate_password_policy=0;

flush privileges;
```

 

```sh
alter user 'root'@'localhost' identified by 'sfj#12348';

flush privileges;
```

 

创建用户

```sh
set global validate_password_policy=0;

flush privileges;

create user songda@'%' identified by 'sfj#12348';
```

 

给用户赋值权限

```sh
grant all privileges on *.* to 'songda'@'%' identified by 'sfj#12348' with grant option;
```

 

***数据库报错：***

### ERROR 1045 (28000): Access denied for user 'root'@'localhost' (using password: YES) 问题

 

mysqld下面添加skip-grant-tables 保存退出启动服务(如图)。

重启服务：

```sh
systemctl start mysqld.service
```

 

在mysql5.7以下的版本如下：

```sh
mysql> UPDATE user SET Password=PASSWORD('sfj#12348') where USER='root' and host='127.0.0.1' or host='localhost';//把空的用户密码都修改成非空的密码就行了。
```

在mysql5.7版本如下：

```sh
update mysql.user set authentication_string=password('sfj#12348') where user='root' and host='127.0.0.1' or host='localhost';


mysql> FLUSH PRIVILEGES;
```

  

### ERROR 2006 (HY000): MySQL server has gone away 问题

```
vi /etc/my.cnf

max_allowed_packet=1073741824
```





