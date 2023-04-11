#!/usr/bin/env bash
set -e

cd `dirname $0`
project_root=`realpath ../../..`

agent_dirs=(bin data logs conf)
for i in "${agent_dirs[@]}"; do
  docker exec $target sudo mkdir -p /usr/local/holoinsight/agent/$i
done

docker exec -u root $target /agent_entrypoint.sh
