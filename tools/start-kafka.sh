#!/bin/bash

# Kafka启动脚本
# 用于快速启动本地Kafka服务

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
KAFKA_HOME="$SCRIPT_DIR/kafka_2.13-4.3.1"

echo "正在启动Kafka服务..."
echo "Kafka目录: $KAFKA_HOME"

# 启动KRaft模式的Kafka (Kafka 4.x默认使用KRaft替代Zookeeper)
cd "$KAFKA_HOME"

# 生成集群ID (如果还没有)
if [ ! -f "/tmp/kraft-cluster-id" ]; then
    KAFKA_CLUSTER_ID=$(bin/kafka-storage.sh random-uuid)
    echo "$KAFKA_CLUSTER_ID" > /tmp/kraft-cluster-id
    echo "生成的Cluster ID: $KAFKA_CLUSTER_ID"

    # 格式化日志目录
    bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/server.properties
else
    KAFKA_CLUSTER_ID=$(cat /tmp/kraft-cluster-id)
    echo "使用已有的Cluster ID: $KAFKA_CLUSTER_ID"
fi

echo ""
echo "================================"
echo "启动Kafka服务器..."
echo "Kafka将运行在 localhost:9092"
echo "按 Ctrl+C 停止服务"
echo "================================"
echo ""

# 启动Kafka服务器
bin/kafka-server-start.sh config/server.properties
