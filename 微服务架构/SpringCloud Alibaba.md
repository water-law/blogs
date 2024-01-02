### Nacos 安装与使用

```sh
wget https://github.com/alibaba/nacos/releases/download/2.1.1/nacos-server-2.1.1.tar.gz
```

###  nacos 数据库

注意：Nacos 目前只支持MySQL数据库，请安装MySQL8.0版本，以免出现其他错误。

新建数据库nacos_config，并运行【conf/nacos-mysql.sql】文件，初始化数据库即可

###  修改 Nacos 的配置文件

```properties
# 填自己的ip地址，本地填127.0.0.1就行
nacos.inetutils.ip-address=127.0.0.1

spring.datasource.platform=mysql
db.num=1
#填自己的数据库连接和密码
db.url.0=jdbc:mysql://127.0.0.1:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
db.user.0=root
db.password.0=root
```

###  启动 Nacos

```sh
Windows：
startup.cmd -m standalone

Linux: 
sh startup.sh -m standalone
```

### Docker

https://nacos.io/zh-cn/docs/quick-start-docker.html

### mac-m1-docker 安装 nacos 异常

https://blog.csdn.net/yang_zzu/article/details/127421532

```
docker build -t nacos/nacos-server:v2.1.1 .
```

本地下载了一个 nacos2.1.1,  在 /Users/zjp/Projects/nacos 目录， 修改 /Users/zjp/Projects/nacos/conf/application.properties 中 mysql 连接 ip:port

```properties
host.docker.internal:3306
```



```sh
docker run -itd -e MODE=standalone --add-host host.docker.internal:host-gateway -p 8848:8848 -p 9848:9848 -m 2048m --memory-swap=2312m -v /Users/zjp/Projects/nacos/conf/application.properties:/home/nacos/conf/application.properties --name nacos nacos-server:v2.1.1
```

### 端口

7848：实现 Nacos 集群通信，一致性选举，心跳检测等功能

8848：Nacos 主端口，对外提供服务的Http端口

9848:   客户端 gRPC 请求服务端端口，用于客户端向服务端发起连接和请求，该端口的配置为：主端口（8848）+ 1000 偏移量

9849：服务端 gRPC 请求服务端端口，用于服务间同步等，该端口的配置为：主端口 + 1001偏移量



### Redis

```sh
docker run -d -p 6379:6379 --name redis redis
```



### ES 本地一键安装与启动

首次安装时，先使用docker创建一个供测试的虚拟网络，后续搭建的其他es节点或者组件（如kibana）都使用这个虚拟网络

```sh
docker network create learnnetwork
```

```sh
docker run --name es --net learnnetwork -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" arm64v8/elasticsearch:7.10.1
```

- 9300端口为ES集群间组件的通信接口
- 9200端口为浏览器访问的http协议的Restful接口：http://localhost:9200/



#### pom.xml

```xml
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>7.10.1</version>
        </dependency>

        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>7.10.1</version>
        </dependency>

        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>1.7.1</version>
        </dependency>
```



#### java

```java
//        CreateIndexRequest request = new CreateIndexRequest("user");
//        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
//        boolean isOk = response.isAcknowledged();
//        System.out.println(isOk);

//        GetIndexRequest request = new GetIndexRequest("user");
//        GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
//        System.out.println(response.getAliases());
//        System.out.println(response.getMappings());
//        System.out.println(response.getSettings());

//        IndexRequest request = new IndexRequest();
//        request.index("user").id("1001");
//
//        User user = new User();
//        user.setName("zjp");
//        user.setAge(30);
//        String userStr = gson.toJson(user);
//
//        request.source(userStr, XContentType.JSON);
//
//        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
//        System.out.println(response.getResult());

//        UpdateRequest request = new UpdateRequest();
//        request.index("user").id("1001");
//        request.doc(XContentType.JSON, "age", 28);
//
//        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
//        System.out.println(response.getGetResult());

//        GetRequest request = new GetRequest();
//        request.index("user").id("1001");
//
//        GetResponse documentFields = client.get(request, RequestOptions.DEFAULT);
//        System.out.println(documentFields.getSourceAsString());

//        BulkRequest bulk = new BulkRequest();
//
//        bulk.add(new IndexRequest().index("user").id("1001").source(XContentType.JSON, "name", "zjp"));
//        bulk.add(new IndexRequest().index("user").id("1002").source(XContentType.JSON, "name", "zjp1"));
//        bulk.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON, "name", "zjp2"));
//        bulk.add(new IndexRequest().index("user").id("1004").source(XContentType.JSON, "name", "zjp3"));
//        bulk.add(new IndexRequest().index("user").id("1005").source(XContentType.JSON, "name", "zjp4"));
//
//        BulkResponse response = client.bulk(bulk, RequestOptions.DEFAULT);
//        System.out.println(response.getTook());
//        System.out.println(response.getItems());

        SearchRequest request = new SearchRequest();
        request.indices("user");
//        request.source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));
//        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.termQuery("name", "zjp"));
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
//        builder.from(0);
//        builder.size(2);
        builder.sort(SortBuilders.fieldSort("name.keyword").order(SortOrder.DESC));
        request.source(builder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        System.out.println(hits.getTotalHits());
        System.out.println(response.getTook());
        for (SearchHit hit: hits) {
            System.out.println(hit.getSourceAsString());
        }

        client.close();
```

