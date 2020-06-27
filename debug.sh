#!/bin/bash

nohup java -Xmx3072m -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar $(ls | grep .jar | grep -v original) > /dev/null &
