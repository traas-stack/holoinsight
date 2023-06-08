#!/usr/bin/env bash
set -e

# docs: Build server docker image for multi arch.

cd `dirname $0`/../..
project_root=`pwd`

if [ -z "$NO_BUILD_APP" ]; then
  sh ./scripts/all-in-one/build.sh
fi

if [ -z "$tag" ]; then
  tag="latest"
fi

docker buildx build \
  --platform=linux/amd64,linux/arm64/v8 \
  --pull --push \
  -f ./scripts/docker/Dockerfile \
  -t holoinsight/server:$tag .
