#!/usr/bin/env bash
set -e

echo [agent] install agent to example-server-1

docker exec example-server-1 sudo mkdir -p /usr/local/holoinsight/agent/{bin,data,logs,conf}
docker cp example-agent-image-1:/usr/local/holoinsight/agent/bin/agent /tmp/agent

docker cp /tmp/agent example-server-1:/usr/local/holoinsight/agent/bin/agent
docker cp ./agent.yaml example-server-1:/usr/local/holoinsight/agent/
docker cp ./agent.ini example-server-1:/etc/supervisord.d/agent.ini

docker exec example-server-1 bash -c 'sc reread && sc add agent'
