### 0. 修复 bug

访问 https://waterlaw.top/articles/1 出现 500 号错误， 目前发现的情况有：
jieba 中文分词的 cache 每次大约占用 0.8～1.8 秒，怀疑是此原因， 待排查。

nginx.conf 文件修改如下， 则正常, 另外服务器不要使用  django 原生服务器(仅在本地开发中使用)

```bash
python3 ./manage.py runserver 0.0.0.0:8000
```

应该使用 gunicorn + wsgi， 参见 [项目-wiki](https://github.com/water-law/waterlawblog/wiki/%E9%A1%B9%E7%9B%AE-wiki)

```nginx
upstream blog_backend {
    server 127.0.0.1:8000 fail_timeout=1s weight=4;
}

server {
    listen 80;
    server_name waterlaw.cn;
    access_log /dev/null;
    error_log /home/zjp/waterlawblog/logs/nginx.log;
    client_max_body_size 5m;

    listen 443 ssl;
    ssl_certificate  /etc/nginx/cert/214259168910063.pem;
    ssl_certificate_key  /etc/nginx/cert/214259168910063.key;
    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
    ssl_prefer_server_ciphers on;
    if ($scheme = http){
        return 301 https://$server_name$request_uri;
    }

    location / {
        proxy_pass http://blog_backend;
        proxy_redirect off;
        proxy_buffering on;
        proxy_set_header Host $http_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        gzip on;
        gzip_types application/json;
    }

    location /static/ {
        alias /home/zjp/waterlawblog/static/;
        gzip on;
        gzip_types text/plain application/xml text/javascript application/javascript text/css;
    }
}
```

可能是没有考虑负载均衡，下面介绍下 Nginx 的基本作用：

### 1. 静态 HTTP 服务器

Nginx 是一个 HTTP 服务器，可以将服务器上的静态文件（如HTML、图片）通过HTTP协议展现给客户端。

```nginx
server {  
    listen 80; # 端口号  
    location / {  
        root /usr/share/nginx/html; # 静态文件路径  
    }  
} 
```

### 2. 反向代理服务器

客户端本来可以直接通过 HTTP 协议访问某网站应用服务器，网站管理员可以在中间加上一个 Nginx，客户端请求 Nginx，Nginx 请求应用服务器，然后将结果返回给客户端，此时 Nginx 就是反向代理服务器。

```nginx
server {  
    listen 80;  
    location / {  
        proxy_pass http://192.168.20.1:8080; # 应用服务器HTTP地址  
    }  
} 
```

### 3. 负载均衡

当网站访问量非常大，网站站长开心赚钱的同时，也摊上事儿了。因为网站越来越慢，一台服务器已经不够用了。于是将同一个应用部署在多台服务器上，将大量用户的请求分配给多台机器处理。同时带来的好处是，其中一台服务器万一挂了，只要还有其他服务器正常运行，就不会影响用户使用。

```nginx
upstream myapp {  
    server 192.168.20.1:8080; # 应用服务器1  
    server 192.168.20.2:8080; # 应用服务器2  
}  
server {  
    listen 80;  
    location / {  
        proxy_pass http://myapp;  
    }  
} 
```

以上配置会将请求轮询分配到应用服务器，也就是一个客户端的多次请求，有可能会由多台不同的服务器处理。

可以通过 ip-hash 的方式，根据客户端ip地址的 hash 值将请求分配给固定的某一个服务器处理。

```nginx
upstream myapp {  
    ip_hash; # 根据客户端IP地址Hash值将请求分配给固定的一个服务器处理  
    server 192.168.20.1:8080;  
    server 192.168.20.2:8080;  
}  
server {  
    listen 80;  
    location / {  
        proxy_pass http://myapp;  
    }  
}  
```

另外，服务器的硬件配置可能有好有差，想把大部分请求分配给好的服务器，把少量请求分配给差的服务器，可以通过 weight 来控制。

```nginx
upstream myapp {  
    server 192.168.20.1:8080 weight=3; # 该服务器处理 3/4 请求  
    server 192.168.20.2:8080; # weight默认为1，该服务器处理 1/4 请求  
}  
server {  
    listen 80;  
    location / {  
        proxy_pass http://myapp;  
    }  
} 
```

### 4、虚拟主机

有的网站访问量大，需要负载均衡。然而并不是所有网站都如此出色，有的网站，由于访问量太小，需要节省成本，将多个网站部署在同一台服务器上。

例如将 www.aaa.com 和 www.bbb.com 两个网站部署在同一台服务器上，两个域名解析到同一个IP地址，但是用户通过两个域名却可以打开两个完全不同的网站，互相不影响，就像访问两个服务器一样，所以叫两个虚拟主机。

```nginx
server {  
    listen 80 default_server;  
    server_name _;  
    return 444; # 过滤其他域名的请求，返回444状态码  
}  
server {  
    listen 80;  
    server_name www.aaa.com; # www.aaa.com域名  
    location / {  
        proxy_pass http://localhost:8080; # 对应端口号8080  
    }  
}  
server {  
    listen 80;  
    server_name www.bbb.com; # www.bbb.com域名  
    location / {  
        proxy_pass http://localhost:8081; # 对应端口号8081  
    }  
}
```

在服务器 8080 和 8081 分别开了一个应用，客户端通过不同的域名访问，根据 server_name 可以反向代理到对应的应用服务器。

虚拟主机的原理是通过 HTTP 请求头中的 Host 是否匹配 server_name 来实现的，有兴趣的同学可以研究一下HTTP协议。

### 5、FastCGI

Nginx 本身不支持PHP等语言，但是它可以通过 FastCGI 来将请求扔给某些语言或框架处理（例如PHP、Python、Perl）。

```nginx
server {  
    listen 80;  
    location ~ \.php$ {  
        include fastcgi_params;  
        fastcgi_param SCRIPT_FILENAME /PHP文件路径$fastcgi_script_name; # PHP文件路径  
        fastcgi_pass 127.0.0.1:9000; # PHP-FPM地址和端口号  
        # 另一种方式：fastcgi_pass unix:/var/run/php5-fpm.sock;  
    }  
} 
```

配置中将.php 结尾的请求通过FashCGI交给PHP-FPM处理，PHP-FPM 是 PHP 的一个 FastCGI 管理器。