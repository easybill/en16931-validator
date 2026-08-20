FROM eclipse-temurin:24-alpine

WORKDIR .

COPY ./build/en16931-validator-0.7.0-runner.jar .

ENTRYPOINT ["java", "-jar", "en16931-validator-0.7.0-runner.jar"]