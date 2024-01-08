# Requirements
1. `docker`
2. `docker-compose` == v1.29.2
3. Be proficient in using `docker-compose` if you need to customize the test scenario.

# Principles of E2E Integration Testing
We have build a default `docker-compose` scene named `scene-default`. It contains mysql:8 + cersedb:v0.3.1 + server + agent(running inside the server as a sidecar process).

When you deploy this `docker-compose` scene, it will start `holoinsight-server` and map its 80 port to a local random port.  
Then we can run our E2E Integration Tests. They use [rest-assure](https://github.com/rest-assured/rest-assured) to test and validate our webapi.

> We set `RestAssured.baseURI="http://localhost:${generated local random port}"` in `beforeAll` method. It is the base URI that's used by REST assured when making requests if a non-fully qualified URI is used in the request.  
> Check `io.holoinsight.server.test.it.BaseIT.setupBaseURI`

If you need to create a new test scene, refer to other chapters of this article.

# How to write an E2E integration test
The name format of integration test is `{prefix}IT`. You can refer to the `AgentVMIT` class. You need to put it under `io.holoinsight.server.test.it` package or its subpackage.

If you want to include the test in Github Action. Add your test to `e2e-test` step in `.github/workflows/e2e-test.yml`.      
If your test class name is `io.holoinsight.server.test.it.foo.BarTest` then use test name `foo.BarTest` in e2e-test.yml.

If you want to include the test when 'running all E2E integration tests locally'. Add your test to `io.holoinsight.server.test.it.bootstrap.SceneDefaultIT` class.

# How to run E2E integration tests

### precondition
The default images of server and agent used by docker-compose are `holoinsight/server:latest` and `holoinsight/agent:latest`.    
Docker will pull images from docker hub if it is unable to find images locally.    
If you need to test local code. You need to build the image first based on the local code.  

Run:
```bash
./scripts/docker/build.sh
```

### How to run all E2E integration tests locally
```bash
./scripts/test/e2e/all.sh
```
At present, all integration tests will be executed serially, and some use cases are time-consuming, so the execution will be very slow, and the optimization will be continued later.

### How to run all E2E integration tests in Github Actions
We use the matrix ability of Github Action. It runs all E2E integration tests concurrently.
For details, please refer to `.github/workflows/e2e-test.yml`.


### How to run an E2E integration test locally

```bash
# Build image based on local code
./scripts/docker/build.sh

# Run one integration test
./scripts/test/e2e/one.sh AgentVMIT
```

### How to override envs passed to docker-compose
All envs starting with `COMPOSE_` will be trimmed prefix `COMPOSE_` and then are passed to `docker-compose`.

If you want to change the agent image, you can refer to the following script: 
```bash
COMPOSE_agent_image=holoinsight/agent:OTHER_TAG ./scripts/test/e2e/one.sh AgentVMIT
```
`agent_iamge` env is used in `test/scenes/scene-default/docker-compose.yaml`.

### How to perform an E2E integration test against an existing environment
Suppose we have deployed `HoloInsight` to `http://demo-server:8080` and want to run an E2E integration test against it.

Edit `~/.holoinsight-IT.properties`:
```properties
baseURI=http://demo-server:8080
```

After editing, you can run E2E ITs (e.g. AgentVMIT) directly through your IDE.

# How to create a new test scene
You can refer to the existing `scene-default`.  
Create a directory under `test/scenes/` directory. The directory name is the scene name, please try to use a meaningful name.  

Under the directory:
```text
after.sh : If the script exists and is executable, it will be executed after `docker-compose.yaml` is successfully deployed.
docker-compose.yaml : docker-compose config file
```
If `docker-compose` needs to use files with the same content, please try to reuse the same file reference instead of copying it again.  
For details, please refer to `io.holoinsight.server.test.it.env.DockerComposeEnv`.
