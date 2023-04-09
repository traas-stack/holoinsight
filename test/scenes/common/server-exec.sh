#!/usr/bin/env bash
set -e

# docs: Run this script to exec into holoinsight-server container.

source `dirname $0`/../common/setup-env.sh

server_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q server) | cut -c2-`

docker exec -w /home/admin/logs/holoinsight-server -it $server_container_name bash
