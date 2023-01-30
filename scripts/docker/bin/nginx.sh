#!/usr/bin/env bash
set -e

# nginx entrypoint for supervisor

# 以 admin 的名义创建
sudo -u admin mkdir -p /home/admin/logs/nginx

nginx -g 'daemon off;'
