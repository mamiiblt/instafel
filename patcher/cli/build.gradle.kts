val libs = rootProject.extra["patcherLibs"] as Map<*, *>
var config = rootProject.extra["instafelConfig"] as Map<*, *>
val projectConfig = config["patcher"] as Map<*, *>
val patcherVersion = projectConfig["patcher_version"] as String
val projectTag = projectConfig["tag"] as String
val commitHash: String by rootProject.extra

plugins {
    kotlin("jvm") version "2.2.10"
    java
    application
    id("com.gradleup.shadow") version "9.0.1"
    id("maven-publish")
}

group = "instafel"
version = "v$patcherVersion"

repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
}

sourceSets {
    main {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
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

application {
    mainClass = "instafel.patcher.MainKt"
}

tasks.shadowJar {
    archiveBaseName = "ifl-patcher"
    archiveClassifier = ""
    destinationDirectory.set(file("${rootProject.rootDir}/patcher/output"))

    exclude("META-INF/maven/**", "LICENSE*", "XMLPULL*")

    manifest {
        attributes(
            "Patcher-Cli-Version" to patcherVersion,
            "Patcher-Cli-Commit" to commitHash,
            "Patcher-Cli-Branch" to "main",
            "Patcher-Cli-Tag" to projectTag
        )
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

tasks.register("build-jar") {
    dependsOn("shadowJar")

    doLast {
        println("All build tasks completed successfully")
    }
}

publishing {
    publications {
        create<MavenPublication>("publish-patcher") {
            groupId = "instafel"
            artifactId = "patcher"
            version = "v$patcherVersion"

            artifact(tasks.shadowJar.get().archiveFile) {
                builtBy(tasks.shadowJar)
            }

            pom {
                name.set("Instafel Patcher CLI ")
                description.set("""
                    # Instafel Patcher

                    To download the **Instafel Patcher** command line executable, click on the **“patcher-v$patcherVersion.jar”** file in the **“Assets”** tab on the side and download it.

                    ## Build Information

                    | Property | Value |
                    | ------------- | ------------- |
                    | Version  | v$patcherVersion  |
                    | Channel  | $projectTag |
                    | Base Commit  | [$commitHash](https://github.com/mamiiblt/instafel/commit/$commitHash)  |
                    | Branch  | [main](https://github.com/mamiiblt/instafel) |

                    ## More Information?

                    For more information about the patcher, please visit [Instafel Wiki](instafel.app/wiki)
                """.trimIndent())
                url.set("https://github.com/mamiiblt/instafel")
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mamiiblt/instafel")
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER")
                password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.register("addPomToJar") {
    dependsOn(tasks.shadowJar)

    doLast {
        val jarFile = tasks.shadowJar.get().archiveFile.get().asFile

        val pomFile = file("build/publications/publish-patcher/pom-default.xml")

        ant.withGroovyBuilder {
            "zip"("destfile" to jarFile, "update" to "true") {
                "zipfileset"("file" to pomFile, "fullpath" to "META-INF/maven/instafel/patcher/pom.xml")
            }
        }
    }
}

tasks.shadowJar {
    finalizedBy("addPomToJar")
}