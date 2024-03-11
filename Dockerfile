FROM eclipse-temurin:21-jdk-alpine@sha256:6da40af213861c1b9cc475affa011d395d4dbcf0cb031b6a5d05a658fd9e3c5e as build

ARG git_sha="development"
ENV GIT_SHA=$git_sha

WORKDIR /scratch

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew /scratch/
COPY vendored vendored
COPY src src

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine@sha256:fb4150a30569aadae9d693d949684a00653411528e62498b9900940c9b5b8a66

ARG git_sha="development"
ENV GIT_SHA=$git_sha
ENV SENTRY_RELEASE=charlie@$git_sha

COPY --from=build /scratch/build/libs/charlie.jar .

# HTTP
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "charlie.jar"]
