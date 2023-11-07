FROM eclipse-temurin:21-jdk-alpine@sha256:001dfe1c179b3f315bd6549ad1fe94fd7204984319bd3c0f3b385b5188cb18b8 as build
WORKDIR /scratch

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
COPY vendored/fmjdbc.jar .

RUN ./mvnw install:install-file -Dfile="fmjdbc.jar" -DgroupId="com.filemaker" -DartifactId="jdbc" -Dversion="18.0.1" -Dpackaging="jar" -DgeneratePom="true"

RUN ./mvnw install -DskipTests

FROM eclipse-temurin:21-jre-alpine@sha256:2a4755c16fe3390e6a89daed9adfc6d9dc7be116dfce84497cf84f761b973311

COPY --from=build /scratch/target/charlie-0.0.1-SNAPSHOT.jar .

# HTTP
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "charlie-0.0.1-SNAPSHOT.jar"]
