#!/usr/bin/env bash
set -e

# docs: Build server docker image with options.

script_dir=`dirname $0`
project_root=`realpath $script_dir/../..`

cd $project_root

if [ -z "$NO_BUILD_APP" ]; then
  ./scripts/all-in-one/build.sh
fi

# build with current arch
docker buildx build --load -t holoinsight/server -f ./scripts/docker/Dockerfile .
