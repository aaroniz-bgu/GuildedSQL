plugins {
    id("java")
}

group = "github.aaroniz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.2")
}

tasks.test {
    useJUnitPlatform()
}