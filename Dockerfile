FROM maven:3.8.6-eclipse-temurin-17-alpine AS builder

COPY /src /usr/app/src

COPY pom.xml /usr/app

RUN ["mvn", "clean", "package", "-f", "/usr/app/pom.xml"]

FROM openjdk:17-alpine

COPY route-cfg.yml /application/

COPY --from=builder usr/app/target/*.jar /application/app.jar

ENV DUMMY_CONFIG_PATH=/application/route-cfg.yml
ENV DUMMY_SERVER_PORT=8080
ENV DUMMY_SERVER_THREAD_COUNT=200

EXPOSE ${DUMMY_SERVER_PORT}

ENTRYPOINT ["java", "-jar", "/application/app.jar"]