
# usage: source path_to/setup-env.sh
# You must use the keyword `source` instead of executing this script.

cd `dirname $0`
script_dir=`pwd`
project_root=`realpath ../../..`

if [ -z "$COMPOSE_PROJECT_NAME" ] && [ -n "$HOLOINSIGHT_DEV" ]; then
  tag="dev-$HOLOINSIGHT_DEV-`basename $script_dir`"
  export COMPOSE_PROJECT_NAME="$tag"
  echo COMPOSE_PROJECT_NAME=$COMPOSE_PROJECT_NAME
  export server_image=holoinsight/server:$tag
  echo server_image=$server_image
  export holoinsight_server_tag=$tag
  if [ -z "$COMPOSE_FILE" ]; then
    export COMPOSE_FILE=$script_dir/docker-compose.yaml
  fi
fi

source ../common/utils.sh
