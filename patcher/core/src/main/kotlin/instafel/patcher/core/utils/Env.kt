package instafel.patcher.core.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

object Env {
    val USER_DIR: String = System.getProperty("user.dir")
    lateinit var PROJECT_DIR: String
    val INSTAFEL_LOCALES = listOf<String>("tr", "de", "el", "fr", "hi", "hu", "pt", "es", "az", "pl", "in")
    val SEPARATOR_LINE = "---------------------------"

    class Project {
        enum class Keys() {
            API_BASE,
            INSTAGRAM_VERSION,
            INSTAGRAM_VERSION_CODE,
            GENID,
            INSTAFEL_VERSION,
            APPLIED_PATCHES,
            IFL_SOURCES_FOLDER,
            P_VCLASS_PATH
        }

        val UPDATE_STR = "Update Project Environment File"
        lateinit var file: File
        lateinit var propertyManager: PropertyManager

        fun setupProject() {
            try {
                file = File(Utils.mergePaths(Env.PROJECT_DIR, "env.properties"))
                propertyManager = PropertyManager(file)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.severe("An error occurred while loading configuration file.")
                exitProcess(-1)
            }
        }

        fun saveProperties() = propertyManager.save(UPDATE_STR)
        fun getString(key: Keys, defaultValue: String): String =
            propertyManager.getString(key.toString(), defaultValue)
        fun getInteger(key: Keys, defaultValue: Int): Int =
            propertyManager.getInteger(key.toString(), defaultValue)
        fun getBoolean(key: Keys, defaultValue: Boolean): Boolean =
            propertyManager.getBoolean(key.toString(), defaultValue)
        fun setString(key: Keys, value: String) = propertyManager.addString(key.toString(), value)
        fun setInteger(key: Keys, value: Int) = propertyManager.addInteger(key.toString(), value)
        fun setBoolean(key: Keys, value: Boolean) = propertyManager.addBoolean(key.toString(), value)

        fun createDefaultProjectFile() {
            propertyManager.addString(Keys.API_BASE.toString(), "api.mamii.me/ifl")
            propertyManager.addString(Keys.APPLIED_PATCHES.toString(), "")
            setIgVerCodeAndVersion()
            saveProperties()
        }

        fun setIgVerCodeAndVersion() {
            val mapper = ObjectMapper(YAMLFactory())
            val root = mapper.readTree(File(
                Utils.mergePaths(Env.PROJECT_DIR, "sources", "apktool.yml")
            )).get("versionInfo")

            propertyManager.addString(Keys.INSTAGRAM_VERSION.toString(), root.get("versionName").asText())
            propertyManager.addString(Keys.INSTAGRAM_VERSION_CODE.toString(), root.get("versionCode").asText())
        }
    }

    class Config {
        enum class Keys() {
            manifest_version,
            source_dir,
            use_external_ifl_source,
            prod_mode,
            manager_token,
            github_pat
        }

        val UPDATE_STR = "Updated Config"
        lateinit var file: File
        lateinit var propertyManager: PropertyManager

        fun setupConfig() {
            try {
                file = File(Utils.mergePaths(Env.PROJECT_DIR, "config.properties"))
                propertyManager = PropertyManager(file)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.severe("An error occurred while loading configuration file.")
                exitProcess(-1)
            }
        }

        fun saveProperties() = propertyManager.save(UPDATE_STR)

        fun getString(key: Keys, defaultValue: String): String =
            propertyManager.getString(key.toString(), defaultValue)
        fun getInteger(key: Keys, defaultValue: Int): Int =
            propertyManager.getInteger(key.toString(), defaultValue)
        fun getBoolean(key: Keys, defaultValue: Boolean): Boolean =
            propertyManager.getBoolean(key.toString(), defaultValue)
        fun setString(key: Keys, value: String) = propertyManager.addString(key.toString(), value)
        fun setInteger(key: Keys, value: Int) = propertyManager.addInteger(key.toString(), value)
        fun setBoolean(key: Keys, value: Boolean) = propertyManager.addBoolean(key.toString(), value)

        @Throws(IOException::class)
        fun createDefaultConfigFile() {
            propertyManager.addInteger(Keys.manifest_version.toString(), 1)
            propertyManager.addString(Keys.source_dir.toString(), "/sources")
            propertyManager.addBoolean(Keys.use_external_ifl_source.toString(), false)
            propertyManager.addBoolean(Keys.prod_mode.toString(), false)
            propertyManager.addString(Keys.manager_token.toString(), "not_needed")
            propertyManager.addString(Keys.github_pat.toString(), "null")
            saveProperties()
        }
    }
}