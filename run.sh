#!/bin/bash

nohup java -Xmx3072m -jar $(ls | grep .jar | grep -v original) > /dev/null &