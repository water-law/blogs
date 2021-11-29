```shell
find . -name _maven.repositories | xargs rm -rf
```

```xmls
<mirror>
    <id>alimaven</id>
		<name>aliyun maven</name>
		<url>http://maven.aliyun.com/nexus/content/groups/public/</url>
		<mirrorOf>central</mirrorOf> 
</mirror>
```

老国结系统

基础：
hytfb-mps:
hytfb-component-common:
hytfb-app: hytfb-mps, hytfb-component-common

hybup-lcfs: 

其他：

hytfb-component: hytfb-app

hytfb-batch: hytfb-component

hybup-lg-web: hytfb-app

hybup-lcfs-web: hytfb-app

tfb-web: hytfb-app,hytfb-component,hybup-lg-web,hybup-lcfs-web



![image-20210630194058772](/Users/zjp/Projects/blogs/老国结/img/image-20210630194058772.png)

