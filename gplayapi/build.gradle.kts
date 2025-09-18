import IFLProjectManager.Config
import IFLProjectManager.BuildConfig

plugins {
    kotlin("jvm")
    java
    application
    id("com.gradleup.shadow")
}

group = "gplayapi"
version = "v${Config.gplayapi.version}-${Config.gplayapi.tag}"

dependencies {
    implementation(BuildConfig.kotlin_stdlib)
    implementation(BuildConfig.gplayapi)
    implementation(BuildConfig.org_json)
    implementation(BuildConfig.okhttp)
}

application {
    mainClass = "instafel.gplayapi.MainKt"
}

tasks.shadowJar {
    archiveBaseName = "ifl-gplayapi"
    archiveClassifier = ""
    destinationDirectory.set(file("${rootProject.rootDir}/.output"))

    doLast {
        println("JAR generated.")
    }
}

tasks.register("build-jar") {
    group = "instafel"
    description = "Builds JAR file"

    dependsOn("shadowJar")

    doLast {
        println("All tasks completed successfully")
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}