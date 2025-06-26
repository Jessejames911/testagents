#!/bin/bash

SpringBoot=agents-builder-main.jar

echo "Stop $SpringBoot"
boot_id=`ps -ef |grep java|grep $SpringBoot|grep -v grep|awk '{print $2}'`
count=`ps -ef |grep java|grep $SpringBoot|grep -v grep|wc -l`

if [ $count != 0 ];then
    echo "find PID $boot_id"
    kill -9 $boot_id
    echo "kill PID $boot_id"
    echo "stop success"
else
    echo "not found $SpringBoot process"
fi

if [ ! -d "../log/" ]; then
    echo 'create log dir'
    mkdir -p ../log
    echo 'create log dir finished'
fi

active=$1
echo $active

nohup java -jar ../$SpringBoot \
--spring.profiles.active="$active" \
--spring.config.location=../config/ \
>> ../log/catlina-`date "+%Y%m%d%H%M"`.log 2>&1 &
echo "start success: [PID] $!"
