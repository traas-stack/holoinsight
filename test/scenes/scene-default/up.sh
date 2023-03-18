#!/usr/bin/env bash
set -e

# doc: Run this script to deploy HoloInsight quick start demo using docker-compose
# usage: up.sh
# usage: build=1 up.sh

cd `dirname $0`
script_dir=`pwd`

./down.sh

if [ -n "$HOLOINSIGHT_DEV" ]; then
  tag="dev-$HOLOINSIGHT_DEV-`basename $script_dir`"
  export COMPOSE_PROJECT_NAME="$tag"
  echo COMPOSE_PROJECT_NAME=$COMPOSE_PROJECT_NAME
  export server_image=holoinsight/server:$tag
  if [ "$build"x = "1"x ]; then
      holoinsight_server_tag=$tag $script_dir/../../../scripts/docker/build.sh
  fi
else
  if [ "$build"x = "1"x ]; then
    $script_dir/../../../scripts/docker/build.sh
  fi
fi

docker-compose up -d

ip=`hostname -I | awk '{print $1}'`

echo holoinsight bootstrap successfully, please visit http://$ip:`docker-compose port server 80 | awk -F: '{print $2}'`
echo visit mysql at $ip:`docker-compose port mysql 3306 | awk -F: '{print $2}'`
echo

if [ -x "$script_dir/after.sh" ]; then
  $script_dir/after.sh
fi
