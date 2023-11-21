##  安装

```bash
brew install jenv
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
echo 'eval "$(jenv init -)"' >> ~/.zshrc
```

## 使用

```bash
增加一个jdk ：jenv add
删除一个jdk：jenv remove
设置默认jdk：jenv global
设置当前环境jdk：jenv local
查看jenv托管的所有jdk：jenv versions
```



jenv add /Library/Java/JavaVirtualMachines/jdk1.8.0_73.jdk/Contents/Home

jenv local 10.0.1