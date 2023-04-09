#!/usr/bin/env bash
set -e

if [ -n "$APP" ]; then
  sed -i "s/app: .*/app: $APP/g" /usr/local/holoinsight/agent/agent.yaml
fi

cd /usr/local/holoinsight/agent

pkill -f /usr/local/holoinsight/agent/bin/agent || true

nohup /usr/local/holoinsight/agent/bin/agent > /usr/local/holoinsight/agent/logs/stdout.log &
