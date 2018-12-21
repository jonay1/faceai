#!/bin/sh
cd "`dirname $0`" || returnerr "$0 is not exsit."
APP_NAME=face-ai
if [ -z "$JAVA_HOME" ]; then
  echo "env JAVA_HOME required"
  exit 0
fi
echo "JDK HOME:$JAVA_HOME"

HTTP_PORT=8001
SYS_PORT=8002
JMX_PORT=8003

APP_DIR=app
[ -z $APP_DIR ] && mkdir $APP_DIR

SYS_MAIN_CLASS=org.springframework.boot.loader.JarLauncher
JAVA_OPTS+=" -Dserver.port=$HTTP_PORT -Dmanagement.server.port=$SYS_PORT "
JAVA_OPTS+=" -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dfile.encoding=utf-8"
JAVA_OPTS+=" -Xmx512m -Xms512m -XX:NewSize=128m -XX:MaxNewSize=128m -XX:PermSize=128m -XX:MaxPermSize=128m"
JAVA_OPTS+=" -XX:CompileThreshold=20000 -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSInitiatingOccupancyOnly"
JAVA_OPTS+=" -XX:+PrintCommandLineFlags -XX:-OmitStackTraceInFastThrow -XX:+ExplicitGCInvokesConcurrent -XX:+ParallelRefProcEnabled"
JAVA_OPTS+=" -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -Xloggc:logs/jvm.log"
JAVA_OPTS+=" -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=. -XX:ErrorFile=logs/java_error_%p.log"

#Custom lib path
export CLASSPATH=$APP_DIR/*:$CLASSPATH
# Restrict glibc thread cache count
export MALLOC_ARENA_MAX=4

#*******fuction define start********
#error return
returnerr()
{
    echo $1
    exit 1
}

# Check start: 0 -> okay, other -> not yet
checkStart()
{
    # ignore stdout/stderr log (e.g. Cannot stat file /proc/xxxx/fd/xxx: No such file or directory)
    fuser -s $SYS_PORT/tcp > /dev/null 2>&1
    return $?
}

# Check stop: 0 -> JVM still running, other -> JVM stopped
checkExit()
{
    fuser -s $JMX_PORT/tcp > /dev/null 2>&1
    return $?
}

#help info
help()
{
    echo "Usage: sh ${0} [start|stop|restart|check]"
    echo "eg: sh ${0} start"
    exit 1
}

#start
start()
{
    #if is running, return
    checkExit
    if [ $? -eq 0 ]; then
        echo "${APP_NAME} is running! Start aborted."
        return 1
    fi

    echo "${APP_NAME} begin to start..."
    nohup $JAVA_HOME/bin/java -cp $CLASSPATH -Dapp.name=${APP_NAME} ${JAVA_OPTS} ${SYS_MAIN_CLASS} >> /dev/null 2>&1 &

    echo "${APP_NAME} pid is: $!"

    #check
    loopcount=0
    echo -n "Binding server listening port"
    while [ $loopcount -lt 30 ];do
        checkStart
        if [ $? -eq 0 ]; then
            echo "OK!"
            return 0
        else
            echo -n "."
            sleep 1
            loopcount=`expr $loopcount + 1`
        fi
    done

    echo "timeout(30s)!!! Please check log: logs/app.log!"
}

#stop
stop()
{
    echo "${APP_NAME} begin to stop..."
    checkExit
    if [ $? -ne 0 ]; then
        echo "${APP_NAME} isn't running! Stop aborted."
        return 0
    fi

    curl -X POST http://localhost:$SYS_PORT/admin/shutdown
    echo ""
    
    #check
    loopcount=0
    echo -n "Unbinding JMX listening port"
    while [ $loopcount -lt 90 ];do
        checkExit
        if [ $? -ne 0 ]; then
            echo "OK!"
            echo "${APP_NAME} stoped."
            return 0
        else
            echo -n "."
            sleep 1
            loopcount=`expr $loopcount + 1`
        fi
    done

    echo "timeout(90s)!!! Please check log: logs/app.log!"

    #force quit
    fuser -s -k $JMX_PORT/tcp
    echo "${APP_NAME} was killed!"
}

restart()
{
    stop
    start
    exit 0
}

#check
#return valus: 0-running, 1-not running
check()
{
    checkExit
    if [ $? -eq 0 ]; then
        echo "${APP_NAME} is running."
        return 1
    else
        echo "${APP_NAME} isn't running"
        return 0
    fi
}
#*******fuction define end********


#do operation
case "${1}" in
start)
    start
    ;;
stop)
    stop
    ;;
restart)
    restart
    ;;
check)
    check
    ;;
check_health)
    check
    ;;
*)
    help
    ;;
esac