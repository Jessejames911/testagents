#!/bin/bash

SpringBoot=agents-builder-main.jar

if [ ! -d "../log/catlina" ]; then
    echo 'create log dir'
    mkdir -p ../log/
    echo 'create log dir finished'
fi

active=$1
echo "$active"

nohup java -jar ../$SpringBoot \
--spring.profiles.active="$active" \
--spring.config.location=../config/ \
 >> ../log/catlina-`date "+%Y%m%d%H%M"`.log 2>&1 &
echo "start success: [PID] $!"
