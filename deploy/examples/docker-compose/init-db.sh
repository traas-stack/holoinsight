#!/usr/bin/env bash
set -e

cd `dirname $0`

find /sql -name "*.sql"  | awk -F '__|/V' '{print $1, $2, $3}'|sort -k 2.1,2.4n | awk '{print $1 "/V" $2 "__" $3}' | xargs -I {} /exec-sql-script.sh {}
