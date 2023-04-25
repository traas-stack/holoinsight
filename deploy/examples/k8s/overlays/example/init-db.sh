#!/usr/bin/env bash
set -e

script_dir=`dirname $0`

export ns=holoinsight-example

echo
echo [database] wait for [mysql] to be available
kubectl -n $ns rollout status statefulset/mysql

echo [database] initialize table structures

find $script_dir/../../../../../server/extension/extension-common-flyway/src/main/resources/db/migration -name "*.sql" | sort | xargs -I {} $script_dir/mysql-exec.sh {}

echo [database] populate example data
$script_dir/mysql-exec.sh $script_dir/../../../../../test/scenes/scene-default/data.sql
echo
