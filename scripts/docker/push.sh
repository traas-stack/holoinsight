#!/usr/bin/env bash
set -e

tag="temp20230313"

docker push holoinsight/server:$tag-amd64-linux
docker push holoinsight/server:$tag-arm64v8-linux

docker manifest rm holoinsight/server:$tag || true

docker manifest create holoinsight/server:$tag\
 holoinsight/server:$tag-amd64-linux \
 holoinsight/server:$tag-arm64v8-linux

docker manifest push holoinsight/server:$tag -f

# docker buildx build --platform linux/amd64,linux/arm64/v8 --pull --push holoinsight/server:temp20230313-1
