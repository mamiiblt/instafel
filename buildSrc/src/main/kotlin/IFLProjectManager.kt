import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import modals.BuildConfigFile
import modals.ConfigFile
import org.gradle.api.Project
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
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
    val configObject = JSONObject(Files.readAllLines(configPath).joinToString("\n"))

    fun Project.getCommitHash(): String =
        ByteArrayOutputStream().use { output ->
            exec {
                commandLine("git", "rev-parse", "--short", "HEAD")
                standardOutput = output
            }
            output.toString().trim()
        }


    val Config: ConfigFile by lazy {
        gson.fromJson(configObject.getJSONObject("config").toString(), ConfigFile::class.java)
    }
}

class DependencyDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String {
        val obj = json?.asJsonObject ?: throw JsonParseException("Invalid dependency JSON")
        val pkg = obj["pkg"].asString
        val ver = obj["ver"].asString
        return "$pkg:$ver"
    }
}