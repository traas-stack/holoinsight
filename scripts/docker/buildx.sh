#!/usr/bin/env bash
set -e

cd `dirname $0`/../..
project_root=`pwd`

#sh ./scripts/all-in-one/build.sh

# TODO update tag
tag="temp"

bash ./scripts/docker/buildx-one.sh linux/amd64 $tag-amd64-linux
bash ./scripts/docker/buildx-one.sh linux/arm64/v8 $tag-arm64v8-linux

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
