val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val ktom_version: String by project
val postgresql_version: String by project
val kotlinx_serialization_version: String by project
val kotlin_telegram_bot: String by project

plugins {
    kotlin("jvm") version "1.7.22"
    id("io.ktor.plugin") version "2.2.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.22"
}

group = "com.tikhon"
version = "0.0.1"
application {
    mainClass.set("com.tikhon.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cio-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.postgresql:postgresql:$postgresql_version")
    implementation("org.ktorm:ktorm-core:$ktom_version")
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:$kotlin_telegram_bot")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinx_serialization_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}
