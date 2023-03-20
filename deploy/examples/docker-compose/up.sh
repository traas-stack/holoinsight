#!/usr/bin/env bash
set -e

# doc: Run this script to deploy HoloInsight quick start demo using docker-compose
# usage: up.sh
# usage: build=1 up.sh

cd `dirname $0`
script_dir=`pwd`

if [ "$build"x = "1"x ]; then
  $script_dir/../../../scripts/docker/build.sh
  export server_image_pull_policy=never
fi

env_file="$script_dir/.env"

$script_dir/docker-compose.sh --env-file $env_file up -d

real_port=`docker-compose port server 80 | awk -F : '{print $2}'`

echo holoinsight bootstrap successfully, please visit http://localhost:${real_port}
echo

sh $script_dir/install-agent.sh
