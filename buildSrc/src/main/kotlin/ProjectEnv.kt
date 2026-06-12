/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

import org.gradle.api.Project
import java.nio.file.Paths
import java.util.Properties

fun Project.getInstafelEnvProperty(propName: String): String {
    val envFile = Paths.get(rootProject.projectDir.toString(), "env.properties").toFile()
    if (!envFile.exists()) {
        error("Environment file doesn't exist")
    }

    val props = Properties()
    envFile.inputStream().use { props.load(it) }
    return props.getProperty(propName)
}