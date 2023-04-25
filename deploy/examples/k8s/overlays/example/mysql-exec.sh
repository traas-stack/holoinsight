#!/usr/bin/env bash
set -e

kubectl -n $ns exec -i mysql-0 -- mysql -uholoinsight -pholoinsight -Dholoinsight < $1 2>/dev/null
