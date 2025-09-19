package instafel.patcher.core.source

import brut.androlib.Config
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.OSDetector
import instafel.patcher.core.utils.Utils
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

object SourceUtils {
    fun getDefaultIflConfigDecoder(config: Config): Config {
        config.isBaksmaliDebugMode = false
        config.jobs = Utils.getSuggestedThreadCount()
        return config
    }

    fun getDefaultIflConfigBuilder(config: Config): Config {
        config.isBaksmaliDebugMode = false
        config.jobs = Utils.getSuggestedThreadCount()
        config.aaptVersion = 1 // aapt2 is buggy for Instagram in Apktool now.
        return config
    }

    fun getDefaultFrameworkDirectory(): String {
        val base = File(System.getProperty("user.home"))
        val path: Path = when {
            OSDetector.isMac() -> Paths.get(base.absolutePath, "Library", "ipatcher", "framework")
            OSDetector.isWin() -> Paths.get(base.absolutePath, "AppData", "Local", "ipatcher", "framework")
            else -> {
                val xdgDataFolder = System.getenv("XDG_DATA_HOME")
                if (xdgDataFolder.isNullOrBlank()) {
                    Paths.get(base.absolutePath, ".local", "share", "ipatcher", "framework")
                } else {
                    Paths.get(xdgDataFolder, "ipatcher", "framework")
                }
            }
        }
        return path.toString()
    }

    @Throws(IOException::class)
    fun createTempSourceDir(igApkFileName: String): String {
        val folderName = igApkFileName.replace(".apk", "") + "_temp"
        val dirPath = File(Utils.mergePaths(Env.USER_DIR, folderName))

        return (if (dirPath.exists()) {
            exitProcess(-1)
        } else {
            FileUtils.forceMkdir(dirPath)
            Log.info("Temp folder for parsing source successfully created.")
            dirPath.absolutePath
        }) as String
    }
}