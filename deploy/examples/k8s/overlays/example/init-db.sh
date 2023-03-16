#!/usr/bin/env bash
set -e

script_dir=`dirname $0`

ns=holoinsight-example

echo
echo [database] wait for [mysql] to be available
kubectl -n $ns rollout status statefulset/mysql

echo [database] initialize table structures
kubectl -n $ns exec -i mysql-0 -- mysql -uholoinsight -pholoinsight -Dholoinsight < $script_dir/../../../../../server/extension/extension-common-flyway/src/main/resources/db/migration/V1__230301_INIT_DDL.sql 2>/dev/null

echo [database] populate example data
kubectl -n $ns exec -i mysql-0 -- mysql -uholoinsight -pholoinsight -Dholoinsight < $script_dir/../../../docker-compose/data.sql 2>/dev/null
echo
