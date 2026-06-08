/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

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
    implementation(libs.gson)
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
    group = "ifl-gplayapi"
    description = "Builds JAR file"

    dependsOn("shadowJar")

    doLast {
        println("All tasks completed successfully")
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}