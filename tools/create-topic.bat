@echo off
REM Kafka Topic创建脚本 - Windows版本

setlocal

set SCRIPT_DIR=%~dp0
set KAFKA_HOME=%SCRIPT_DIR%kafka_2.13-4.3.1

if "%~1"=="" (
    echo 用法: create-topic.bat ^<topic名称^> [分区数] [副本数]
    echo 示例: create-topic.bat test-topic 3 1
    exit /b 1
)

set TOPIC_NAME=%~1
set PARTITIONS=%~2
set REPLICAS=%~3

if "%PARTITIONS%"=="" set PARTITIONS=1
if "%REPLICAS%"=="" set REPLICAS=1

cd /d "%KAFKA_HOME%"

echo 正在创建Topic: %TOPIC_NAME%
echo 分区数: %PARTITIONS%
echo 副本数: %REPLICAS%

bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --topic %TOPIC_NAME% --partitions %PARTITIONS% --replication-factor %REPLICAS%

echo.
echo 查看创建的Topic:
bin\windows\kafka-topics.bat --describe --bootstrap-server localhost:9092 --topic %TOPIC_NAME%
