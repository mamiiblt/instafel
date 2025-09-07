package instafel.patcher.core.source

import brut.androlib.ApkBuilder
import brut.androlib.ApkDecoder
import brut.androlib.Config
import brut.androlib.exceptions.AndrolibException
import brut.directory.ExtFile
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

class SourceManager(var config: Config = Config()) {

    @Throws(IOException::class, AndrolibException::class)
    fun decompile(apkFile: ExtFile) {
        Log.info("Decompiling Instagram APK...")
        val apkDecoder = ApkDecoder(apkFile, config)
        apkDecoder.decode(File(Utils.mergePaths(Env.PROJECT_DIR, "sources")))
        Log.info("APK decompiled successfully")
    }

    @Throws(IOException::class, AndrolibException::class)
    fun build(fileName: String) {
        Log.info("Building APK")
        val buildDir = File(Utils.mergePaths(Env.PROJECT_DIR, "build"))
        if (!buildDir.exists()) {
            FileUtils.forceMkdir(buildDir)
        }
        val apkBuilder = ApkBuilder(
            ExtFile(
                Utils.mergePaths(Env.PROJECT_DIR, "sources")
            ), config
        )
        apkBuilder.build(File(Utils.mergePaths(buildDir.absolutePath, fileName)))
        Log.info("APK built successfully")
        Log.info("Saved as build/$fileName")
    }

    @Throws(IOException::class)
    fun createConfigAndEnvFile() {
        val pEnvFile = File(Utils.mergePaths(Env.PROJECT_DIR, "env.properties"))
        pEnvFile.createNewFile()
        Env.Project.setupProject()
        Env.Project.createDefaultProjectFile()
        Env.Project.saveProperties()

        val pConfigFile = File(Utils.mergePaths(Env.PROJECT_DIR, "config.properties"))
        pConfigFile.createNewFile()
        Env.Config.setupConfig()
        Env.Config.createDefaultConfigFile()
        Env.Config.saveProperties()
    }
}