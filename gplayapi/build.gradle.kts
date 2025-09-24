import IFLProjectManager.Config

plugins {
    java
    application
    alias(libs.plugins.shadowjar)
    alias(libs.plugins.kotlin.jvm)
}

group = "gplayapi"
version = "v${Config.gplayapi.version}-${Config.gplayapi.tag}"

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.gplayapi)
    implementation(libs.org.json)
    implementation(libs.okhttp)
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