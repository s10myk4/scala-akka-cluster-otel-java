#!/bin/bash

set -exu

sbt docker:publishLocal

export KUBECONFIG=~/.kube/config
kubectl config set-context docker-desktop

kubectl apply -f kubernetes/namespace.json
kubectl config set-context --current --namespace=appka-1
kubectl apply -f kubernetes/akka-cluster.yml
kubectl apply -f kubernetes/otel-collector.yml

kubectl expose deployment appka --type=LoadBalancer --name=appka-service
