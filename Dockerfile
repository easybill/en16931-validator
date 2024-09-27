FROM eclipse-temurin:21-alpine

WORKDIR .

COPY ./build/en16931-validator--runner.jar .

ENTRYPOINT ["java", "-jar", "en16931-validator--runner.jar"]