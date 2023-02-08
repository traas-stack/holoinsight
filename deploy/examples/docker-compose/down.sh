#!/usr/bin/env bash
set -e

script_dir=`dirname $0`
cd $script_dir

env_file="$script_dir/.env"
if [ "$mirror" = "cn" ]; then
  echo use cn mirror
fi

echo use env file $env_file

docker-compose --env-file $env_file down
