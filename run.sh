#!/bin/bash

nohup java -Xmx1024m -jar $(ls | grep .jar | grep -v original) > /dev/null &