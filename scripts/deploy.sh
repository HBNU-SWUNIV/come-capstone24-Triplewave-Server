#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app
LOG_DIR=$REPOSITORY/logs
TODAY=$(date +%Y-%m-%d)
LOG_FILE=$LOG_DIR/deploy-$TODAY.txt

# 로그 파일 생성 여부 확인 및 생성
if [ ! -f "$LOG_FILE" ]; then
    echo "로그 파일 없음 $LOG_FILE, 파일 생성" | sudo tee -a "$LOG_FILE"
    sudo chmod 666 "$LOG_FILE"
fi

# 환경변수 DOCKER_APP_NAME을 delivery로 설정
DOCKER_APP_NAME=delivery

# Nginx가 실행 중이지 않으면 Nginx 서비스 시작
if ! sudo docker ps | grep -q nginx; then
    echo "Nginx 실행 중이 아님, Nginx 시작" | sudo tee -a "$LOG_FILE"
    sudo docker compose -p ${DOCKER_APP_NAME}-nginx -f "$REPOSITORY/docker-compose.yml" up -d nginx
fi

# 실행 중인 blue 컨테이너가 있는지 확인
EXIST_BLUE=$(sudo docker ps -f "name=${DOCKER_APP_NAME}-blue" -q)

# 배포 시작 로그 작성
echo "배포 시작일자 : $(date '+%Y-%m-%d %H:%M:%S')" | sudo tee -a "$LOG_FILE"

# Green이 실행 중이면 Blue 컨테이너 시작
if [ -z "$EXIST_BLUE" ]; then
    echo "Blue 배포 시작 : $(date '+%Y-%m-%d %H:%M:%S')" | sudo tee -a "$LOG_FILE"
    sudo docker compose -p ${DOCKER_APP_NAME}-blue -f "$REPOSITORY/docker-compose.yml" up -d --build blue

    sleep 30

    echo "Green 중단 시작 : $(date '+%Y-%m-%d %H:%M:%S')" | sudo tee -a "$LOG_FILE"
    sudo docker compose -p ${DOCKER_APP_NAME}-green -f "$REPOSITORY/docker-compose.yml" down
    sudo docker image prune -af

    echo "Green 중단 완료 : $(date '+%Y-%m-%d %H:%M:%S')" | sudo tee -a "$LOG_FILE"

# Blue가 실행 중이면 Green 컨테이너 시작
else
    echo "Green 배포 시작 : $(date '+%Y-%m-%d %H:%M:%S')" | sudo tee -a "$LOG_FILE"
    sudo docker compose -p ${DOCKER_APP_NAME}-green -f "$REPOSITORY/docker-compose.yml" up -d --build green

    sleep 30

    echo "Blue 중단 시작 : $(date '+%Y-%m-%d %H:%M:%S')" | sudo tee -a "$LOG_FILE"
    sudo docker compose -p ${DOCKER_APP_NAME}-blue -f "$REPOSITORY/docker-compose.yml" down
    sudo docker image prune -af

    echo "Blue 중단 완료 : $(date '+%Y-%m-%d %H:%M:%S')" | sudo tee -a "$LOG_FILE"
fi

# 배포 종료 로그 작성
echo "배포 종료 : $(date '+%Y-%m-%d %H:%M:%S')" | sudo tee -a "$LOG_FILE"
echo "===================== 배포 완료 =====================" | sudo tee -a "$LOG_FILE"
