#!/usr/bin/env bash
set -e

cd `dirname $0`/../..

cd front
yarn
yarn run build

mkdir -p dist/src

cp -R ./src/assets dist/src/

zip -rq dist.zip dist/
