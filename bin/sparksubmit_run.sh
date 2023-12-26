#!/bin/bash

set -x
cd $(dirname $0)
echo -e "Input parameter description:\n"
echo -e "\t-local: Local mode submission\n"
echo -e "\t-client: Yarn's client mode submission\n"
echo -e "\t-cluster: Yarn's cluster mode submission\n"
if [ $# -eq 3 ]; then
  type=$1
  input_path=$2
  if [ $type == "-local" ]; then
    sh ./sparksubmit_local.sh $input_path $3
  fi
  if [ $type == "-client" ]; then
    sh ./sparksubmit_yarnclient.sh $input_path $3
  fi
  if [ $type == "-cluster" ]; then
    sh ./sparksubmit_yarncluster.sh $input_path $3
  fi
elif [ $# -eq 4 ]; then
  type=$1
  input_path=$2
  output_path=$3
  if [ $type == "-local" ]; then
    sh ./sparksubmit_local.sh $input_path $output_path $4
  fi
  if [ $type == "-client" ]; then
    sh ./sparksubmit_yarnclient.sh $input_path $output_path $4
  fi
  if [ $type == "-cluster" ]; then
    sh ./sparksubmit_yarncluster.sh $input_path $output_path $4
  fi
else
  echo "Please enter the submission type (-local/-client/-cluster) and the path for decompression"
fi
