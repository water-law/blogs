# git rebase

### 合并 commit

在本地分支做修改后， add commit 后 

```bash
$ git commit -m "3 1"
[master 7b3a8d7] 3 1
```

pull 远程仓库， 发现有冲突手动合并

```bash
$ git commit -m "3 11"
[master 7b3a8d7] 3 11
```

分叉了

```bash
$ git log --oneline --graph
*   7b3a8d7 (HEAD -> master) 3 11
|\
| * faa932d (origin/master) modify 1 and add 2
* | e42c2f2 3 1
|/
* d070ffb add 1.txt 1

```

开始使用 git rebase

```bash
$ git rebase
First, rewinding head to replay your work on top of it...
Applying: 3 1
Using index info to reconstruct a base tree...
M       1.txt
Falling back to patching base and 3-way merge...
Auto-merging 1.txt
CONFLICT (content): Merge conflict in 1.txt
error: Failed to merge in the changes.
Patch failed at 0001 3 1
Use 'git am --show-current-patch' to see the failed patch

Resolve all conflicts manually, mark them as resolved with
"git add/rm <conflicted_files>", then run "git rebase --continue".
You can instead skip this commit: run "git rebase --skip".
To abort and get back to the state before "git rebase", run "git rebase --abort".
```

进入 no branch git rebase 临时分支

根据提示合并冲突并且 git add， 再执行 

```bash
git rebase --continue
```

rebase 成功后可以看到只有之前提交的那条 commit 记录， 如果想回到 git rebase 那个点的话应该使用

```bash
git rebase --abort
```

git rebase --abort 后仍可继续重新 git rebase。

如果想放弃之前的提交记录的话则使用

```bash
git rebase --skip
```

这个操作相当于放弃本地修改回到远程仓库的最新代码， 尽量不要使用， 应当使用 git rebase --abort。

最后 push 上去就可以了。

这里的应用场景有点类似那个， 同学 A 和同学 B 共同维护一个分支， 同学 B 改了某个文件并提交， 同学 A 改了相同的文件，并且本地有多次 commit,  结果同学 A 因为修改前没有 pull 导致 commit 后再 push 失败， 同学 A 使用 git pull 发现

git 因为自动合并分叉了， 所以使用 git rebase 把同学 B 的修改 merge 了并且保留同学 A 上次 commit 信息(message), 如果本地有多次 commit, 则可以使用 git rebase 合并。

这里有个问题， git pull 后需要 merge 然后 commit, 可是 git rebase 回滚又提示有冲突需要 merge, 相当于第一次的 merge 又得重做一次， 相同的 merge 做两遍， 肯定有更好的办法， 办法是有的， 那就是 git pull --rebase。

```bash
git pull --rebase
```

使用 git pull --rebase, 然后后面的操作就是 git rebase 操作了， 只需要一次 merge 就可以了。

### git rebase 的局限？

git rebase 只能合并本地的 commit,  已提交到远程仓库的 commit 无法合并， 而且当你 rebase 的时候， 如果 pull 不是最新的(是的， 同事又更新了代码)， 则 push 失败后再次执行 git pull --rebase 合并代码， 只是这样你的代码是在你的同事的提交后面。

### 不同分支之间的合并

 在  feature  分支开发一个新的功能。 

```bash
git branch -b feature

git checkout feature
```

改完 feature 分支之后， 需要将它合并的主分支中。 

```bash
git rebase master
```

git rebase master 是以 master 为基础，将 feature 分支上的修改增加到 master 分支上，并生成新的版本。

接下去就和前面一样合并冲突， add, git rebase --continue, 然后执行

 

```bash
git checkout master

git merge feature
```

