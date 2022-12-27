#!/usr/bin/env bash
set -e

# usage: this script is used to build holoinsight-server fat jar.

script_dir=`dirname $0`
project_root=`realpath $script_dir/../..`

module=all-in-one

mvn -f $project_root/server/server-parent/pom.xml \
  clean package \
  -DskipTests \
  -Dmaven.test.skip=true \
  -T 1C  \
  -am -pl ../${module}/${module}-bootstrap
