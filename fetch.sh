#!/bin/bash

jar_path="${JAR_PATH:=cli/target/*-dependencies.jar}"
out_path="${OUT_PATH:=archive}"
num_days="${NUM_DAYS:=7}"

d=$(date +%Y-%m-%d)
for i in $(seq 1 $num_days); do
  echo "fetching $d ..."
  java -jar $jar_path $d > "$out_path/$d.json"
  d=$(date -d "$d + 1 day" +%Y-%m-%d)
done