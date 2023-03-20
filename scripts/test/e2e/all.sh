#!/usr/bin/env bash
set -e

# docs: run all E2E Integration Tests

cd `dirname $0`/../../..

# if $HOLOINSIGHT_DEV is not empty, then it is used as a prefix of docker images and containers to avoid conflicts with others.
if [ -n "$HOLOINSIGHT_DEV" ]; then
  tag="dev-$HOLOINSIGHT_DEV-`basename $0 | awk -F. '{print $1}'`"
  export COMPOSE_server_image=holoinsight/server:$tag
  if [ "$build"x = "1"x ]; then
      holoinsight_server_tag=$tag ./scripts/docker/build.sh
  fi
else
  if [ "$build"x = "1"x ]; then
    ./scripts/docker/build.sh
  fi
fi

if [ -z "$forks" ]; then
  forks=1
fi

mvn clean integration-test -f server/server-parent -am -pl ../../test/server-e2e-test -Dgroups=e2e-all -DskipITs=false -DitForkCount=${forks}
