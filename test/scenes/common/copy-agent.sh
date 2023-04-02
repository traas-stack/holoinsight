#!/usr/bin/env bash
set -e

cd `dirname $0`
project_root=`realpath ../../..`

agent_dirs=(bin data logs conf)
for i in "${agent_dirs[@]}"; do
  docker exec $target sudo mkdir -p /usr/local/holoinsight/agent/$i
done

docker cp $agent_path $target:/usr/local/holoinsight/agent/bin/agent >/dev/null
docker cp $project_root/test/scenes/common/agent.yaml $target:/usr/local/holoinsight/agent/ >/dev/null
docker cp ./agent_entrypoint.sh $target:/agent_entrypoint.sh >/dev/null

docker exec -u root $target /agent_entrypoint.sh
