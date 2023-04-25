#!/usr/bin/env bash

ns=holoinsight-example

kubectl -n $ns delete statefulset mongo mysql holoinsight-server-example clusteragent ceresdb
kubectl -n $ns delete daemonset cadvisor daemonagent
kubectl -n $ns delete service registry gateway mongo mysql ceresdb service-collector prometheus
kubectl -n $ns delete deployment collector prometheus
kubectl -n $ns delete configmap holoinsight-agent-cm mongo-cm server-cm collector-config prometheus-cm
kubectl delete clusterrolebinding holoinsight-agent-example-clusterrolebinding
kubectl delete clusterrole holoinsight-agent-example-clusterrole

kubectl -n $ns get all
