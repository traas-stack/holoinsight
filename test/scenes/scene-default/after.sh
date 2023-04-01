#!/usr/bin/env bash
set -e -x

# Don't use source here
#source `dirname $0`/../common/setup-env.sh

cd `dirname $0`
script_dir=`pwd`
project_root=`realpath ../../..`

if [ -z "$COMPOSE_PROJECT_NAME" ]; then
  source ../common/setup-env.sh
fi

server_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q server) | cut -c2-`

temp_agent_path=`../common/copy-agent-0.sh`
echo $temp_agent_path

echo [agent] install agent to server
agent_path=$temp_agent_path target=$server_container_name ../common/copy-agent.sh

echo copy log-generator.py to $server_container_name
docker cp $project_root/test/scenes/common/log-generator.py $server_container_name:/home/admin/logs/holoinsight-server/log-generator.py >/dev/null
docker exec -w /home/admin/logs/holoinsight-server $server_container_name bash -c ' python log-generator.py & '

echo copy log-alert-generator.py to $server_container_name
docker cp $project_root/test/scenes/common/log-alert-generator.py $server_container_name:/home/admin/logs/holoinsight-server/log-alert-generator.py >/dev/null
docker exec -w /home/admin/logs/holoinsight-server $server_container_name bash -c ' python log-alert-generator.py & '
