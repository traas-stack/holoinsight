#!/usr/bin/env bash
set -e

cd `dirname $0`
script_dir=`pwd`

if [ -n "$HOLOINSIGHT_DEV" ]; then
  tag="dev-$HOLOINSIGHT_DEV-`basename $script_dir`"
  export COMPOSE_PROJECT_NAME="$tag"
  echo COMPOSE_PROJECT_NAME=$COMPOSE_PROJECT_NAME
fi

docker-compose down
