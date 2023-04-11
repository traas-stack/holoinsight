#!/usr/bin/env bash
set -e

# Don't use source here
#source `dirname $0`/../common/setup-env.sh

cd `dirname $0`
script_dir=`pwd`
project_root=`realpath ../../..`

if [ -z "$COMPOSE_PROJECT_NAME" ]; then
  source ../common/setup-env.sh
fi

server_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q server) | cut -c2-`

echo [agent] install agent to server
target=$server_container_name ../common/copy-agent.sh

echo copy log-generator.py to $server_container_name
docker-compose exec -w /home/admin/logs/holoinsight-server -d -T server bash -c ' python /home/admin/test/log-generator.py & '

echo copy log-alert-generator.py to $server_container_name
docker-compose exec -w /home/admin/logs/holoinsight-server -d -T server bash -c ' python /home/admin/test/log-alert-generator.py & '
