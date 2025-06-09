FROM gradle:8-jdk21 AS builder

WORKDIR /tmp

COPY settings.gradle .
COPY build.gradle .

RUN gradle --no-daemon dependencies

COPY . .

RUN gradle clean build --no-daemon -x check -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /tmp/build/libs/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "-jar", "/app/app.jar"]
