#!/usr/bin/env bash
set -e

# docs: run one E2E Integration Test

cd `dirname $0`/../../..

it_class=$1
if [ -z "$1" ]; then
  it_class=$IT_CLASS
fi

if [ -z "$it_class" ]; then
  echo 'usage: one.sh <it_class>'
  exit 1
fi

if [ "$build"x = "1"x ]; then
  ./scripts/docker/build.sh
fi

IT_CLASS=$it_class mvn clean integration-test -f server/server-parent -am -pl ../../test/server-e2e-test -Dgroups=e2e-one -DskipITs=false

summary=./test/server-e2e-test/target/failsafe-reports/failsafe-summary.xml
cat $summary
errors=`awk -F '[<>]' '/errors/{print $3}' $summary`
failures=`awk -F '[<>]' '/failures/{print $3}' $summary`
if [ "$errors" -gt 0 ] || [ "$failures" -gt 0 ]; then
  echo 'IT failure'
  exit 1
fi
