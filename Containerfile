FROM eclipse-temurin:21-jdk-alpine@sha256:001dfe1c179b3f315bd6549ad1fe94fd7204984319bd3c0f3b385b5188cb18b8 as build

ARG git_sha="development"
ENV GIT_SHA=$git_sha
ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN

WORKDIR /scratch

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew /scratch/
COPY vendored/fmjdbc.jar .
COPY src src

RUN ./gradlew build

FROM eclipse-temurin:21-jre-alpine@sha256:2a4755c16fe3390e6a89daed9adfc6d9dc7be116dfce84497cf84f761b973311

COPY --from=build /scratch/build/libs/charlie.jar .

# HTTP
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "charlie.jar"]
