#!/usr/bin/env bash
set -e -x

prefix=example

echo [agent] install agent to $prefix-server-1

#docker exec $prefix-server-1 sudo mkdir -p /usr/local/holoinsight/agent/{bin,data,logs,conf}
docker exec $prefix-server-1 sudo mkdir -p /usr/local/holoinsight/agent/bin
docker exec $prefix-server-1 sudo mkdir -p /usr/local/holoinsight/agent/data
docker exec $prefix-server-1 sudo mkdir -p /usr/local/holoinsight/agent/logs
docker exec $prefix-server-1 sudo mkdir -p /usr/local/holoinsight/agent/conf

docker cp $prefix-agent-image-1:/usr/local/holoinsight/agent/bin/agent /tmp/agent

docker cp /tmp/agent $prefix-server-1:/usr/local/holoinsight/agent/bin/agent
docker cp ./agent.yaml $prefix-server-1:/usr/local/holoinsight/agent/
docker cp ./agent.ini $prefix-server-1:/etc/supervisord.d/agent.ini

docker exec $prefix-server-1 bash -c 'sc reread && sc add agent'
