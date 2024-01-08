# 要求
1. `docker` 不太旧的版本，最近 3 年内的版本都 OK
2. `docker-compose` == v1.29.2
3. 如果需要自定义测试场景的话，需要熟练使用 `docker-compose`

# E2E 集成测试原理
我们预先准备了一个名为 `scene-default` 的 `docker-compose` 部署场景，它配有 mysql:8 + cersedb:v0.3.1 + server + agent（跑在 server 里）。

基于 `docker-compose` 启动该部署场景，它会将 `holoinsight-server` 的 80 端口映射到本地某个随机端口，这样场景就部署好了。然后再执行集成测试用例，它会访问刚部署的 server 的 webapi，并且对结果做断言。
> 我们在测试用例的 `beforeAll` 方法里设置 `rest-assured` 默认访问的 baseURL 为 `http://localhost:${随机生成的端口}`，这样可以在后续的 webapi 访问中免去指定 host 部分。  
> 详情见 `io.holoinsight.server.test.it.BaseIT.setupBaseURI`


如果你想测试一个新的场景，比如: mysql:8 + influxdb:2 + server x 2 (组成集群) + 一个 demo 应用 + agent(跑在 demo 应用里)，那么你需要新编写一个 `docker-compose` 场景。
测试场景的编写方法参考本文其他章节。

# 如何编写 E2E 集成测试
命名格式是 `{Prefix}IT`，可以参考现有的例子（比如 `AgentVMIT`），将 Java 文件放在包 `io.holoinsight.server.test.it` 或其子包下。  
为了让执行流程识别新的类，需要打开 `.github/workflows/e2e-test.yml`，找到 test-e2e step，编辑 matrix，往 it 数组后追加一项你的类名称，如果你是放在子包（比如 foo 下），那么格式是 `foo.YourIT` 这样的名称。
如果你想在本地执行所有 E2E 测试，你还需要在 `io.holoinsight.server.test.it.bootstrap.SceneDefaultIT` 里引用你的类。

# 如何运行 E2E 集成测试

### 前提
运行 E2E 集成测试时，会从使用 `holoinsight/server:latest` 和 `holoinsight/agent:latest` 镜像，**如果本地没有则会从 Docker Hub 拉取**。
如果你想对本地的代码进行 E2E 集成测试，请使用如下的命令构建出 `holoinsight/server:latest` 镜像（不用 push 到远程，放在本地就行）：
```bash
./scripts/docker/build.sh
```

### 如何在本地运行所有 E2E 集成测试
```bash
# 构建镜像, 然后执行测试
build=1 ./scripts/test/e2e/all.sh

# 不用构建镜像, 直接执行测试
./scripts/test/e2e/all.sh
```
目前，所有集成测试会串行执行，并且有一些用例耗时较高，因此执行会很慢，后续再持续优化。


### 如何在 Github Actions 上运行所有 E2E 集成测试
参考 `.github/workflows/e2e-test.yml`，找到 test-e2e step，它利用了 matrix 能力，可以并发地运行足有集成测试，每个集成测试的运行环境是**完全独立**的。


### 如何在本地运行单个 E2E 集成测试

```bash
# 构建镜像, 然后执行测试
build=1 ./scripts/test/e2e/one.sh AgentVMIT

# 不用构建镜像, 直接执行测试
./scripts/test/e2e/one.sh AgentVMIT
```

### 如何覆盖传递给 docker-compose 的环境变量
所有以 `COMPOSE_` 开头的环境变量，会被裁剪掉 `COMPOSE_` 前缀然后传递给底层的 docker-compose，你可以通过这种方式来覆盖一些版本号。  
例如 `COMPOSE_server_image=xxx` 会传递 `server_image=xxx` 给 `docker-compose`。

```bash
COMPOSE_agent_image=holoinsight/agent:latest IT_SCENE=scene-default ./scripts/test/e2e/one.sh AgentVMIT
```
> 如果不指定 IT_SCENE 则默认值是 scene-default。
> 镜像的默认版本请参考对应 scene 目录下的 docker-compose.yaml。

### 如何对一个已有的环境执行单个 E2E 集成测试
> 这种方法会在编写 E2E 集成测试时用到。

假设我们已经将 `HoloInsight` 部署到 `http://demo-server:8080` 上，并且想对它进行集成测试。  

编辑 `~/.holoinsight-IT.properties`，修改内容为：
```properties
baseURI=http://demo-server:8080
```

使用 IDEA 直接运行对应的集成测试即可。


# 如何编写一个新的测试场景
在 `test/scenes/` 下建立一个目录，目录名即为你的场景名，请尽量保持名字有意义(比如 scene-influxdb)。

在目录下的目录结构有如下约定:
```text
after.sh 如果该脚本存在且可执行，会在 docker-compose 部署完毕之后执行该脚本。
docker-compose.yaml docker-compose 指令文件
```
如果 `docker-compose` 需要引用一个已经存在的文件，请尽量复用同一个文件，而不是重新复制一份。  
详情请参考 `io.holoinsight.server.test.it.env.DockerComposeEnv`。
