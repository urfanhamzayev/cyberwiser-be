FROM openjdk:17-jdk-slim

WORKDIR /phoenix-sat-backend

COPY ./build/libs/phoenix-sat-backend-0.0.1-SNAPSHOT.jar /app/myapp.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]