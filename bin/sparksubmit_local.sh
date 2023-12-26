#!/bin/bash

set -x
if [ $# -eq 0 ]; then
  echo "Please enter the decompression path or file"
elif [ $# -eq 2 ]; then
  # 解压到当前路径
  input_path=$1
  spark-submit \
      --files ./config/$2 \
      --class com.lingyi.data.emr.tartool.TarToolMain \
      --master local[*] \
      ./spark-tar-tool-1.0-SNAPSHOT.jar $input_path $2
elif [ $# -eq 3 ]; then
  # 自定义解压路径
  input_path=$1
  output_path=$2
  spark-submit \
      --files ./config/$3 \
      --class com.lingyi.data.emr.tartool.TarToolMain \
      --master local[*] \
      ./spark-tar-tool-1.0-SNAPSHOT.jar $input_path $output_path $3
else
  echo "If the number of parameters is greater than 2, the parameter values are as follows: $@"
fi
