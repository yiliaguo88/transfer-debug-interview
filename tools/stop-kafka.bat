@echo off
REM Kafka停止脚本 - Windows版本

setlocal

set SCRIPT_DIR=%~dp0
set KAFKA_HOME=%SCRIPT_DIR%kafka_2.13-4.3.1

echo 正在停止Kafka服务...

cd /d "%KAFKA_HOME%"

REM 停止Kafka服务器
bin\windows\kafka-server-stop.bat

echo Kafka服务已停止
