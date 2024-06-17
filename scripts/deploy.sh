#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app
LOG_FILE=$REPOSITORY/log.txt

echo "deploy-user.sh 시작" | sudo tee -a $LOG_FILE

cd $REPOSITORY || echo "repository 없음 $REPOSITORY" | sudo tee -a $LOG_FILE
echo "현재 디렉토리: $REPOSITORY" | sudo tee -a $LOG_FILE

APP_NAME=delivery-app
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z "$CURRENT_PID" ]
then
  echo "실행중인 user서비스 없음." | sudo tee -a $LOG_FILE
else
  echo "kill -9 $CURRENT_PID" | sudo tee -a $LOG_FILE
  kill -9 "$CURRENT_PID"
  sleep 5
fi

# 실행
nohup java -jar -Dspring.profiles.active=production -Dapp.name=$APP_NAME "$JAR_PATH" &

# 실행된 프로세스ID 확인
RUNNING_PROCESS=$(ps aux | grep java | grep "$JAR_NAME")
if [ -z "$RUNNING_PROCESS" ]
then
  echo "어플리케이션 프로세스가 실행되고 있지 않습니다." | sudo tee -a $LOG_FILE
else
  echo "어플리케이션 프로세스 확인: $RUNNING_PROCESS" | sudo tee -a $LOG_FILE
fi
