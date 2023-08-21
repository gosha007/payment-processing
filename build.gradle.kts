plugins {
    java
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.2"
    id("org.owasp.dependencycheck") version "8.3.1"
}

group = "lt.paymentprocessing"
version = "0.0.1"
description = "Payment Processing"

java {
    sourceCompatibility = JavaVersion.VERSION_20
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    implementation("org.postgresql:postgresql")
    implementation("org.modelmapper:modelmapper:3.1.1")
    implementation(platform("org.zalando:logbook-bom:3.3.0"))
    implementation("org.zalando:logbook-spring-boot-starter")
    implementation("org.zalando:logbook-netty")
    compileOnly("org.projectlombok:lombok:1.18.28")
    runtimeOnly("com.h2database:h2:2.2.220")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
