#!/usr/bin/env bash
set -e

host=mysql

echo [database] initialize table structures
mysql -h $host -uholoinsight -pholoinsight -Dholoinsight < /ddl.sql

echo [database] populate example data
mysql -h $host -uholoinsight -pholoinsight -Dholoinsight < /data.sql
