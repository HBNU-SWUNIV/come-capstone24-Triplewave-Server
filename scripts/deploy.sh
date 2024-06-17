#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app
LOG_DIR=$REPOSITORY/logs
LOG_FILE=$REPOSITORY/logs/deploy.txt

if [ ! -d "$LOG_DIR" ]; then
  echo "로그 디렉토리 없음 $LOG_DIR 디렉토리 생성" | sudo mkdir -p $LOG_DIR
fi

TODAY=$(date +%Y-%m-%d)

echo "deploy.sh 시작" | sudo tee -a $LOG_FILE
sudo chmod 666 "$LOG_LOG_FILE"

cd $REPOSITORY || echo "repository 없음 $REPOSITORY" | sudo tee -a $LOG_FILE
echo "현재 디렉토리: $REPOSITORY" | sudo tee -a $LOG_FILE

APP_NAME=delivery

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z "$CURRENT_PID" ]
then
  echo "실행중인 서비스 없음." | sudo tee -a $LOG_FILE
else
  echo "kill -15 $CURRENT_PID" | sudo tee -a $LOG_FILE
  kill -15 "$CURRENT_PID"
  sleep 5
fi

echo "새 애플리케이션 배포" | sudo tee -a $LOG_FILE
JAR_NAME=$(ls $REPOSITORY | grep '.jar' | tail -n 1)
echo "jar 이름 : $JAR_NAME" | sudo tee -a $LOG_FILE
JAR_PATH=$REPOSITORY/$JAR_NAME

if [ ! -f "$LOG_DIR/delivery-$TODAY.txt" ]; then
    echo "로그 파일 없음 $LOG_DIR/delivery-$TODAY.txt, 파일 생성" | sudo tee -a $LOG_DIR/delivery-"$TODAY".txt
    sudo chmod 666 $LOG_DIR/delivery-"$TODAY".txt
fi

# 실행
nohup java -jar "$JAR_PATH" --spring.profiles.active=production > $LOG_DIR/delivery-"$TODAY".txt 2>&1 &

# 실행된 프로세스ID 확인
RUNNING_PROCESS=$(ps aux | grep java | grep "$JAR_NAME")
if [ -z "$RUNNING_PROCESS" ]
then
  echo "어플리케이션 프로세스가 실행되고 있지 않습니다." | sudo tee -a $LOG_FILE
else
  echo "어플리케이션 프로세스 확인: $RUNNING_PROCESS" | sudo tee -a $LOG_FILE
fi
