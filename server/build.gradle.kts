// Версии и зависимости теперь вынесены в версионный каталог (libs.versions.toml)

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.tikhon"
version = "0.0.2"
application {
    mainClass.set("com.tikhon.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}



dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.cio)
    implementation(libs.logback.classic)
    implementation(libs.janino)
    implementation(libs.postgresql)
    implementation(libs.ktorm.core)
    implementation(libs.kotlin.telegram.bot)
    implementation(libs.kotlinx.serialization.core)
//    testImplementation(libs.ktor.server.tests.jvm) // TODO Для теста еще нет соответствующей версии
    testImplementation(libs.kotlin.test.junit)
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}
