#!/usr/bin/env bash
set -e

source `dirname $0`/../common/setup-env.sh

docker-compose down --remove-orphans --volumes
