#!/usr/bin/env bash

# DB schema变化之后, 重新执行以下这个脚本, 可以生成新的do类
# 执行之前确保git里没有任何变化
# 执行之后diff看一下变化, 理论上只有少数几个文件的变化

#export DB_ADDR=""
#export DB_NAME=""
#export DB_USER=""
#export DB_PASSWORD=""

if [ -z "$DB_ADDR" ] || [ -z "$DB_NAME" ] || [ -z "$DB_USER" ] || [ -z "$DB_PASSWORD" ]; then
  echo 'set DB_* env before running this generate.sh'
  exit 1
fi

current_dir=$( dirname $0 )
echo $current_dir

rm -rf $current_dir/src/main/java/io/holoinsight/server/registry/dao \
 $current_dir/target \
 $current_dir/src/main/resources/sqlmap \
 $current_dir/src/main/resources/sqlite/sqlmap

cat $current_dir/src/main/resources/generatorConfig.xml.template \
  | sed "s/\${DB_ADDR}/$DB_ADDR/" \
  | sed "s/\${DB_NAME}/$DB_NAME/" \
  | sed "s/\${DB_USER}/$DB_USER/" \
  | sed "s/\${DB_PASSWORD}/$DB_PASSWORD/" > $current_dir/src/main/resources/generatorConfig.xml

mvn -f $current_dir/pom.xml mybatis-generator:generate || true

rm $current_dir/src/main/resources/generatorConfig.xml

