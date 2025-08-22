package instafel.patcher.core.source

import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.system.exitProcess

object WorkingDir {

    fun createWorkingDir(projectDir: String, igApkFileName: String): String {
        val projFolderName = igApkFileName.replace(".apk", "")
        val dirPath = File(Utils.mergePaths(projectDir, projFolderName))
        if (dirPath.exists()) {
            Log.severe("Working directory for this apk is already exists, delete it or continue from the project.")
            exitProcess(-1)
        } else {
            FileUtils.forceMkdir(dirPath)
            Log.info("Project working directory successfully created.")
            return dirPath.absolutePath
        }
    }

    fun getExistsWorkingDir(folderDir: File): String {
        if (folderDir.isDirectory) {
            if (folderDir.exists()) {
                return folderDir.absolutePath
            } else {
                Log.severe("Working directory not exists, please create it or use different working dir.")
                exitProcess(-1)
            }
        } else {
            Log.severe("Its's not an directory bro...")
            exitProcess(-1)
        }
    }
}