```bash
git add xxx

git commit -m "commit message"

git pull origin master:dev

git fetch origin master:tmp

git merge tmp --no-ff

git push origin dev:master

git branch -d tmp
```



```bash
#切换到当前你自己的分支，获取最新的代码
git branch consult
#rebase远程master分支的最新代码，合并到你自己的分支
git rebase origin/master
#如果出现冲突，则需要解决冲突
git diff -w
#解决冲突文件
vim 冲突文件
git rebase origin/master
git rebase --continue
git add .
git commit -m "xx"
git log
#然后fetch到当前分支
git fetch
#然后push到自己的分支
git push origin consult
#push不上去的时候，直接强push(慎用)
git push -f origin consult
```

