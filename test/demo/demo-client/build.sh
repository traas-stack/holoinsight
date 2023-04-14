#!/usr/bin/env bash
set -e

cd `dirname $0`

mvn clean package -DskipTests -Dmaven.test.skip=true
