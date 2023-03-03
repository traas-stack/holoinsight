<p align="center">
  <img height="300" src="https://github.com/traas-stack/holoinsight/blob/main/docs/logo/logo-flat.png" alt="HoloInsight">
</p>

![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)
[![Github stars](https://img.shields.io/github/stars/traas-stack/holoinsight?style=flat-square])](https://github.com/traas-stack/holoinsight)
[![OpenIssue](https://img.shields.io/github/issues/traas-stack/holoinsight)](https://github.com/traas-stack/holoinsight/issues)

[English](./README.md)  

HoloInsight 集数据采集、洞察分析、智能运维和一站式技术风险解决方案为一体，帮您清晰观测分布式系统中的应用表现，高效应对云原生时代的技术挑战。


# 文档
HoloInsight 是一个云原生可观察性平台，特别专注于实时日志分析和 AI 集成。您可以查看以下文档以获取更多信息。
* [Holoinsight 文档](https://github.com/traas-stack/holoinsight-docs)

# 快速入门

### 使用 docker-compose 部署
先决条件:
1. 已安装 [docker](https://docs.docker.com/engine/install/) & [docker-compose](https://docs.docker.com/compose/install/other/)(**>=v1.29 || >=v2**)
2. 有 Linux 或 Mac 环境

检查 `docker compose` 的版本：
```bash
# V1
docker-compose version

# V2
docker compose version
```

> 可以参考附录里的 [安装-docker-compose](#安装-docker-compose).  

1. 克隆仓库
```bash
git clone https://github.com/traas-stack/holoinsight.git --depth 1 
```

2. 运行部署脚本
```bash
sh ./deploy/examples/docker-compose/up.sh
 
# 中国的用户可以使用如下的脚本获得更好的网络访问
# sh ./deploy/examples/docker-compose/up.sh cn
``` 
这个脚本会顺便部署一个 holoinsight-agent 到 holoinsight-server 容器中，这种用法仅仅用于演示，不适用于生产环境。

3. 访问 Holoinsight  
   访问 http://localhost:8080  
   产品的使用方法可以参考[这个文档](https://github.com/traas-stack/holoinsight-docs/blob/main/docs/src/cn/product/quick-start.md)。

### 使用 k8s 部署
先决条件:
1. 有 k8 集群
2. 有 Linux 或 Mac 环境
<br/>

1. 克隆仓库
```bash
git clone https://github.com/traas-stack/holoinsight.git --depth 1 
```

2. 部署 k8s 资源
```bash
sh ./deploy/examples/k8s/overlays/example/apply.sh

# 中国的用户可以使用如下的脚本获得更好的网络访问。
# sh ./deploy/examples/k8s/overlays/example-cn/apply.sh 
```
> Notice: 你的 k8s 用户必须有权限创建 ClusterRole。

使用如下脚本将 Holoinsight 从 k8s 集群中卸载。
```bash
# sh ./deploy/examples/k8s/overlays/example/delete.sh
```

3. 访问 Holoinsight  
   访问 http://localhost:8080  
   产品的使用方法可以参考[这个文档](https://github.com/traas-stack/holoinsight-docs/blob/main/docs/src/cn/product/quick-start.md)。
   
### 附录
#### 安装 docker-compose
快速安装 docker-compose V2:
```bash
sudo curl -SL https://github.com/docker/compose/releases/download/v2.15.1/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose && sudo chmod a+x /usr/local/bin/docker-compose
```

中国的用户可以使用如下的脚本获得更好的网络访问。
```bash
docker run --name holoinsight-tools -d registry.cn-hangzhou.aliyuncs.com/holoinsight-examples/tools:latest && \
  docker cp holoinsight-tools:/docker-compose . && \
  docker rm -f holoinsight-tools
```

# 开源许可
HoloInsight 基于 [Apache License 2.0](https://github.com/traas-stack/holoinsight/blob/main/LICENSE) 协议。

# 社区与支持
- 邮箱地址: traas_stack@antgroup.com
- 微信群 [二维码](https://github.com/traas-stack/community/blob/main/holoinsight/groups/wechat-qrcode.jpg)
- 钉钉群 [二维码](https://github.com/traas-stack/community/blob/main/holoinsight/groups/dingtalk-qrcode.jpg)
