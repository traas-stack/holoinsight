#!/usr/bin/env bash
set -e

cd `dirname $0`/../..
project_root=`pwd`

buildx_bin=""

if docker buildx >/dev/null 2>&1; then
  buildx_bin='docker buildx'
else
  tochecks=( "$HOME/.docker/cli-plugins" /usr/local/lib/docker/cli-plugins /usr/local/libexec/docker/cli-plugins /usr/lib/docker/cli-plugins /usr/libexec/docker/cli-plugins )

  for tocheck in ${tochecks[@]}; do
    if [ -e "$tocheck/buildx" ]; then
      buildx_bin="$tocheck/buildx"
      break
    fi
  done
fi

if [ -z "$buildx_bin" ]; then
  echo Please install buildx before running this script.
  echo Check https://docs.docker.com/build/install-buildx/
  exit 1
fi

echo use $buildx_bin

platform=$1
tag=$2

$buildx_bin build --platform $platform -t holoinsight/server:$tag -f ./scripts/docker/Dockerfile .
