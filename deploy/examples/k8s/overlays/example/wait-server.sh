#!/usr/bin/env bash
set -e

ns=holoinsight-example

echo [server] wait for [holoinsight-server-example] to be available
kubectl -n $ns rollout status statefulset/holoinsight-server-example
echo

kubectl -n $ns get pods -o wide

echo [server] deploy successfully, visit holoinsight at http://localhost:8080
kubectl -n $ns port-forward pod/holoinsight-server-example-0 --address 0.0.0.0 8080:80
