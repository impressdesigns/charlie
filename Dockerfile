FROM eclipse-temurin:21-jdk-alpine@sha256:e23a0ed210af78c9e38d97c75be490b1e7bcf274d933d3dd09c7b45276024988 as build

ARG git_sha="development"
ENV GIT_SHA=$git_sha

WORKDIR /scratch

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew /scratch/
COPY vendored vendored
COPY src src

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine@sha256:f153dfdd10e9846963676aa6ea8b8630f150a63c8e5fe127c93e98eb10b86766

ARG git_sha="development"
ENV GIT_SHA=$git_sha
ENV SENTRY_RELEASE=charlie@$git_sha

COPY --from=build /scratch/build/libs/charlie.jar .

# HTTP
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "charlie.jar"]
