#!/usr/bin/env bash
set -e

script_dir=`dirname $0`
cd $script_dir

env_file="$script_dir/.env"
if [ "$1" = "cn" ]; then
  echo use cn mirror
  env_file="$script_dir/.env-cn"
fi

echo use env file $env_file
cat $env_file

docker-compose --env-file $env_file up -d

echo holoinsight bootstrap successfully, please visit http://YOUR_HOST_IP:8080
echo

sh $script_dir/install-agent.sh
