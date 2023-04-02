#!/usr/bin/env bash
set -e

source `dirname $0`/../common/setup-env.sh

ip=`hostname -I | awk '{print $1}'`

echo
docker-compose ps

echo
echo Visit server http://$ip:`docker-compose port server 80 | awk -F: '{print $2}'`
echo Exec server using ./server-exec.sh
echo

echo Visit mysql at $ip:`docker-compose port mysql 3306 | awk -F: '{print $2}'`
echo Exec mysql using ./mysql-exec.sh
echo

if docker-compose ps | grep kibana >/dev/null; then
  echo Visit kibana at $ip:`docker-compose port kibana 5601 | awk -F: '{print $2}'`
fi
