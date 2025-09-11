import org.json.JSONObject
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
    object Deps {
        val kotlin_stdlib: String get() = getDependencyInfo("kotlin_stdlib")
        val kotlin_reflect: String get() = getDependencyInfo("kotlin_reflect")
        val org_json: String get() = getDependencyInfo("org_json")
        val commons_io: String get() = getDependencyInfo("commons_io")
        val okhttp: String get() = getDependencyInfo("okhttp")
        val apktool_lib: String get() = getDependencyInfo("apktool_lib")
        val classgraph: String get() = getDependencyInfo("classgraph")
        val jackson_databind: String get() = getDependencyInfo("jackson_databind")
        val jackson_yaml: String get() = getDependencyInfo("jackson_yaml")
        val gplayapi: String get() = getDependencyInfo("gplayapi")
        val shizuku_api: String get() = getDependencyInfo("shizuku_api")
        val shizuku_provider: String get() = getDependencyInfo("shizuku_provider")
        val rootbeer: String get() = getDependencyInfo("rootbeer")
        val m3_preferences: String get() = getDependencyInfo("m3_preferences")

        object Android {
            val appcompat get() = getDependencyInfo("android#appcompat")
            val material get() = getDependencyInfo("android#material")
            val activity get() = getDependencyInfo("android#activity")
            val constraintlayout get() = getDependencyInfo("android#constraintlayout")
            val navigation_fragment get() = getDependencyInfo("android#navigation_fragment")
            val work_manager get() = getDependencyInfo("android#work_manager")
            val navigation_ui get() = getDependencyInfo("android#navigation_ui")
            val preference get() = getDependencyInfo("android#preference")
        }
    }

    private val rootDir: String
        get() = System.getProperty("project.rootDir") ?: throw IllegalStateException("project.rootDir not set!")

    private val libs: JSONObject by lazy {
        val path = Paths.get("$rootDir/config/build-config.json")
        val text = Files.readAllLines(path).joinToString("\n")
        JSONObject(text)
    }

    private fun getDependencyInfo(depName: String): String {
        lateinit var depBlock: JSONObject
        if (depName.startsWith("android#")) {
            depBlock = libs
                .getJSONObject("dependencies")
                .getJSONObject("android").getJSONObject(depName.replace("android#", ""))
        } else {
            depBlock = libs
                .getJSONObject("dependencies").getJSONObject(depName)
        }
        return "${depBlock.getString("package")}:${depBlock.getString("version")}"
    }
}