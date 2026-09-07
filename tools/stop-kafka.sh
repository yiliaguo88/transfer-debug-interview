#!/bin/bash

# Kafka停止脚本

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
KAFKA_HOME="$SCRIPT_DIR/kafka_2.13-4.3.1"

echo "正在停止Kafka服务..."

cd "$KAFKA_HOME"

# 停止Kafka服务器
bin/kafka-server-stop.sh

echo "Kafka服务已停止"
