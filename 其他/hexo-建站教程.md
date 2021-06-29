### 安装

参考官方文档 [hexo](https://hexo.io/zh-cn/docs/ )

<!--more-->

### 创建 github 仓库并配置 ssh 公钥

在 github 创建一个 <用户名>.github.io 的仓库

保管好你的 ssh 私钥， 以便于将来迁移到新的电脑上要使用到该密钥。

### 创建并部署 hexo 项目

在项目根目录下的 _config.yml 文件添加 github 仓库地址 

```yaml
# Deployment
## Docs: https://hexo.io/docs/deployment.html
deploy:
  type: git
  repo: git@github.com:water-law/water-law.github.io.git
  branch: master
```

为了将 markdown 同步到 github 仓库， 除了安装 git 外， 还需要安装一个插件 hexo-deployer-git，

```bash
$ npm install hexo-deployer-git --save
```



### 配置 hexo 博客主题

[hexo官方主题](https://hexo.io/themes/) 中最常用的是 [Next 主题](https://github.com/iissnan/hexo-theme-next)，下载好 Next 主题后， 还需要配置一下 样式 themes/next/_config.yml 

修改 hexo 主题为 next

```yaml
# Extensions
## Plugins: https://hexo.io/plugins/
## Themes: https://hexo.io/themes/
theme: next
```

设置博客主题样式

```yaml
# Schemes
#scheme: Muse
#scheme: Mist
#scheme: Pisces
scheme: Gemini
```

添加社交账号支持

```yaml
social:
  GitHub: https://github.com/water-law || github
  E-Mail: mailto:goowaterlaw@gmail.com || envelope
  #Weibo: https://weibo.com/yourname || weibo
  #Google: https://plus.google.com/yourname || google
  #Twitter: https://twitter.com/yourname || twitter
  #FB Page: https://www.facebook.com/yourname || facebook
  #VK Group: https://vk.com/yourname || vk
  #StackOverflow: https://stackoverflow.com/yourname || stack-overflow
  #YouTube: https://youtube.com/yourname || youtube
  #Instagram: https://instagram.com/yourname || instagram
  #Skype: skype:yourname?call|chat || skype
```

### 增加 rss 订阅功能

在项目根目录下的 _config.yml 文件中的 Extensions 下添加

```yaml
# Extensions
## Plugins: https://hexo.io/plugins/
## Themes: https://hexo.io/themes/
theme: next
feed:
  type: atom
  path: atom.xml
  limit: 20
  hub:
  content:
  content_limit: 140
  content_limit_delim: ' '
```

rss 功能需要 hexo-generator-feed 插件， 使用如下安装命令

```bash
$ npm install hexo-generator-feed --save
```

### 增加分类和标签页

新建一个分类页面

```bash
$ hexo new page categories
```

会发现你的`source`文件夹下有了`categorcies/index.md`，打开`index.md`文件将title设置为`title: 分类`， 并增加一行

```markdown
type: "categories"
```

新建一个标签页面,  

```
$ hexo new page tags
```

会发现你的`source`文件夹下有了`tags/index.md`，打开`index.md`文件将title设置为`title: 标签`, 并增加一行

```markdown
type: "tags"
```

在主题配置文件中启用 标签、分类和归档功能

```yaml
menu:
  home: / || home
  #about: /about/ || user
  tags: /tags/ || tags
  categories: /categories/ || th
  archives: /archives/ || archive
  #schedule: /schedule/ || calendar
  #sitemap: /sitemap.xml || sitemap
  #commonweal: /404/ || heartbeat
```

### 添加阅读全文按钮

因为在你的博客主页会有多篇文章，如果你想让你的文章只显示一部分，多余的可以点击阅读全文来查看，那么你需要在你的文章中添加

```html
<!--more-->
```

### 添加评论系统

next 主题下已经集成了 valine， [注册地址](https://leancloud.cn/)，注册完以后需要创建一个应用，名字可以随便起，然后 **进入应用->设置->应用key**

获取你的appid 和 appkey， 打开**主题配置文件** 搜索 **valine**，填入appid 和 appkey 并启用 valine. 

```yaml
valine:
  enable: true # When enable is set to be true, leancloud_visitors is recommended to be closed for the re-initialization problem within different leancloud adk version.
  appid: # your leancloud application appid
  appkey: # your leancloud application appkey
```

在存储-数据中创建Class ‘Comment’，用作评论的数据库表。

最后！记得在Leancloud -> 设置 -> 安全中心 -> Web 安全域名 把你的域名加进去.