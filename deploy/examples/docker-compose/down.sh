#!/usr/bin/env bash
set -e

# doc: Run this script to shutdown HoloInsight quick start demo

cd `dirname $0`
script_dir=`pwd`

env_file=".env"

$script_dir/docker-compose.sh --env-file $env_file down -v --remove-orphans
