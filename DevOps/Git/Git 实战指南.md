# Git 实践

## 如何高效的使用 Git

如何高效的使用 Git 中文版： [how-to-use-git-efficiently](https://crossoverjie.top/JCSprout/#/soft-skills/how-to-use-git-efficiently)

如何高效的使用 Git 英文原文： [how-to-use-git-efficiently](https://medium.freecodecamp.org/how-to-use-git-efficiently-54320a236369)

## 分支模型

1. master: 主分支，代表生产最新版本，从 release 分支合并。
2. release：准生产分支，从 master 分支拉取，只能从 feat 分支合并。
3. uat: uat 环境测试分支，从 master 分支拉取，只能从 feat 分支合并。
4. feat: 需求分支。如：feat/2021-2021-1-001。从  master 分支拉取， 合并到 uat 分支。
5. dev: 个人开发分支，如: dev/zhangsan-feat/xq-2021-1-001



## 工作流

1. 一个需求分配到开发团队后，由团队技术组长从代码仓库中某项目(如 Git )的 master 分支上拉取需求分支，

   例如: feat/xq-2021-1-001。拉完后立刻将 feat 打上初始 tag，命名规则: tfb_product_feat/xq-2021-1-001_20211228

2. 需求开发人员认领自己的需求分支，从需求分支上拉取自己个人开发分支，例如: dev/zhangsan-feat/xq-2021-1-001 进行开发。

3. 当需求开发人员完成开发并自测通过后，提交 MR(merge request) 请求: 20210101: dev/zhangsan-feat/xq-2021-1-001 ->feat/xq-2021-1-001 ，

   审代码的人审核完代码后，完成代码审查表填写，点击 merge 合并到 feat(需求)分支，完成 merge 后将该需求分支打上需求分标签 (tag) 如: tfb_feat/xq-2021-1-001_20211014，打完 tag 后，开发人员回去将版本发布清单打印出来找宙代码的人签字。

4. 开发人员在找完组长签字后，提交 MR: 20210101: feat/xq-2021-1-001->uat, 

   去找 uat 版本管理员申请合入 uat，uat 版本管理员检查版本清单签字无误后，点击 merge 将该 feat 合入uat，然后将 uat 分支打个 tag 如:  tfb_uat_20211014，tfb_uat_20211014_1。

   合并完后就可以去版本机里通过抽增量工具将版本抽取出来，然后部署到 uat 服务器上。

5. 在上线时生产版本值班人员搜集所有本次版本窗口计划上线的需求分支准生产标签，从当前 master 分支拉取一个 release 分支(准生产分支)，将 release 分支打上初始 tag，如: tfb_release_20210101_init。

   将收集到的需求分支准生产标签都合入 release 分支。合入后将 release 打个 tag，然后在版本机上通过抽增量工具将版本包抽取出来，部署到回归服务器上，其中 sql 应在准生产数据库执行。

6. 在上完版本且验证无误后，由生产版本值班人员将 release 合入 master，合入后将 master 打个 tag，如: tfb_master_20211014。

​       然后开发人员提交 master->feat(开发人员自己手里未上生产的需求)的 merge request。版本管理员处理相应 MR。

## feat 到 uat 冲突

当分支合并出现冲突时，一般解决办法是:将源分支 checkout，pull from 目标分支，然后再合并。

在这里，源分支指的就是 feat 了，目标分支指的是 uat。

但是如果按照这种方法的话，我们的 feat 的代码就会变成 uat，而在现有模型中，开发应基于生产代码，

所以本着这一原则，我们便不能对 feat 分支做修改，而是应该考虑从 uat 分支入手。

建议单独 clone 一个项目用作冲突处理，这样不会对本地正在开发的造成影响。 

```bash
git fetch --all #拉取远程所有的分支、tag 到本地
git checkout origin/uat #切换到远程 uat 分支 
git pull origin uat #更新
git checkout -b uat conflict_feat/xq-2020-10-198_20211018 #从当前分支(origin/uat)检出一条处理冲突的临时分支
git merge origin/feat/xq-2020-10-198  #将 feat 合入进这条处理冲突的临时分支这个时候就产生了冲突 
git status #查看当前状态
```

在 idea 里手动处理冲突: VCS->git->resolve conflicts

```bash
git commit -m "deal with conflicts feat/xq-2020-10-198" #提交
git push origin uat_conflict_feat/xq-2020-10-198_20211018 
#将该临时分支推到远端去 gitlab 上发起 mr:uat_conflict_feat/xq-2020-10-198_20211018-> uat将原来 mr关闭
```



## master 到 feat 冲突

1. 在 git bash 命令行中依次执行:

```bash
git fetch --all
git checkout origin/feat/xq-2021-12-425
git pull origin feat/xq-2021-12-425
git checkout -b master_conflict_feat/xq-2021-12-425 (注意分支名)
git merge origin/master
```



2. 在 idea 中解决冲突: vCs-->Git-->Resolve Conflicts，对弹框中的冲突文件点击 Merge 进行合并处理

```bash
git commit -m "deal with conflicts master" #提交
git push origin feat_conflict_master/xq-2021-12-425_20211018 
#将该临时分支推到远端去 gitlab 上发起 mr:feat_conflict_master/xq-2021-12-425_20211018-> feat 将原来 mr关闭
```



## cherry-pick

feat->uat

如上图，带红圈的为本次 dev 到 feat 的 mr 记录， 通过黑圈的按钮可以复制左边的 mergeid 

```bash
git fetch --all #拉取远程所有的分支、tag到本地 
git checkout origin/uat #切换到远程 uat 分支 
git pull origin uat #更新
git checkout -b cherry-pick/feat/xq-2021-1-259_20211213 #从当前分支(origin/uat)检出一条处理冲突的临时分支
git cherry-pick 远程分支 mergelD -m 1 
#(例:git cherry-pick origin/feat/xg-2021-1-259 aae2cle599a8e834410a4337e93d8ffdf25593b1-m 1)
```

执行到这一步，如果报【cherry-pick】 操作已存在的错误，需要执行【git cherry-pick --abort】取消上一次 cherry-pick 操作，重新执行上述步骤即可; 

如果本次 mr 有冲突报错那是正常的，

① 在 idea 里手动处理冲突: VCS->git->resolve conflicts; 

②  commit

```bash
git commit -m "deal with conflicts feat/xq-2021-1-259" 
```

③  push

```bash
git push origin cherry-pick/feat/xq-2021-1-259_20211213
```

③ 去 gitlab 上发起 mr (cherry-pick/feat/xq-2021-1-259_20211213--> uat);

⑤ 关闭原来的 mr 如果没有冲突，直接 push 即可

2、如果多出来的改动部分在 uat，说明本身 feat 分支存在问题，需要排查



## 配置文件提交方法

项目中有某个配置文件， 其中某个片段的代码要提交，其他片段的代码不提交，可以这样做。

git update-index --assume-unchanged Makefile 命令会把 Makefile 文件留存在目录下，

但是这会使 Git 假定随后的修改没有被追踪。这样，我可以提交我想要发布的有 CFLAGS I的版本，用 --assume-unchanged 标记

```bash
$ git update-index --no-assume-unchanged Makefile  # 追踪 Makefile 文件

$ git add -p Makefile  # 选取 Makefile 文件中要添加的部分

# [添加我要发布的Makefile的变更]
$ git commit 

$ git update-index --assume-unchanged Makefile # 取消追踪 Makefile 文件

$ git push
```



## git stash

git stash save "WIP: message" 会将修改暂存在 .git 文件夹下的某个目录下，使用 git stash list 查看所有暂存记录

然后使用 git stash apply stash{n} 应用暂存，可能需要处理冲突。