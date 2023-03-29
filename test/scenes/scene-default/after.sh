#!/usr/bin/env bash
set -e

cd `dirname $0`

server_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q server) | cut -c2-`
agent_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q agent-image) | cut -c2-`

echo [agent] install agent to $server_container_name

docker exec $server_container_name sudo mkdir -p /usr/local/holoinsight/agent/bin
docker exec $server_container_name sudo mkdir -p /usr/local/holoinsight/agent/data
docker exec $server_container_name sudo mkdir -p /usr/local/holoinsight/agent/logs
docker exec $server_container_name sudo mkdir -p /usr/local/holoinsight/agent/conf

temp=`mktemp`

docker cp $agent_container_name:/usr/local/holoinsight/agent/bin/agent $temp

docker cp $temp $server_container_name:/usr/local/holoinsight/agent/bin/agent
docker cp ../common/agent.yaml $server_container_name:/usr/local/holoinsight/agent/
docker cp ../common/agent.ini $server_container_name:/etc/supervisord.d/agent.ini

docker exec $server_container_name bash -c 'sc reread && sleep 1 && sc add agent'

echo copy log-generator.py to $server_container_name
docker cp ../common/log-generator.py $server_container_name:/home/admin/logs/holoinsight-server/log-generator.py
docker exec -w /home/admin/logs/holoinsight-server $server_container_name bash -c ' python log-generator.py & '

docker cp ../common/log-alert-generator.py $server_container_name:/home/admin/logs/holoinsight-server/log-alert-generator.py
docker exec -w /home/admin/logs/holoinsight-server $server_container_name bash -c ' python log-alert-generator.py & '
