#!/usr/bin/env bash
set -e

cd `dirname $0`

find /sql -name "*.sql" | sort | xargs -I {} /exec-sql-script.sh {}
