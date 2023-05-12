#!/usr/bin/env bash
set -e

# docs: Build server docker image with options.

script_dir=`dirname $0`
project_root=`realpath $script_dir/../..`

cd $project_root

if [ "$nobuild"x != "1"x  ]; then
  ./scripts/all-in-one/build.sh
fi

tag=$holoinsight_server_tag
if [ -z "$tag" ]; then
  tag="latest"
fi

# build with current arch
docker build -t holoinsight/server:$tag -f ./scripts/docker/Dockerfile .
