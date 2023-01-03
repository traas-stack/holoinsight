#!/usr/bin/env bash
set -e

current_dir=$( dirname $0 )
echo $current_dir

rm -rf $current_dir/src/main/java/io/holoinsight/server/meta/proto \
 $current_dir/target \

mvn -f $current_dir/pom.xml protobuf:compile
mvn -f $current_dir/pom.xml protobuf:compile-custom

rm -rf $current_dir/target
