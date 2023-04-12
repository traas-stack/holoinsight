#!/usr/bin/env bash
set -e

cd `dirname $0`

mvn protobuf:compile
mvn protobuf:compile-custom

rm -rf ../demo-client/src/main/java/io/holoinsight/server/demo/server/grpc || true
mkdir -p ../demo-client/src/main/java/io/holoinsight/server/demo/server/grpc
cp -r ./src/main/java/io/holoinsight/server/demo/server/grpc ../demo-client/src/main/java/io/holoinsight/server/demo/server/grpc
