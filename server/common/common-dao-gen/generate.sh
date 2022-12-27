#!/usr/bin/env bash

# DB schema变化之后, 重新执行以下这个脚本, 可以生成新的do类
# 执行之前确保git里没有任何变化
# 执行之后diff看一下变化, 理论上只有少数几个文件的变化

current_dir=$( dirname $0 )
echo $current_dir

rm -rf $current_dir/src/main/java/ \
 $current_dir/target \
 $current_dir/src/main/resources/sqlmap \
 $current_dir/src/main/resources/sqlite/sqlmap

mkdir -p $current_dir/src/main/java

mvn -f $current_dir/pom.xml mybatis-generator:generate
