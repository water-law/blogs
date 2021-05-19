## Linux 相关指令

- head、tail

  前十行

  ```bash
  head -n 10 /etc/profile
  ```

- 后十行

  ```bash
  tail -n 10 /etc/profile
  ```

- df

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

- fdisk

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

#### 新加一块硬盘

VMVare 增加一块硬盘， 重启

使用 fdisk -l， 



## 其他博客

[LINUX逻辑卷(LVM)管理与逻辑卷分区](https://www.cnblogs.com/kawashibara/p/8861940.html)