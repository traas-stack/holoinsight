#!/usr/bin/env bash
set -e

out=`curl -s --retry 3 --retry-max-time 10 --max-time 10 localhost:8089/internal/api/actuator/health`

status=`echo $out | jq ' .status ' -r`

if [ "$status" != "UP" ]; then
    echo $out
    exit 1
fi

exit 0
