#!/usr/bin/env bash
set -e

cd `dirname $0`

./demo-client/build.sh
./demo-server/build.sh
