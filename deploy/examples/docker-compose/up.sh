#!/usr/bin/env bash
set -e

script_dir=`dirname $0 | xargs realpath`
cd $script_dir

env_file="$script_dir/.env"
if [ "$1" = "cn" ]; then
  echo use cn mirror
  env_file="$script_dir/.env-cn"
fi

# For M1 mac
if [ `uname` = "Darwin" ] && [ `uname -m` = "arm64" ]; then
  if [ "$1" = "cn" ]; then
    export server_image=registry.cn-hangzhou.aliyuncs.com/holoinsight-examples/server:latest-arm64v8
    export agent_image=registry.cn-hangzhou.aliyuncs.com/holoinsight-examples/agent:latest-arm64v8
    export mysql_image=registry.cn-hangzhou.aliyuncs.com/holoinsight-examples/mysql:8-arm64v8
    export mongo_image=registry.cn-hangzhou.aliyuncs.com/holoinsight-examples/mongo:5-arm64v8
  else
    export server_image=holoinsight/server:latest-arm64v8
    export agent_image=holoinsight/agent:latest-arm64v8
  fi
fi

docker-compose --env-file $env_file up -d

echo holoinsight bootstrap successfully, please visit http://localhost:8080
echo

sh $script_dir/install-agent.sh
