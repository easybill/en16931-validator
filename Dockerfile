FROM eclipse-temurin:24-alpine

WORKDIR .

COPY ./build/quarkus-app/app/en16931-validator-0.6.0.jar .

ENTRYPOINT ["java", "-jar", "en16931-validator-0.6.0.jar"]