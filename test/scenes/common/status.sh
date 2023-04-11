#!/usr/bin/env bash
set -e

source `dirname $0`/../common/setup-env.sh

ip=`hostname -I | awk '{print $1}'`

echo

ps=`docker-compose ps`
echo "$ps"

echo
echo Visit server at http://$ip:`docker-compose port server 80 | awk -F: '{print $2}'`
echo Debug server at $ip:`docker-compose port server 8000 | awk -F: '{print $2}'` "(if debug mode is enabled)"
echo Exec server using ./server-exec.sh
echo

echo Visit MySQL at $ip:`docker-compose port mysql 3306 | awk -F: '{print $2}'`
echo Exec MySQL using ./mysql-exec.sh
if echo "$ps" | grep phpmyadmin >/dev/null; then
  echo Visit MySQL Web UI at http://$ip:`docker-compose port phpmyadmin 80 | awk -F: '{print $2}'`"?db=holoinsight"
fi
echo

if echo "$ps" | grep mongo-express >/dev/null; then
  echo Visit Mongo Web UI at http://$ip:`docker-compose port mongo-express 8081 | awk -F: '{print $2}'`/db/holoinsight/
fi
echo

if echo "$ps" | grep kibana >/dev/null; then
  echo Visit Kibana at http://$ip:`docker-compose port kibana 5601 | awk -F: '{print $2}'`
fi
echo
