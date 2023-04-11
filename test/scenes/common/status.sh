#!/usr/bin/env bash
set -e

source `dirname $0`/../common/setup-env.sh

ip=`hostname -I | awk '{print $1}'`

echo

ps=`docker-compose ps`
echo "$ps"

echo
echo Visit server at http://$ip:`get_port server 80`
echo Debug server at $ip:`get_port server 8000` "(if debug mode is enabled)"
echo Exec server using ./server-exec.sh
echo Exec server using Web UI http://$ip:`get_port server 7681` "(if debug mode is enabled)"
echo

echo Visit MySQL at $ip:`get_port mysql 3306`
echo Exec MySQL using ./mysql-exec.sh
if echo "$ps" | grep phpmyadmin >/dev/null; then
  echo Visit MySQL Web UI at http://$ip:`get_port phpmyadmin 80`"?db=holoinsight"
fi
echo

if echo "$ps" | grep mongo-express >/dev/null; then
  echo Visit Mongo Web UI at http://$ip:`get_port mongo-express 8081`/db/holoinsight/
  echo
fi

if echo "$ps" | grep kibana >/dev/null; then
  echo Visit Kibana at http://$ip:`get_port kibana 5601`
  echo
fi

if echo "$ps" | grep grafana >/dev/null; then
  echo Visit Grafana at http://$ip:`get_port grafana 3000`
  echo
fi
