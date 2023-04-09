#!/usr/bin/env bash
set -e

# docs: Run this script to build holoinsight server app jar, copy app jar to server container, restart server process

source `dirname $0`/../common/setup-env.sh

$project_root/scripts/all-in-one/build.sh

server_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q server) | cut -c2-`

echo stop holoinsight server ...
docker exec $server_container_name sc stop app

echo copy holoinsight server ...
docker cp $docker_cp_opts $project_root/server/all-in-one/all-in-one-bootstrap/target/holoinsight-server.jar $server_container_name:/home/admin/app.jar >/dev/null

echo start holoinsight server ...
docker exec $server_container_name sc start app
