#!/usr/bin/env bash
set -e

cd `dirname $0`
script_dir=`pwd`

env_file=".env"
if [ "$mirror" = "cn" ]; then
  echo use cn mirror
  env_file=".env-cn"
fi

$script_dir/docker-compose.sh --env-file $env_file down
