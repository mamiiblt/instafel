plugins {
    kotlin("jvm") version "2.2.0"
    java
    `java-library`
}

val libs = rootProject.extra["patcherLibs"] as Map<*, *>
val config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config["patcher"] as Map<*, *>
val coreSupportedVersion = projectConfig["core_supported_version"] as String
val commitHash: String by rootProject.extra

group = "patcher"
version = "$commitHash"

apply(from = "publish.gradle.kts")

sourceSets {
    main {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
}

repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs["kotlin-reflect"]!!)
    implementation(libs["org-json"]!!)
    implementation(libs["commons-io"]!!)
    implementation(libs["okhttp"]!!)
    implementation(libs["apktool-lib"]!!)
    implementation(libs["classgraph"]!!)
    implementation(libs["jackson-databind"]!!)
    implementation(libs["jackson-yaml"]!!)
}

tasks.named<Jar>("jar") {
    archiveBaseName.set("ifl-pcore")
    archiveClassifier.set("")
    destinationDirectory.set(file("${rootProject.rootDir}/patcher/output"))
    manifest {
        attributes(
            "Patcher-Core-Commit" to commitHash,
            "Patcher-Core-Supported-Version" to coreSupportedVersion,
            "Patcher-Core-Branch" to "main"
        )
    }
}


tasks.register("build-jar") {
    dependsOn("jar")
    doLast {
        println("JAR succesfully generated.")
    }
}
