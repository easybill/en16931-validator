FROM eclipse-temurin:21-alpine

WORKDIR /app

COPY ./build/en16931-validator--runner.jar /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

RUN chown -R appuser:appgroup /app

USER appuser

ENTRYPOINT ["java", "-jar", "en16931-validator--runner.jar"]