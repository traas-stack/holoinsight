#!/usr/bin/env bash
set -e

# docs: Build server docker image with options.

script_dir=`dirname $0`
project_root=`realpath $script_dir/../..`

cd $project_root

sh ./scripts/all-in-one/build.sh

docker buildx build --load -t holoinsight/server -f ./scripts/docker/Dockerfile .
