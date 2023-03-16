#!/usr/bin/env bash

set -exo pipefail

## init varibles
USER="ceres"
DATA_DIR="/home/${USER}/data"
DATA_PATH="${DATA_DIR}/ceresdb"
CONFIG_FILE="/etc/ceresdb/ceresdb.toml"

# enable jemalloc heap profiling
export MALLOC_CONF="prof:true,prof_active:false,lg_prof_sample:19"

## data dir
mkdir -p ${DATA_DIR}
chmod +777 -R ${DATA_DIR}
chown -R ${USER}.${USER} ${DATA_DIR}

# hack: replace addr option to real container ip
# hack begin
ip=`hostname -I | awk '{print $1}'`
echo current ip is $ip

if ! grep $ip $CONFIG_FILE >/dev/null 2>&1; then
  sed -i "s|addr = \"127.0.0.1\"|addr = \"$ip\"|" $CONFIG_FILE
fi

cat $CONFIG_FILE
# hack end

exec /usr/bin/ceresdb-server --config ${CONFIG_FILE}
