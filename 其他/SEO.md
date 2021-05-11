# 谷歌分析（ga）工具

 工具地址：https://www.google.com/analytics/ 

在项目中的 templates 下的 base.html, login.html, register.html 加入这行 GA 代码

```js
	<!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-152710976-1">			
    </script>
    <script>
      window.dataLayer = window.dataLayer || [];
      function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());

      gtag('config', 'UA-152710976-1');
    </script>
```



# 谷歌管理员工具

 https://search.google.com/ 

打开域名解析界面->修改记录

```shell
Verification method:
Domain name provider
Failure reason:
We couldn't find your verification token in your domain's TXT records.
We found these DNS TXT records instead:
v=spf1 include:spf.qiye.aliyun.com -all
Sometimes DNS changes can take a while to appear. Please wait a few hours, then reopen 

your property in Search Console. If verification fails again, try adding a different DNS TXT record.
```



```
记录类型：TXT

主机记录： @

解析路线：默认

记录值： <Google给你的TXT值>
```



# sitemap.xml 和 robots.txt

 https://www.xml-sitemaps.com/  生成 sitemap 并把 xml 放到网站的某个位置， 在谷歌管理员工具中配置它

```nginx
    location /sitemap.xml {
        alias /home/code/sitemap.xml;
    }

    location /robots.txt {
        alias /home/code/robots.txt;
    }
```

robots.txt 需要 sitemap.xml,  https://robots.51240.com/  生成并放在网站的根目录下



# 相关博客

 https://cn.bluehost.com/blog/websites/3874.html 

 https://www.cifnews.com/article/54418 

 https://www.zzshe.com/Blog/76.html 