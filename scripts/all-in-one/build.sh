#!/usr/bin/env bash
set -e

# usage: this script is used to build holoinsight-server fat jar.

script_dir=`dirname $0`
project_root=`realpath $script_dir/../..`

module=all-in-one

flag_quick=

while getopts 'q' OPT; do
  case "$OPT" in
  q)
    flag_quick=1
    ;;
  *)
    echo unsupported option $OPT
    exit 1
    ;;
  esac
done

mvn_goals="clean package"
if [ -n "$flag_quick" ]; then
  mvn_goals="package"
fi

rm $project_root/server/all-in-one/all-in-one-bootstrap/target/holoinsight-server.jar >/dev/null 2>&1 || true

mvn -f $project_root/server/server-parent/pom.xml \
  $mvn_goals \
  -DskipTests \
  -Dmaven.test.skip=true \
  -T 1C  \
  -am -pl ../${module}/${module}-bootstrap
