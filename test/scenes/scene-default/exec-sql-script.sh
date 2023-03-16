#!/usr/bin/env bash
set -e

echo exec $1

mysql -h mysql -uholoinsight -pholoinsight -Dholoinsight < $1
