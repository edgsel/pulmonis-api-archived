FROM maven:3.6.3-jdk-8-slim AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/

ARG SERVER_PORT=6001

ENV ENV_SERVER_PORT=$SERVER_PORT
ENV ENV_DATABASE_HOST=localhost
ENV ENV_DATABASE_PORT=6002
ENV ENV_DATABASE_NAME=pulmonis
ENV ENV_DATABASE_USERNAME=unicornGary
ENV ENV_DATABASE_PASSWORD=magicalRainbow
ENV ENV_DATABASE_CONNECTION_STRING="jdbc:postgresql://${ENV_DATABASE_HOST}:${ENV_DATABASE_PORT}/${ENV_DATABASE_NAME}?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false"

RUN mvn clean install

FROM openjdk:8-jdk-alpine

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]