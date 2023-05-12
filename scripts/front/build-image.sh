#!/usr/bin/env bash
set -e

cd `dirname $0`/../..

./scripts/front/build.sh

if [ -z "$tag" ]; then
  tag="latest"
fi

docker build -t holoinsight/front:$tag -f ./scripts/front/Dockerfile .
