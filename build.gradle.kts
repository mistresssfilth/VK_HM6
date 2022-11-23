plugins {
    java
    `kotlin-dsl`
    application
    id("nu.studer.jooq") version "8.0" apply false
}

group = "ru.vk.mistressfilth"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
subprojects {
    apply{
        plugin("org.jetbrains.kotlin.jvm")
        plugin("nu.studer.jooq")
    }
    dependencies {
        annotationProcessor("org.projectlombok:lombok:1.18.24")
        compileOnly("org.projectlombok:lombok:1.18.24")

        implementation("org.flywaydb:flyway-core:9.6.0")
        implementation("org.postgresql:postgresql:42.5.0")
        implementation("org.jetbrains:annotations:23.0.0")

        implementation("org.jooq:jooq:3.17.4")
        implementation("org.jooq:jooq-codegen:3.17.4")
        implementation("org.jooq:jooq-meta:3.17.4")

        testImplementation("org.dbunit:dbunit:2.7.3")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }
}