#!/usr/bin/env bash
set -e

docker_compose_bin=

if docker compose >/dev/null 2>&1; then
  docker_compose_bin='docker compose'
elif which docker-compose >/dev/null 2>&1; then
  docker_compose_bin='docker-compose'
fi

if [ -z "$docker_compose_bin" ]; then
  echo 'docker compose is required'
  echo 'See https://docs.docker.com/compose/install/'
  exit 1
fi

$docker_compose_bin "$@"
