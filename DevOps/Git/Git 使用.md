## 常用指令
### 修改远程仓库地址

```
git remote set-url [--push] <name> <newurl> [<oldurl>]
```

如将 origin  的远程仓库地址设置为 http://github.com/water-law/blogs.git

```shell
git remote set-url origin  http://github.com/water-law/blogs.git
```

### 重名名分支

```
git branch -m oldBranchName newBranchName
```

### 删除分支
```
git branch -d branchName
```
或者强制删除
```
git branch -D branchName
```
### 对比两个分支的差异

```
git diff master...feat/XQ-2021-6-371
```


### 分段提交代码
```
git add -p <file>
```

### stash 暂存指定文件
```
git stash push -m "WIP: XQ-2021-6-371: xxx"  Hello.java
```

### stash 暂存
```
git stash save "WIP: XQ-2021-6-371: xxx"
```

### stash 查询

```
git stash list
```
### 应用 stash 的代码

```
git stash apply stash@{N}
```
### 删除 stash

```
git stash drop stash@{N}
```
### 放弃跟踪某个文件

```
git update-index --assume-unchanged <file>
```

### 跟踪某个文件

```
git update-index --no-assume-unchanged <file>
```

### git reset
语法
回退到 commit_id 提交，--soft 会保持 commit_id 到当前 HEAD 的文件修改内容， commit_id 到当前 HEAD 的提交记录会被删除
```
git reset --soft <commit_id>
```

回退到 commit_id 提交，--hard 不保持 commit_id 到当前 HEAD 的文件修改内容，commit_id 到当前 HEAD 的提交记录会被删除
```
git reset --hard <commit_id>
```
### cherry-pick
cherry-pick 用于提取某个 commit 记录追加到当前分支上
拉取远程所有的分支、tag到本地

```
git fetch --all 
```
切换到远程uat分支

```
git checkout origin/uat
```
更新


```
git pull origin uat
```
从当前分支（origin/uat）检出一条处理冲突的临时分支

```
git checkout -b cherry-pick/feat/xq-2021-1-259_20211213 
```


可以先提 mr, 得到一个 commit_id(merge 的 id)  aae2c1e599a8e834410a4337e93d8ffdf25593b1 
在当前分支执行该 commit_id 的 cherry-pick
```
git cherry-pick 远程分支 mergeID -m 1
```
（例：git cherry-pick origin/feat/xq-2021-1-259 aae2c1e599a8e834410a4337e93d8ffdf25593b1 -m 1）

## 分支命名规范

### 需求分支
feat+/+需求号，如 ：feat/XQ-2021-6-278

### 开发分支
dev+/+姓名+-+需求分支名，如：dev/wuxincheng-feat/XQ-2021-6-278
### 临时需求分支
mr+/开发分支名+-+to-feat-日期，如：mr/dev/wuxincheng-feat/XQ-2021-6-278-to-feat-20210930
### 临时 uat 分支
mr+/需求分支+-to-uat-日期，如：mr/feat/XQ-2021-6-278-to-uat-20210930
### 临时 release 分支
mr/需求分支+to-release-日期，如：mr/feat/XQ-2021-6-278-to-release-20210930
### 临时同步 master 代码分支
mr/syncmaster-to-需求分支-日期，如：mr/syncmaster-to-feat/XQ-2021-6-278-20210930

## master 分支同步到 feat 分支
上线后，同步 master 分支代码到 feat 分支

### 检出 master
```
git checkout master
```
### 更新 master
```
git pull origin master
```

### 检出 feat
```
git checkout feat/xxx
```
### 更新 feat
```
git pull origin feat/xxx
```
### 从 feat 检出新分支
```
git checkout -b mr/syncmaster-to-feat/xxx-20220520
```
### 合并 master
```
git merge master
```
merge 这步无冲突则进行 push, 有冲突则解决冲突，commit 后再 push
### push

```
git push origin mr/syncmaster-to-feat/xxx-20220520
```
### 提 mr
提 mr/syncmaster-to-feat/xxx-20220520 到 feat 的 mr



## feat 分支合并到 uat 分支
开发分支合并到需求分支以后，开发人员通过 git pull 指令从远程拉取最新 feat 分支代码以及最新的 uat 分支代码到本地。每次合并都需要先拉取最新的 uat 分支。

从 uat 分支拉取一个 mr 临时分支，命令格式为 mr/feat/xxxx-to-uat-20220428，在本地执行 git merge feat/xxxx，以后，将mr 分支推送到远程 coding，并发起合并请求。

拉取合并请求的步骤与合并到 feat 的过程一致
评审人员处理此 checklist 可简单处理，直接通过，点击允许后，仍由国结版本发布账号处理合并，并打 tag 后执行构建计划，将版本打到 uat 服务器上。



### 拉取远程所有的分支、tag 到本地

```
git fetch --all
```

### 切换到远程 uat 分支 

```
git checkout uat
```

### 更新 uat

```
git pull origin uat
```

### 从当前分支（origin/uat）检出一条处理 mr 的临时分支

```
git checkout -b mr/feat/xq-2021-11-159_20220428-to-uat
```

如果本地没有 feat 分支则执行 fetch
### 拉取 feat 分支到本地 

```
git fetch origin feat/xq-2021-11-159:feat/xq-2021-11-159
```

有 feat 分支则切换到  feat 分支 执行 pull

```
git checkout feat/xq-2021-11-159

```

```
git pull origin feat/xq-2021-11-159
```

或者不 checkout 一次到位
```
git pull origin feat/xq-2021-11-159:feat/xq-2021-11-159
```



### 合并 feat 分支
 ```
git checkout mr/feat/xq-2021-11-159_20220428-to-uat
 ```

```
git merge feat/xq-2021-11-159
```

### 有冲突处理冲突后 commit, push，无冲突则直接 push

```
git commit -m "uat mr xq-2021-11-159"
```

### 推送分支到远程

```
git push origin mr/feat/xq-2021-11-159_20220428-to-uat
```

### 提 mr/feat/xq-2021-11-159_20220428 到 uat 的 mr



