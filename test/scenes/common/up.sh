#!/usr/bin/env bash
set -e

# doc: Run this script to deploy HoloInsight quick start demo using docker-compose
# usage: build=1 debug=1 up.sh
# build=1: re build docker image before up
# debug=1: using debug mode, start Java with debug mode and start Web UI of MySQL and MongoDB as well

source `dirname $0`/../common/setup-env.sh

docker-compose down --remove-orphans --volumes --rmi local

if [ "$build"x = "1"x ]; then
  $project_root/scripts/docker/build.sh
fi

if [ "$debug"x = "1"x ]; then
  export JAVA_DEBUG_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
  echo 'debug enabled'
  if [ -z "$COMPOSE_PROFILES" ]; then
    export COMPOSE_PROFILES="debug"
  else
    export COMPOSE_PROFILES="$COMPOSE_PROFILES,debug"
  fi
fi

docker-compose up -d --build

if [ -x "$script_dir/after.sh" ]; then
  $script_dir/after.sh
fi

./status.sh
