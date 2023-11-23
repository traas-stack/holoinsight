#!/usr/bin/env bash
set -e

mvn clean install -T 1C -Dmaven.test.skip -DskipTests -f ../../server-parent/pom.xml -am -pl ../agg/agg-core

current_dir=$( dirname $0 )
echo $current_dir

#rm -rf $current_dir/src/main/java/ \
# $current_dir/target \

#mkdir -p $current_dir/src/main/java/

mvn -f $current_dir/pom.xml protobuf:compile
#mvn -f $current_dir/pom.xml protobuf:compile-custom

rm -rf $current_dir/target

