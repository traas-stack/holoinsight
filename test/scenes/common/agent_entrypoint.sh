#!/usr/bin/env bash
set -e

if [ ! -e "/usr/local/holoinsight/agent/agent.yaml" ]; then
  cp /share/agent.yaml /usr/local/holoinsight/agent/agent.yaml
fi

if [ -n "$APP" ]; then
  sed -i "s/app: .*/app: $APP/g" /usr/local/holoinsight/agent/agent.yaml
fi

if [ ! -e "/usr/local/holoinsight/agent/bin/agent" ]; then
  cp /share/agent /usr/local/holoinsight/agent/bin/agent
fi

cd /usr/local/holoinsight/agent

pkill -f /usr/local/holoinsight/agent/bin/agent || true

nohup /usr/local/holoinsight/agent/bin/agent > /usr/local/holoinsight/agent/logs/stdout.log &
