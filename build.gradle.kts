plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
}

val main = "com.github.raink1208.radiobot.MainKt"
group = "com.github.raink1208"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven(uri("https://jitpack.io"))
    maven(uri("https://m2.dv8tion.net/releases"))
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    implementation("net.dv8tion:JDA:5.0.0-alpha.11")
    implementation("com.github.walkyst:lavaplayer:1.3.77")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("org.yaml:snakeyaml:1.30")
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
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes("Main-Class" to main)
    }
    from(configurations.runtimeClasspath.get()
        .filter { !it.name.endsWith("pom") }
        .onEach { println("add from dependencies:" + it.name) }
        .map { if (it.isDirectory) it else zipTree(it) }
    )
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add form sources: "+it.name) }
    from(sourcesMain.output)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}