#!/usr/bin/env bash
set -e

# docs: Run this script to exec into mysql container.

source `dirname $0`/../common/setup-env.sh

mysql_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q mysql) | cut -c2-`

docker exec -it $mysql_container_name mysql -uholoinsight -pholoinsight -Dholoinsight
