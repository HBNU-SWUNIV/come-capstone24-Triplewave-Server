# 기본 이미지 설정
FROM openjdk:17-jdk

### JAR_FILE 경로에 해당하는 파일을 Docker 이미지 내부로 복사한다.
COPY ./delivery-0.0.1-SNAPSHOT.jar delivery-dev.jar

# 컨테이너 실행 명령 설정
ENTRYPOINT ["java", "-jar", "/delivery-dev.jar", "--spring.profiles.active=production"]
