#!/usr/bin/env bash
set -e

current_dir=$( dirname $0 )
echo $current_dir

rm -rf $current_dir/src/main/java \
 $current_dir/target \

mkdir -p $current_dir/src/main/java

mvn -f $current_dir/pom.xml protobuf:compile
mvn -f $current_dir/pom.xml protobuf:compile-custom

rm -rf $current_dir/target

