#!/usr/bin/env bash
set -e

# 在这里做 /home/admin 的权限修复
# 为什么在启动的瞬间才去做呢? 而不是在Dockerfile里做呢?
# 因为在 Dockerfile 里做 chown 的话, 对于 Docker build cache 来说, 这算是一次文件改动, 因此每次的构建增量都是几十MB
# 而在启动的时候去做, 每次的构建增量只有几KB!

function fixDir() {
  local dir=$1
  if [ ! -e "$dir" ]; then
    return
  fi
  dirOwner=`stat -c '%U' $dir`
  currentUser=`id -nu`

  if [ "$dirOwner" != "$currentUser" ]; then
      sudo chown $currentUser:$currentUser -R $dir
  fi
}

fixDir /home/admin/logs
ln -sf /home/admin/api /home/admin/logs/api || true

exec sudo -E /usr/bin/supervisord -n
