import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
    id("io.sentry.jvm.gradle") version "4.10.0"
    id("org.jetbrains.kotlinx.kover") version "0.8.2"
}

group = "com.impressdesigns"
version = "6.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation(files("vendored/fmjdbc.jar"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.sentry:sentry-spring-boot-starter-jakarta:7.12.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    archiveFileName.set("${project.name}.jar")
}

sentry {
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
    org.set("impressdesigns")
    projectName.set("charlie")
    includeSourceContext.set(true)
}
