import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.31"

    id("com.google.cloud.tools.jib") version "3.1.4"
}

group = "com.example.ec"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.example.ec:event-sourcing:1.0.0")

    implementation("com.example.ec:application-support-stack:1.0.0")
    implementation("com.azure.spring:azure-spring-boot-starter-cosmos:3.9.0")

    implementation("com.example.ec:shared:1.0.0")

    implementation("com.example.ec:order-api:1.0.0")

    implementation("com.example.ec:billing-api:1.0.0")

    implementation("com.example.ec:jms-messaging:1.0.0")
    implementation("com.azure.spring:azure-spring-boot-starter-servicebus-jms:3.9.0")

    implementation("com.example.ec:mq:1.0.0")
    implementation("com.google.code.gson:gson:2.8.8")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("com.microsoft.sqlserver:mssql-jdbc:9.4.0.jre11")
    runtimeOnly("com.h2database:h2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

allOpen {
    annotation("com.example.ec.applicationsupportstack.kotlinsupport.AllOpen")
}

jib {
    to {
        val azurePrivateRegistryUrlPrefix = findProperty("azure.privateregistry.url.prefix")
        image = "$azurePrivateRegistryUrlPrefix.azurecr.io/ec-order"
        auth {
            val azurePrivateRegistryUserName = findProperty("azure.privateregistry.username")
            val azurePrivateRegistryPassword = findProperty("azure.privateregistry.password")
            username = "$azurePrivateRegistryUserName"
            password = "$azurePrivateRegistryPassword"
        }
    }
}