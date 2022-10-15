Docker 容器下的日志过大

[官方文档 json-file](https://docs.docker.com/config/containers/logging/json-file/)

容器运行的时候加上参数

```bash
docker run --log-driver json-file --log-opt max-size=10m alpine echo hello world
```
