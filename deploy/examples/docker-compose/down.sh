#!/usr/bin/env bash
set -e

script_dir=`dirname $0 | xargs realpath`
cd $script_dir

env_file=".env"
if [ "$mirror" = "cn" ]; then
  echo use cn mirror
  env_file=".env-cn"
fi

docker-compose --env-file $env_file down
