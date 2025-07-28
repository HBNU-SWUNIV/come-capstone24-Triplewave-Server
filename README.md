## Overview
이 프로젝트는 학교 내 자율주행 로봇 배달 서비스의 백엔드 서버입니다. Spring Boot 기반으로 구현되었으며, 실시간 맵 트래킹, 주문 관리, 멤버 관리, ROS와의 연동 기능을 제공합니다. 

## Main Features
- **주문 관리**  
  - 주문 생성, 조회, 수락, 완료 처리  
  - 기간별/당일 주문 내역 조회  
  - 주문 상태(대기, 수락, 진행중, 배달완료 등) 관리

- **멤버 관리**  
  - 멤버 정보 조회  
  - 학과별 교수/조교 리스트 및 카운트 조회  
  - 이름/학과 검색 기능

- **실시간 맵 트래킹 및 로봇 위치 표시**  
  - SSE 엔드포인트를 통해 실시간 맵 데이터 및 로봇 위치 전송  
  - 실시간 맵 및 위치 시각화

- **ROS 연동**  
  - ROSBridge와 WebSocket 통신  
  - 로봇 네비게이션 명령 전송 및 상태 추적  
  - 맵 데이터 및 위치 정보 수신


## Server Architecture 
<img width="1136" alt="아키텍처" src="https://github.com/user-attachments/assets/8d630cf0-b91e-40d5-a082-c3a8302aca82">


## ROS Study 
아래는 ROS를 공부하며 작성한 내용입니다.

1. [Ubuntu20, Ros1 Noetic 설치](https://imported-event-228.notion.site/Ubuntu20-Ros1-Noetic-a8d1493e3d7a47d6ba5105b290a15d9c?pvs=4)
2. [Ros Node, Ros Topic](https://imported-event-228.notion.site/1-Ros-Node-Ros-Topic-30e5af0172684907bc213f1886e7d1d2?pvs=4)
3. [ROS with Vscode](https://imported-event-228.notion.site/2-ROS-with-Vscode-3d10527a890349e298e0dde4e62cc923?pvs=4)
4. [ROS Publisher, Subscriber](https://imported-event-228.notion.site/3-ROS-Publisher-Subscriber-a4ffbe305f7d419a9b1630efe65ac9c4?pvs=4)
5. [ROS Gazebo with Github Repo](https://imported-event-228.notion.site/4-ROS-Gazebo-with-Github-Repo-7ade95bc3197462c9002699c5fc2b812?pvs=4)
6. [ROS Gazebo Basic Programming](https://imported-event-228.notion.site/5-ROS-Gazebo-Basic-Programming-d1db4edbabad40e5bc6dc17228743921?pvs=4)
7. [Slam + Navigation with turtlebot3](https://imported-event-228.notion.site/6-Slam-Navigation-with-turtlebot3-bc04ceb328cc45e78068de11714aac89?pvs=4)
8. [ROS Topic Study](https://imported-event-228.notion.site/7-ROS-Topic-Study-149b57af9764451793b5bffeffb3f955?pvs=4)
9. [Server and ROS Communication](https://imported-event-228.notion.site/8-Server-and-ROS-Communication-33bd8bb08bbe49fc86a72c0b2b2b3adf?pvs=4)
10. [로봇에게 네비게이션 명령 전송 및 목표 상태 추적과 완료 알림](https://imported-event-228.notion.site/9-e438a313392f43928fd07fc4ed26af1a?pvs=4)
11. [맵 상에 로봇의 실시간 위치 표시하기](https://imported-event-228.notion.site/10-3c75c67e304542538dfb173b53a50624?pvs=4)

## Tech Stack
- Java 17 / Spring Boot 3.2.5
- MySQL
- JPA 
- QueryDSL
- WebSocket / SSE
- ROSBridge 
- Gradle
