### 安装

[oracle sql 官方](https://www.oracle.com/cn/downloads/)

sqlplus

```bash
sqlplus/ as sysdba
```

 指定‘<username/password>’

```shell
sqlplus system/manager
```

oracle 默认用户名密码

       超级管理员：sys　　 密码：change_on_install；
    
       普通管理员：system 　　 密码： manager；
    
       普通用户：scott 　　密码： tiger；(是在选定了“样本“方案数据库之后产生的)
    
       大数据用户：sh 　　 密码：sh;

### sqlplus 命令行工具

**查看数据库 select name from v$database;**

# jdbc 连接 oracle rac 报错： Got minus one from a read call

于是接着问度娘，关于该报错信息终于找到了一个我认为比较合理的解释
http://news.newhua.com/news/2011/0331/119074.shtml
大意是指一些老的JDBC驱动包是不支持RAC数据库的，想着我们这套系统还是多年前开发的，极有可能是JDBC驱动包过老引起的。马上建议同事将JDBC驱动包换成最新的，于是从oracle的安装目录中找了JDBC驱动ojdbc6.jar替换原来的JDBC驱动包，再一测试，果然没问题了。

删除数据库要在注册表中把 ORACLE_SID 改回去