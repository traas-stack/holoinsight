#!/usr/bin/env bash
set -e

# usage: add license for all java sources

script_dir=`dirname $0`
project_root=`realpath $script_dir/..`

cd $project_root

mvn -f server/server-parent/pom.xml license:format

