#!/usr/bin/env bash
set -e

cd `dirname $0`/../..
project_root=`pwd`

sh ./scripts/all-in-one/build.sh

buildx_bin=""

if docker buildx >/dev/null 2>&1; then
  buildx_bin='docker buildx'
else
  tochecks=("$HOME/.docker/cli-plugins" /usr/local/lib/docker/cli-plugins /usr/local/libexec/docker/cli-plugins /usr/lib/docker/cli-plugins /usr/libexec/docker/cli-plugins)

  for tocheck in ${tochecks[@]}; do
    if [ -e "$tocheck/buildx" ]; then
      buildx_bin="$tocheck/buildx"
      break
    fi
  done
fi

if [ -z "$buildx_bin" ]; then
  echo Please install buildx before running this script.
  echo Check https://docs.docker.com/build/install-buildx/
  exit 1
fi

echo use $buildx_bin

# TODO update tag
tag="temp"

$buildx_bin build --platform linux/amd64 -t holoinsight/server:$tag-amd64-linux -f ./scripts/docker/Dockerfile .
$buildx_bin build --platform linux/arm64/v8 -t holoinsight/server:$tag-arm64v8-linux -f ./scripts/docker/Dockerfile .


# You need to change the tag and push these images to Docker Hub manually
# docker push holoinsight/server:$tag-amd64-linux
# docker push holoinsight/server:$tag-arm64v8-linux

# And then create a manifest
# See https://docs.docker.com/engine/reference/commandline/manifest/
# docker manifest create holoinsight/server:$tag\
#   -a holoinsight/server:$tag-amd64-linux \
#   -a holoinsight/server:$tag-arm64v8-linux
#
# docker manifest push holoinsight/server:$tag
