#!/usr/bin/env bash
set -e

if [ "admin" != "`whoami`" ]; then
  echo "app.sh must running with 'admin' user"
  exit 1
fi

JAVA_BIN=/usr/local/java/bin/java
APP_JAR=/home/admin/app.jar

JAVA_OPTS="-Dfile.encoding=UTF-8"

# java heap config
# Container memory limit may not be accurately read inside the running container.
# With a high probability, you will read the memory capacity of the physical machine.
# In this case, you need configure your java heap using JAVA_APP_OPTS env, for example "JAVA_APP_OPTS='-Xmx1g -Xms1g -Xmn 512m'".
# '[[' and ']]' must be used here.
# Compatible with APP_JAVA_OPTS
if [ -z "$JAVA_APP_OPTS" ] && [ -n "$APP_JAVA_OPTS" ]; then
  JAVA_APP_OPTS=$APP_JAVA_OPTS
fi
if [[ "$JAVA_APP_OPTS" != *"-Xmx"* ]]; then
  # setup heap config
  memCapacity=`cat /proc/meminfo | grep "MemTotal:" | awk  '{print $2}'`

  echo "read /proc/meminfo memCapacity=${memCapacity}KB"

  if [ "$memCapacity" -lt 8388608 ]; then
    # maybe 2c4g
    JAVA_OPTS="$JAVA_OPTS -Xmx3200m -Xms3200m -Xmn1536m -XX:MaxDirectMemorySize=256m"
  elif [ "$memCapacity" -lt 16777216 ]; then
    # maybe 4c8g
    JAVA_OPTS="$JAVA_OPTS -Xmx6800m -Xms6800m -Xmn3400m -XX:MaxDirectMemorySize=512m"
  elif [ "$memCapacity" -lt 33554432 ]; then
    # maybe 8c16g
    JAVA_OPTS="$JAVA_OPTS -Xmx14g -Xms14g -Xmn8g -XX:MaxDirectMemorySize=1g"
  else
    # unknown container specifications
    JAVA_OPTS="$JAVA_OPTS -Xmx1g -Xms1g -Xmn512m -XX:MaxDirectMemorySize=256m"
  fi

fi


# GC
JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=65 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:+CMSScavengeBeforeRemark -XX:CMSMaxAbortablePrecleanTime=5000 -XX:CMSScheduleRemarkEdenPenetration=50 -XX:+CMSParallelInitialMarkEnabled -XX:PermSize=256m -XX:MaxPermSize=256m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=3"
JAVA_OPTS="$JAVA_OPTS -Xloggc:/home/admin/logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintAdaptiveSizePolicy -XX:+PrintTenuringDistribution -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100m -verbose:gc -XX:+DisableExplicitGC"

# other
JAVA_OPTS="$JAVA_OPTS -Dlogging.path=/home/admin/logs"

# Compatible with 'PROFILE' env variable.
if [ -n "$PROFILE" ] && [ -z "$SPRING_PROFILES_ACTIVE" ]; then
  export SPRING_PROFILES_ACTIVE=$PROFILE
fi

# If you need debug, you can un-comment this line, and restart app using 'sc restart app' in 'supervisord' mode.
# Otherwise you'd have to redeploy this app with JAVA_DEBUG_OPTS='-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n' to enable debug mode.
#debug_opts="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

SW_AGENT_PATH="/home/admin/skywalking-agent/skywalking-agent.jar"
if [ -f $SW_AGENT_PATH ];
then
  JAVA_OPTS="-javaagent:$SW_AGENT_PATH $JAVA_OPTS"
fi

echo $JAVA_BIN -server $JAVA_OPTS $JAVA_APP_OPTS $JAVA_DEBUG_OPTS $debug_opts -jar $APP_JAR
exec $JAVA_BIN -server $JAVA_OPTS $JAVA_APP_OPTS $JAVA_DEBUG_OPTS $debug_opts -jar $APP_JAR
