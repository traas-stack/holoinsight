#!/usr/bin/env bash
set -e

if [ -n "$HOLOINSIGHT_DEV" ]; then
  # automatically enable debug mode when developing
  debug=1
fi

source `dirname $0`/../common/`basename $0`
