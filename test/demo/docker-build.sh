#!/usr/bin/env bash
set -e

cd `dirname $0`

./build.sh

tag="1.0.0"

docker buildx build --platform linux/amd64,linux/arm64/v8 --push -t holoinsight/demo-client:$tag -f ./demo-client/Dockerfile ./demo-client

docker buildx build --platform linux/amd64,linux/arm64/v8 --push -t holoinsight/demo-server:$tag -f ./demo-server/Dockerfile ./demo-server
