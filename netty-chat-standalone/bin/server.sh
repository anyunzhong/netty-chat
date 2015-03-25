#!/bin/sh

#####################################
MAIN_CLASS=net.datafans.netty.chat.standalone.StandaloneServer
PORT=50000
#####################################

BASE_PATH=`dirname $0`/..

CLASSPATH=.:${CLASSPATH}:$BASE_PATH/conf
for jar in $BASE_PATH/lib/*.jar; do
    CLASSPATH=$CLASSPATH:$jar
done


JVM_OPTS="-Xmx200M \
          -Xms200M \
          -Xmn35M \
          -XX:PermSize=30M \
          -XX:MaxPermSize=30M \
          -Xss256K \
          -XX:+DisableExplicitGC \
          -XX:SurvivorRatio=8 \
          -XX:+UseConcMarkSweepGC \
          -XX:+UseParNewGC \
          -XX:+CMSParallelRemarkEnabled \
          -XX:+UseCMSCompactAtFullCollection \
          -XX:CMSFullGCsBeforeCompaction=0 \
          -XX:+CMSClassUnloadingEnabled \
          -XX:CMSInitiatingOccupancyFraction=80 \
          -XX:SoftRefLRUPolicyMSPerMB=0 \
          -XX:+PrintGCDetails \
          -Xloggc:$BASE_PATH/log/gc.log "

# kill service
try=0

while [ $try -lt 5 ];
do
    pid=`lsof -i:$PORT | tail -n 1 | awk '{print $2}'`
    if [ "$pid" ]; then
        echo "[killing service... pid: $pid]"
        kill $pid
        try=`expr $try + 1`
    else
        break
    fi
    sleep 2
done


# launch service
exec su root -c "java $JVM_OPTS -cp $CLASSPATH $MAIN_CLASS &"

exit 1
