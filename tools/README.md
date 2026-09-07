# Kafka 工具目录

本目录包含面试环境所需的Kafka服务，支持 macOS/Linux 和 Windows 平台。

## 目录结构

- `kafka_2.13-4.3.1/` - Kafka 4.3.1 二进制文件
- `start-kafka.sh` / `start-kafka.bat` - Kafka启动脚本
- `stop-kafka.sh` / `stop-kafka.bat` - Kafka停止脚本
- `create-topic.sh` / `create-topic.bat` - Topic创建脚本

## 快速开始

### macOS / Linux

#### 1. 启动Kafka

```bash
cd tools
./start-kafka.sh
```

Kafka将在 `localhost:9092` 上运行。

#### 2. 创建Topic

在另一个终端中：

```bash
cd tools
./create-topic.sh test-topic 3 1
```

参数说明：
- 第一个参数：Topic名称（必填）
- 第二个参数：分区数（默认1）
- 第三个参数：副本数（默认1）

#### 3. 停止Kafka

```bash
cd tools
./stop-kafka.sh
```

### Windows

#### 1. 启动Kafka

```cmd
cd tools
start-kafka.bat
```

Kafka将在 `localhost:9092` 上运行。

#### 2. 创建Topic

在另一个命令提示符窗口中：

```cmd
cd tools
create-topic.bat test-topic 3 1
```

参数说明：
- 第一个参数：Topic名称（必填）
- 第二个参数：分区数（默认1）
- 第三个参数：副本数（默认1）

#### 3. 停止Kafka

```cmd
cd tools
stop-kafka.bat
```

## 说明

- 使用KRaft模式运行（不需要Zookeeper）
- **macOS/Linux**: 数据存储在 `/tmp/kraft-combined-logs`，Cluster ID保存在 `/tmp/kraft-cluster-id`
- **Windows**: 数据存储在 `%TEMP%\kraft-combined-logs`，Cluster ID保存在 `%TEMP%\kraft-cluster-id.txt`
- 如需重置Kafka，删除上述目录和文件后重新启动

## Windows用户注意事项

如果在Windows上遇到问题，可以使用以下替代方案：
- **WSL2（推荐）** - 在Windows Subsystem for Linux中运行Linux脚本
- **Git Bash** - 使用Git Bash运行 `.sh` 脚本
