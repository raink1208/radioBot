plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

val main = "com.github.raink1208.radioBot.MainKt"
group = "com.github.raink1208"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven(uri("https://jitpack.io"))
    maven(uri("https://m2.dv8tion.net/releases"))
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    implementation("net.dv8tion:JDA:4.4.0_350")
    implementation("com.sedmelluq:lavaplayer:1.3.75")
    implementation("ch.qos.logback:logback-classic:1.2.10")
    implementation("org.yaml:snakeyaml:1.29")
    implementation("com.google.api-client:google-api-client:1.32.2")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.32.1")
    implementation("com.google.apis:google-api-services-youtube:v3-rev20210915-1.32.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    archiveFileName.set("${project.name}.jar")
    manifest {
        attributes("Main-Class" to main)
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}