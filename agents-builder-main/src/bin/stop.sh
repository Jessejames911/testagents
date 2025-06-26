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
