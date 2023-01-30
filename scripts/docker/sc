#!/usr/bin/env bash
set -e

bin=/usr/bin/supervisorctl
conf=/etc/supervisord.conf

if [ "$#" = "0" ]; then
  $bin -c $conf status
  exit 0
fi

$bin -c $conf "$@"
