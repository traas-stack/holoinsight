#!/usr/bin/env bash
set -e

source `dirname $0`/../common/setup-env.sh

mvn clean -f $project_root/server/server-parent
