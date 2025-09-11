plugins {
    kotlin("jvm")
    java
    application
    id("com.gradleup.shadow")
}

var config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config[project.name] as Map<*, *>
val projectVersion = projectConfig["version"] as String
val projectTag = projectConfig["tag"] as String 

val commitHash: String by rootProject.extra

group = "gplayapi"
version = "v$projectVersion-$projectTag"

dependencies {
    implementation(IFLProjectManager.Deps.kotlin_stdlib)
    implementation(IFLProjectManager.Deps.gplayapi)
    implementation(IFLProjectManager.Deps.org_json)
    implementation(IFLProjectManager.Deps.okhttp)
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