#!/usr/bin/env bash
set -e

source `dirname $0`/../common/setup-env.sh

ip=`hostname -I | awk '{print $1}'`

echo

ps=`docker-compose ps`
echo "$ps"

echo

source="  Component\t  Access
  Server_UI\t  http://$ip:`get_port server 80`
  Server_JVM_Debugger\t  $ip:`get_port server 8000`
  Server_exec\t  ./server-exec.sh
  Server_Web_Shell\t  http://$ip:`get_port server 7681`
  Collector_SkyWalking\t  $ip:`get_port collector 11800`
  Collector_DataDog\t  $ip:`get_port collector 5001`
  MySQL\t  $ip:`get_port mysql 3306`
  MySQL_exec\t  ./mysql-exec.sh"

if echo "$ps" | grep phpmyadmin >/dev/null; then
  source="$source
  MySQL_Web_UI\t  http://$ip:`get_port phpmyadmin 80`?db=holoinsight"
fi

if echo "$ps" | grep kibana >/dev/null; then
  source="$source
  Kibana_Web_UI\t  http://$ip:`get_port kibana 5601`"
fi

if echo "$ps" | grep grafana >/dev/null; then
  source="$source
  Grafana_Web_UI\t  http://$ip:`get_port grafana 3000`"
fi

#echo "$source" | column -t | sed '1{p;s/./-/g}'
#echo
echo "$source" | ../common/utils/prettytable/prettytable.sh 2

