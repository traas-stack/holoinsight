#!/usr/bin/env bash
set -e

# docs: run all E2E Integration Tests

cd `dirname $0`/../../..

if [ "$build"x = "1"x ]; then
  ./scripts/docker/build.sh
fi

mvn clean integration-test -f server/server-parent -am -pl ../../test/server-e2e-test -Dgroups=e2e-all -DskipITs=false
