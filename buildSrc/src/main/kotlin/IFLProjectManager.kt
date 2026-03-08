import modals.ConfigFile
import org.gradle.api.Project
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths

/*
    Kotlin plugin should be updated manually from
        - settings.gradle.kts
        - buildSrc/build.gradle.kts
        - config/build-config.json
    A little note for me...
*/
object IFLProjectManager {
    private val rootDir: String
        get() = System.getProperty("project.rootDir") ?: throw IllegalStateException("project.rootDir not set!")

    val configPath = Paths.get("$rootDir/.config/ifl_config.json")

    fun Project.getCommitHash(): String =
        ByteArrayOutputStream().use { output ->
            exec {
                commandLine("git", "rev-parse", "--short", "HEAD")
                standardOutput = output
            }
            output.toString().trim()
        }


    val Config: ConfigFile by lazy {
        val configObject = JSONObject(Files.readAllLines(configPath).joinToString("\n"))
        gson.fromJson(configObject.toString(), ConfigFile::class.java)
    }
}