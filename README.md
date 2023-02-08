# holoinsight

# Quick Start

### Deploy using docker-compose
Requirements:
1. [docker](https://docs.docker.com/engine/install/) & [docker-compose](https://docs.docker.com/compose/install/other/) installed
2. Linux or Mac environment 


1. clone the repo
```bash
git clone https://github.com/traas-stack/holoinsight.git --depth 1 
```

2. run deploy script
```bash
sh ./deploy/examples/docker-compose/up.sh 
``` 
This script will also install an agent inside holoinsight-server container. This usage is only for demonstration and is not applicable to production environment.

3. visit holoinsight  
visit http://YOUR_HOST_IP:8080

### Deploy using k8s
Requirements:
1. k8 cluster
2. Linux or Mac environment


1. clone the repo
```bash
git clone https://github.com/traas-stack/holoinsight.git --depth 1 
```

2. deploy k8s resources
```bash
kubectl apply -k ./deploy/examples/k8s/overlays/example 
```
> Notice: Your k8s user must has the permission to create ClusterRole.

3. visit holoinsight  
   visit http://YOUR_HOST_IP:8080


# Licensing
Holoinsight is under Apache License 2.0.
