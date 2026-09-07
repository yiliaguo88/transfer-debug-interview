#!/bin/bash

# Kafka Topic创建脚本

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
KAFKA_HOME="$SCRIPT_DIR/kafka_2.13-4.3.1"

if [ -z "$1" ]; then
    echo "用法: ./create-topic.sh <topic名称> [分区数] [副本数]"
    echo "示例: ./create-topic.sh test-topic 3 1"
    exit 1
fi

TOPIC_NAME=$1
PARTITIONS=${2:-1}
REPLICAS=${3:-1}

cd "$KAFKA_HOME"

echo "正在创建Topic: $TOPIC_NAME"
echo "分区数: $PARTITIONS"
echo "副本数: $REPLICAS"

bin/kafka-topics.sh --create \
    --bootstrap-server localhost:9092 \
    --topic $TOPIC_NAME \
    --partitions $PARTITIONS \
    --replication-factor $REPLICAS

echo ""
echo "查看创建的Topic:"
bin/kafka-topics.sh --describe \
    --bootstrap-server localhost:9092 \
    --topic $TOPIC_NAME
