#!/usr/bin/env bash
set -e

# docs: Run this script to start holoinsight-enterprise server with its dependencies
# options:
# - build=1 : build image before start
# - debug=1 : start server with debug options

source `dirname $0`/../common/setup-env.sh

./down.sh

if [ "$build"x = "1"x ]; then
  $project_root/scripts/docker/build.sh
fi

if [ "$debug"x = "1"x ]; then
  export JAVA_DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
  echo 'debug enabled'
fi

docker-compose up -d

ip=`hostname -I | awk '{print $1}'`

echo Visit server http://$ip:`docker-compose port server 80 | awk -F: '{print $2}'`
echo Exec server using ./server-exec.sh
if [ "$debug"x = "1"x ]; then
  echo Debug server at $ip:`docker-compose port server 8000 | awk -F: '{print $2}'`
fi
echo Visit mysql at $ip:`docker-compose port mysql 3306 | awk -F: '{print $2}'`
echo Exec mysql using ./mysql-exec.sh
echo

if [ -x "$script_dir/after.sh" ]; then
  $script_dir/after.sh
fi
