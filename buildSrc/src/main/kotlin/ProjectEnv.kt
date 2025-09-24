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