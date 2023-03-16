#!/usr/bin/env bash
set -e

image=$1
to=$2

docker pull $image && docker save -o docker-images/$to $image
