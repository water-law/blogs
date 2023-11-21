### 下载 jenkins

[jenkins](http://mirrors.jenkins.io)

### 运行 war 包

```bash
java -jar jenkins.war --httpPort=8080
```



启动后日志输出：

```bash
080bb42338014cc3ba01dc886fe3396f
This may also be found at: /Users/zjp/.jenkins/secrets/initialAdminPassword
```



### Docker

```bash
docker run -it --platform linux/amd64  --name jenkins f4b4fccc65bb /bin/bash
```



jenkins.war

默认端口 8080

```bash
wget https://get.jenkins.io/war-stable/2.164.1/jenkins.war --no-check-certificate
```

```bash
https://updates.jenkins.io/update-center.json
```

```
https://mirrors.tuna.tsinghua.edu.cn/jenkins/updates/update-center.json
```

