### 1. 建站教程
zmrenwu 的入门教程，[传送地址](https://www.zmrenwu.com/post/2/)，作为一个开发者，写一篇博客总结自己开发经历，反省自己，努力进步，并给 python 新手提供一个好的学习范例。

### 2. 虚拟环境
python 语法简洁，同时 python 库十分强大， 对于新手而言，入门门槛较低，本网站采用 Django 框架进行开发。
进行开发前，你应该具备以下知识： 

1. 使用 virtualenv 或其他工具创建虚拟环境，以避免环境污染。 

2. 了解 python 基本语法。

3. 了解 Linux 常用命令或相关操作系统命令工具

关于 virtualenv 的安装使用，可以参考以下命令：
```bash
pip3 install virtualenv
virtualenv -p python3.6 env    # 3.6 是 python 的版本号， 你也可以简写 python3, env 是虚拟环境的目录名称
source ./env/bin/activate    # 在 Linux, 你也可以使用 . ./env/bin/activate
```
进入虚拟环境， 你看到的是
```bash
(env) zjp@localhost:~/Projects> 
```
### 3. 安装 django
在虚拟环境中使用 pip install django==1.11.4
```bash
(env) zjp@localhost:~/Projects> pip install django==1.11.4
(env) zjp@localhost:~/Projects> django-admin startproject waterlawblog
(env) zjp@localhost:~/Projects> cd waterlawblog
(env) zjp@localhost:~/Projects/waterlawblog> pip freeze > requirements.txt
(env) zjp@localhost:~/Projects/waterlawblog> ./manage.py runserver
```
网站运行起来了， 去访问 http://127.0.0.1:8000/
### 4. 程序员社区推荐
除了 [StackOverFlow](https://stackoverflow.com/), [Github](https://github.com/) 外， 国内的[掘金社区](https://juejin.im/timeline)也是个不错的网站。 另外博主最近迷上一小众社区[v2ex](https://www.v2ex.com/)， 有空就去逛逛。

### 5. 本网站源代码
本网站的开放源代码使用 BSD3.0 许可证书开放于 Github, [Github 地址](https://github.com/water-law/waterlawblog)