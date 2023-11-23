#!/usr/bin/env bash
set -e

mvn clean compile -T 1C -Dmaven.test.skip=true -DskipTests=true -f ../../server-parent/pom.xml -am -pl ../agg/agg-executor-bootstrap
