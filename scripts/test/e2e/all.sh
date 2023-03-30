#!/usr/bin/env bash
set -e

# docs: run all E2E Integration Tests

cd `dirname $0`/../../..

# if $HOLOINSIGHT_DEV is not empty, then it is used as a prefix of docker images and containers to avoid conflicts with others.
if [ -n "$HOLOINSIGHT_DEV" ]; then
  tag="dev-$HOLOINSIGHT_DEV"
  export COMPOSE_server_image=holoinsight/server:$tag
  if [ "$build"x = "1"x ]; then
      holoinsight_server_tag=$tag ./scripts/docker/build.sh
  fi
else
  if [ "$build"x = "1"x ]; then
    ./scripts/docker/build.sh
  fi
fi

if [ "$build"x = "1"x ] && [ -z "$MVN_NO_CLEAN" ]; then
  # There is no need to clean when `build=1`
  MVN_NO_CLEAN='1'
fi

if [ -z "$forks" ]; then
  forks=1
fi

if [ "$MVN_NO_CLEAN"x = "1"x ]; then
  goals="integration-test"
else
  goals="clean integration-test"
fi

# prevent maven from using too much memory
MAVEN_OPTS="-Xmx512m" mvn $goals -f server/server-parent -T 1C -am -pl ../../test/server-e2e-test -Dgroups=e2e-all -DskipUTs=true -DskipITs=false -DitForkCount=${forks}

summary=./test/server-e2e-test/target/failsafe-reports/failsafe-summary.xml
cat $summary
errors=`awk -F '[<>]' '/errors/{print $3}' $summary`
failures=`awk -F '[<>]' '/failures/{print $3}' $summary`
if [ "$errors" -gt 0 ] || [ "$failures" -gt 0 ]; then
  echo
  echo 'IT failure'
  exit 1
fi
