package instafel.patcher.cli.utils

import me.mamiiblt.instafel.patcher.cli.utils.Utils
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.jar.Manifest
import kotlin.system.exitProcess

object Utils {
    lateinit var PROP_CLI_VERSION: String
    lateinit var PROP_CLI_COMMIT_HASH: String
    lateinit var PROP_CLI_PROJECT_TAG: String
    lateinit var PROP_CLI_PROJECT_BRANCH: String
    lateinit var PROP_CORE_COMMIT: String
    lateinit var PROP_CORE_BRANCH: String
    val USER_DIR: String = System.getProperty("user.dir")

    fun readPatcherProps() {
        try {
            val stream = Utils::class.java.getResourceAsStream("/META-INF/MANIFEST.MF")
            val manifest = Manifest(stream)
            val attr = manifest.mainAttributes

            PROP_CLI_VERSION = attr.getValue("Patcher-Cli-Version")
            PROP_CLI_COMMIT_HASH = attr.getValue("Patcher-Cli-Commit")
            PROP_CLI_PROJECT_TAG = attr.getValue("Patcher-Cli-Tag")
            PROP_CLI_PROJECT_BRANCH = attr.getValue("Patcher-Cli-Branch")
        } catch (e: Exception) {
            e.printStackTrace();
            Log.severe("Error while organizing environment")
            exitProcess(-1)
        }
    }

    fun printPatcherHeader() {
        println("Instafel Patcher v$PROP_CLI_VERSION (with Kotlin)")
        println("by mamiiblt")
        println("")
    }

    fun getPatcherFolder(): String {
        val OS = System.getProperty("os.name").lowercase(Locale.getDefault())

        val base = File(System.getProperty("user.home"))
        val path: Path
        if (OS.contains("mac")) {
            path = Paths.get(base.absolutePath, "Library", "ipatcher")
        } else if (OS.contains("win")) {
            path = Paths.get(base.absolutePath, "AppData", "Local", "ipatcher")
        } else {
            val xdgDataFolder = System.getenv("XDG_DATA_HOME")
            path = when (xdgDataFolder) {
                null -> Paths.get(base.absolutePath, ".local", "share", "ipatcher")
                else -> Paths.get(xdgDataFolder, "ipatcher", "framework")
            }
        }

        return path.toString()
    }
}