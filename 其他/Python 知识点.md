## 基础

### 方法

装饰器

### 变量

Python 中变量有类变量，成员变量，保护变量、私有变量

```python
class Cls:
    # 类变量
    cls_var = None


class Member:
    def __init__(self, b):
        # 成员变量
        self.member_var = b


class Protected:
    def __init__(self, v):
        # 保护变量
        self._protected_var = v


class Private:
    def __init__(self, v):
        # 私有变量
        self.__private_var = v
        p = Protected(12)
        print(p._protected_var)


if __name__ == '__main__':
    p = Private(1)
    print(p.__private_var)
```

p.__private_var 会报错，我们来看下什么原因

安装 uncompyle 反编译库， 现将 py 编译为 pyc, 然后再反编译 pyc 为 py, 

编译 py 文件,  因为我放在  basic 这个 package 下， 编译单个文件有点问题

```bash
python3 -m compileall basic
```

反编译 

```bash
uncompyle6 var.cpython-36.pyc > var.py
```

得到 

```python
# uncompyle6 version 3.6.0
# Python bytecode 3.6 (3379)
# Decompiled from: Python 3.6.0 (v3.6.0:41df79263a11, Dec 23 2016, 08:06:12) [MSC v.1900 64 bit (AMD64)]
# Embedded file name: basic\var.py
# Compiled at: 2019-12-23 16:08:09
# Size of source mod 2**32: 502 bytes


class Cls:
    cls_var = None


class Member:

    def __init__(self, b):
        self.member_var = b


class Protected:

    def __init__(self, v):
        self._protected_var = v


class Private:

    def __init__(self, v):
        self._Private__private_var = v
        p = Protected(12)
        print(p._protected_var)


if __name__ == '__main__':
    p = Private(1)
# okay decompiling var.cpython-36.pyc
```

可见， python 的私有变量本质上还是保护变量， 只要加上'_类名' 依旧可以访问

试下

```python
if __name__ == '__main__':
    p = Private(1)
    print(p._Private__private_var)
```

打印出

```bash
E:\Projects\pysonar\env\Scripts\python.exe E:/Projects/pysonar/basic/var.py
12
1
```



### 单例

### metaclass



## GIL

可以使用多进程

可以使用协程

可以使用 Cython、PyPy

## 框架

### Django

### Celery

celery 原理