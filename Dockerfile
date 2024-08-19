FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine

WORKDIR /phoenix-sat-backend

COPY ./build/libs/phoenix-sat-backend-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

VOLUME /tmp

ENTRYPOINT ["java", "-jar", "/app.jar"]