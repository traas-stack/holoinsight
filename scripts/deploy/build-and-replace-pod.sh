#!/usr/bin/env bash
set -e

pod=$1
if [ -z "$pod" ]; then
  echo 'build-and-place-pod.sh <pod>'
  exit 1
fi

cd `dirname $0`/../..

echo build fat jar ...
./scripts/all-in-one/build.sh
echo


echo stop app ...
kubectl -n holoinsight-server exec $pod -- bash -c 'sc stop app'
echo

echo copy app.jar into $pod
kubectl -n holoinsight-server cp ./server/all-in-one/all-in-one-bootstrap/target/holoinsight-server.jar $pod:/home/admin/app.jar
echo copy done

echo start app ...
kubectl -n holoinsight-server exec $pod -- bash -c 'sc start app'
echo
