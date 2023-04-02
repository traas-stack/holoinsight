#!/usr/bin/env bash
set -e

temp=`mktemp`

agent_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q agent-image) | cut -c2-`

docker cp $agent_container_name:/usr/local/holoinsight/agent/bin/agent $temp >/dev/null

echo $temp
