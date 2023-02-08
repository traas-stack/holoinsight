#!/usr/bin/env bash
set -e

script_dir=`dirname $0`

echo [deploy] deploy k8s resources
kubectl apply -k $script_dir

sh $script_dir/../example/init-db.sh
sh $script_dir/../example/wait-server.sh
