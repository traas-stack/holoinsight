#!/usr/bin/env bash
set -e

cd `dirname $0`

find /sql -name "*.sql" | awk -F '__' '/^V/ {print $1,$2}' | sort -k 1.2,1.5n | awk '{print $1 "__" $2}' | xargs -I {} /exec-sql-script.sh {}
