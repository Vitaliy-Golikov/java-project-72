import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("application")
    id("checkstyle")
    id ("org.sonarqube") version "7.3.1.8318"
    id("jacoco")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val lombokVersion = "1.18.34"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.javalin:javalin-testtools:6.7.0")
    testImplementation("org.assertj:assertj-core:3.27.3")

    testImplementation("com.squareup.okhttp3:mockwebserver:5.3.2")
    testImplementation("com.squareup.okhttp3:okhttp-urlconnection:5.3.2")
    testImplementation("com.squareup.okhttp3:okhttp:5.3.2")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    implementation("io.javalin:javalin:6.1.3")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    implementation("gg.jte:jte:3.1.9")
    implementation("io.javalin:javalin-rendering:6.1.3")

    runtimeOnly("com.h2database:h2:2.2.220")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.zaxxer:HikariCP:5.0.1")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    implementation("com.konghq:unirest-java-core:4.7.4")
    implementation("org.jsoup:jsoup:1.15.3")
}

tasks.check {
    dependsOn(tasks.checkstyleMain, tasks.checkstyleTest)
}

tasks.build {
    dependsOn(tasks.check)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        showStandardStreams = true
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.shadowJar {
    archiveBaseName.set("app")
    archiveClassifier.set("")
    archiveVersion.set("")
    archiveFileName.set("app.jar")
    manifest {
        attributes["Main-Class"] = "hexlet.code.App"
    }
}

application {
    mainClass = "hexlet.code.App"
}

sonar {
    properties {
        property("sonar.projectKey", "Vitaliy-Golikov_java-project-72")
        property("sonar.organization", "vitaliy-golikov")
    }
}