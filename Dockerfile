# 베이스 이미지로 사용할 Zulu OpenJDK 17 이미지를 가져옵니다.
FROM azul/zulu-openjdk:17

# 작업 디렉토리를 설정합니다.
WORKDIR /app

# 호스트의 JAR 파일을 Docker 이미지의 작업 디렉토리로 복사합니다.
COPY ./build/libs/finalticket-0.0.1-SNAPSHOT.jar finalticket.jar

# 포트 매핑
EXPOSE 8080

# 애플리케이션을 실행합니다.
CMD ["java", "-jar", "finalticket.jar"]