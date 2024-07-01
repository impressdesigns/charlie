FROM eclipse-temurin:21-jdk-alpine@sha256:68a8a4ad547e750f497824540d90ff29d4b819a6a6287a5eb1b03a71e4c2167b as build

ARG git_sha="development"
ENV GIT_SHA=$git_sha

WORKDIR /scratch

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew /scratch/
COPY vendored vendored
COPY src src

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine@sha256:f05c742dd20051b104b939370f7db4d6eb420cc7fd842aeb4e2446837da3bd03

ARG git_sha="development"
ENV GIT_SHA=$git_sha
ENV SENTRY_RELEASE=charlie@$git_sha

COPY --from=build /scratch/build/libs/charlie.jar .

# HTTP
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "charlie.jar"]
