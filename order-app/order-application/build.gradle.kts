import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"

    `maven-publish`
}

group = "com.example.ec"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("com.example.ec:eventsourcing-core:1.0.0")

    implementation("com.example.ec:application-support-stack:1.0.0")

    implementation("com.example.ec:shared:1.0.0")

    implementation("com.example.ec:order-api:1.0.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

allOpen {
    annotation("com.example.ec.applicationsupportstack.kotlinsupport.AllOpen")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.example.ec"
            artifactId = "order-application"
            version = "1.0.0"

            from(components["java"])
        }
    }
}