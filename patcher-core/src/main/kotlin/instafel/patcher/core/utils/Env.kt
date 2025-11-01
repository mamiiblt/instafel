package instafel.patcher.core.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import instafel.patcher.core.utils.modals.pojo.ConfigPOJO
import instafel.patcher.core.utils.modals.pojo.ProjectPOJO
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.system.exitProcess

object Env {
    val USER_DIR: String = System.getProperty("user.dir")
    lateinit var PROJECT_DIR: String
    val INSTAFEL_LOCALES = listOf("tr", "de", "el", "fr", "hi", "hu", "pt", "es", "az", "pl", "in", "it")
    const val SEPARATOR_LINE = "---------------------------"
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    lateinit var fileConf: File
    lateinit var fileProj: File
    var Config = ConfigPOJO()
        set(value) {
            field = value
            saveConfig()
        }

    var Project = ProjectPOJO()
        set(value) {
            field = value
            saveProject()
        }

    fun setupConfig() {
        try {
            fileConf = File(Utils.mergePaths(PROJECT_DIR, "config.json"))
            if (!fileConf.exists()) {
                Config = ConfigPOJO(
                    manifestVersion = 1,
                    productionMode = false,
                    managerToken = "",
                    githubPatToken = "",
                )
            }
            Config = gson.fromJson(fileConf.readText(), ConfigPOJO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while loading config.json file.")
            exitProcess(-1)
        }
    }

    fun setupProject() {
        try {
            fileProj = File(Utils.mergePaths(PROJECT_DIR, "project.json"))
            if (!fileProj.exists()) {
                Project = ProjectPOJO(
                    apiBase = "api.instafel.app",
                    igVersion = getIgVerCodeAndVersion().split("#")[0],
                    igVersionCode = getIgVerCodeAndVersion().split("#")[1]
                )
            }
            Project = gson.fromJson(fileProj.readText(), ProjectPOJO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("An error occurred while loading project.json file.")
            exitProcess(-1)
        }
    }

    fun saveConfig() = FileUtils.writeStringToFile(fileConf, gson.toJson(Config), Charsets.UTF_8)
    fun saveProject() = FileUtils.writeStringToFile(fileProj, gson.toJson(Project), Charsets.UTF_8)

    fun getIgVerCodeAndVersion(): String {
        val mapper = ObjectMapper(YAMLFactory())
        val root = mapper.readTree(File(
            Utils.mergePaths(PROJECT_DIR, "sources", "apktool.yml")
        )).get("versionInfo")

        return "${root.get("versionName").asText()}#${root.get("versionCode").asText()}"
    }
}