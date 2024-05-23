FROM eclipse-temurin:21-jdk-alpine@sha256:ebfc28d35b192c55509e3c7cc597d91136528f1a9d3261965b44663af9eb4b4b as build

ARG git_sha="development"
ENV GIT_SHA=$git_sha

WORKDIR /scratch

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew /scratch/
COPY vendored vendored
COPY src src

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine@sha256:23467b3e42617ca197f43f58bc5fb03ca4cb059d68acd49c67128bfded132d67

ARG git_sha="development"
ENV GIT_SHA=$git_sha
ENV SENTRY_RELEASE=charlie@$git_sha

COPY --from=build /scratch/build/libs/charlie.jar .

# HTTP
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "charlie.jar"]
