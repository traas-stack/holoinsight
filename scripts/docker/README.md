# Build Docker Image
```bash
sh ./scripts/docker/build.sh
```

# Push to Docker Hub
See [docker hub](https://hub.docker.com/repository/docker/holoinsight/server/general)

```bash
docker tag holoinsight/server:temp holoinsight/server:${VERSION}
docker push holoinsight/server:${VERSION}
```

# Run
Edit application.yaml
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/holoinsight?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&rewriteBatchedStatements=true&socketTimeout=15000&connectTimeout=3000&useTimezone=true&serverTimezone=Asia/Shanghai
    username: holoinsight
    password: holoinsight
  data:
    mongodb:
      uri: mongodb://holoinsight:holoinsight@127.0.0.1:27017/holoinsight?authSource=admin&keepAlive=true&maxIdleTimeMS=1500000&maxWaitTime=120000&connectTimeout=10000&socketTimeout=10000&socketKeepAlive=true&readPreference=primary&retryWrites=true

holoinsight:
  roles:
    active: meta,registry,gateway,query,home
  metric:
    storage:
      type: ceresdbx
```

```bash
docker run \
  --name holoinsight-server -d \
  -v $PWD/application.yaml:/home/admin/application.yaml \
  -p 80:80 \
  holoinsight/server:1.0.0-SNAPSHOT
```
