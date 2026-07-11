plugins {
    id("java")
    id("application")  // Требуется по заданию
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Javalin - последняя версия, не ниже 5.6
    implementation("io.javalin:javalin:6.1.3")

    // Логгер
    implementation("org.slf4j:slf4j-simple:2.0.9")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("hexlet.code.App")  // Точка входа
}

tasks.test {
    useJUnitPlatform()
}