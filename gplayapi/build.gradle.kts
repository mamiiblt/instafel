plugins {
    kotlin("jvm") version "2.2.0"
    java
    application
    id("com.gradleup.shadow") version "8.3.6"
}

/************************************************/
/* BUILD CONFIG INITIALIZATION PASHE */

var config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config[project.name] as Map<*, *>
val projectVersion = projectConfig["version"] as String
val projectTag = projectConfig["tag"] as String 

val commitHash: String by rootProject.extra

group = "gplayapi"
version = "v$projectVersion-$commitHash-$projectTag"

/************************************************/

sourceSets {
    main {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.gitlab.AuroraOSS:gplayapi:0e224071")
    implementation("org.json:json:20250517")
    implementation("com.squareup.okhttp3:okhttp:5.1.0")
}

application {
    mainClass = "instafel.gplayapi.MainKt"
}

tasks.register("clear-cache") {
    val filesToDelete = listOf(
        file("${project.projectDir}/bin"),
        file("${project.projectDir}/build"),
    )

    delete(filesToDelete)
    doLast {
        println("Cache successfully deleted.")
    }
}

tasks.shadowJar {
    archiveBaseName = "ifl-gplayapi"
    archiveClassifier = ""
    destinationDirectory.set(file("${project.projectDir}/output"))

    doLast {
        println("JAR generated.")
    }

    mustRunAfter("clear-cache")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.register("build-jar") {
    dependsOn("clear-cache", "shadowJar")

    doLast {
        delete(file("${project.projectDir}/build"))
        delete(file("${project.projectDir}/bin"))
        println("Temp build caches cleared.")
        println("All tasks completed succesfully")
    }
}

tasks.test {
    useJUnitPlatform()
}
