#!/usr/bin/env bash
set -e

exec java $JAVA_OPTS -javaagent:/home/admin/skywalking-agent/skywalking-agent.jar -jar /home/admin/demo-server.jar
