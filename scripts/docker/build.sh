#!/usr/bin/env bash
set -e

script_dir=`dirname $0`
project_root=`realpath $script_dir/../..`

cd $project_root

sh ./scripts/all-in-one/build.sh

docker build --network host -t holoinsight/server:latest -f ./scripts/docker/Dockerfile .
