#!/usr/bin/env bash
set -e

cd `dirname $0`

if [ -z "$COMPOSE_PROJECT_NAME" ]; then
  source `dirname $0`/../common/setup-env.sh
fi

server_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q server) | cut -c2-`
agent_container_name=`docker inspect -f '{{.Name}}' $(docker-compose ps -q agent-image) | cut -c2-`

echo [agent] install agent to $server_container_name

agent_dirs=(bin data logs conf)
for i in "${agent_dirs[@]}"; do
  docker exec $server_container_name sudo mkdir -p /usr/local/holoinsight/agent/$i
done

temp=`mktemp`

if docker cp --help | grep quiet >/dev/null 2>&1; then
  # when docker >= 23, docker cp print many logs, use quiet mode
  docker_cp_opts='-q'
fi


docker cp $docker_cp_opts $agent_container_name:/usr/local/holoinsight/agent/bin/agent $temp

docker cp $docker_cp_opts $temp $server_container_name:/usr/local/holoinsight/agent/bin/agent
docker cp $docker_cp_opts ../common/agent.yaml $server_container_name:/usr/local/holoinsight/agent/
docker cp $docker_cp_opts ../common/agent.ini $server_container_name:/etc/supervisord.d/agent.ini

docker exec $server_container_name bash -c 'sc reread && sleep 1 && sc add agent'

echo copy log-generator.py to $server_container_name
docker cp $docker_cp_opts ../common/log-generator.py $server_container_name:/home/admin/logs/holoinsight-server/log-generator.py
docker exec -w /home/admin/logs/holoinsight-server $server_container_name bash -c ' python log-generator.py & '

docker cp $docker_cp_opts ../common/log-alert-generator.py $server_container_name:/home/admin/logs/holoinsight-server/log-alert-generator.py
docker exec -w /home/admin/logs/holoinsight-server $server_container_name bash -c ' python log-alert-generator.py & '
