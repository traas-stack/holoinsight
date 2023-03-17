#!/usr/bin/env bash
set -e

cd `dirname $0`

version=1.0.2

docker buildx build --platform linux/amd64,linux/arm64/v8 --pull --push -t holoinsight/server-base:$version .
