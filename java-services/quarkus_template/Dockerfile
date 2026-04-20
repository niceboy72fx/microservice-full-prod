FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle clean build -Dquarkus.package.type=fast-jar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /workspace/build/quarkus-app/lib/ /app/lib/
COPY --from=builder /workspace/build/quarkus-app/*.jar /app/
COPY --from=builder /workspace/build/quarkus-app/app/ /app/app/
COPY --from=builder /workspace/build/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
