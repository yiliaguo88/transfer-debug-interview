@echo off
REM Kafka启动脚本 - Windows版本
REM 用于快速启动本地Kafka服务

setlocal

set SCRIPT_DIR=%~dp0
set KAFKA_HOME=%SCRIPT_DIR%kafka_2.13-4.3.1

echo 正在启动Kafka服务...
echo Kafka目录: %KAFKA_HOME%

cd /d "%KAFKA_HOME%"

REM 生成集群ID (如果还没有)
set CLUSTER_ID_FILE=%TEMP%\kraft-cluster-id.txt

if not exist "%CLUSTER_ID_FILE%" (
    echo 生成新的Cluster ID...
    for /f "delims=" %%i in ('bin\windows\kafka-storage.bat random-uuid') do set KAFKA_CLUSTER_ID=%%i
    echo !KAFKA_CLUSTER_ID! > "%CLUSTER_ID_FILE%"
    echo 生成的Cluster ID: !KAFKA_CLUSTER_ID!

    REM 格式化日志目录
    bin\windows\kafka-storage.bat format -t !KAFKA_CLUSTER_ID! -c config\server.properties
) else (
    set /p KAFKA_CLUSTER_ID=<"%CLUSTER_ID_FILE%"
    echo 使用已有的Cluster ID: !KAFKA_CLUSTER_ID!
)

echo.
echo ================================
echo 启动Kafka服务器...
echo Kafka将运行在 localhost:9092
echo 按 Ctrl+C 停止服务
echo ================================
echo.

REM 启动Kafka服务器
bin\windows\kafka-server-start.bat config\server.properties
