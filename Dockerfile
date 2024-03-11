FROM eclipse-temurin:21-jdk-alpine@sha256:82698e23d15ada036bc176f6fb210401e0679cd0a4b1e71d05e7329982d6062c as build

ARG git_sha="development"
ENV GIT_SHA=$git_sha

WORKDIR /scratch

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew /scratch/
COPY vendored vendored
COPY src src

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre-alpine@sha256:8535a86d923e1e5ce62c67c53cf7699567ed228b23a7e52cd0c0ad68aafd1cd2

ARG git_sha="development"
ENV GIT_SHA=$git_sha
ENV SENTRY_RELEASE=charlie@$git_sha

COPY --from=build /scratch/build/libs/charlie.jar .

# HTTP
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "charlie.jar"]
