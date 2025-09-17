package instafel.patcher.core.jobs

import brut.directory.ExtFile
import instafel.patcher.core.source.SourceManager
import instafel.patcher.core.source.SourceUtils
import instafel.patcher.core.source.WorkingDir
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.modals.CLIJob
import instafel.patcher.core.utils.Utils
import java.io.File
import kotlin.system.exitProcess

object InitProject: CLIJob {

    override fun runJob(vararg args: Any) {
        val projectDir = args.getOrNull(0) as? File
        val apkFile = args.getOrNull(1) as? File

        if (projectDir !is File || apkFile !is File) {
            Log.severe("Arguments must be Files")
            exitProcess(-1)
        }

        try {
            Env.PROJECT_DIR = WorkingDir.createWorkingDir(projectDir.absolutePath, apkFile.name)
            val sourceManager = SourceManager()
            sourceManager.config = SourceUtils.getDefaultIflConfigDecoder(sourceManager.config)
            sourceManager.config.frameworkDirectory = SourceUtils.getDefaultFrameworkDirectory()
            sourceManager.decompile(ExtFile(
                Utils.mergePaths(apkFile.absolutePath)
            ))

            val dwBin = File(Utils.mergePaths(Env.PROJECT_DIR, "source", "assets", "drawables.bin"))
            if (dwBin.exists()) {
                Log.info("drawables.bin deleted.")
                dwBin.delete()
            }

            sourceManager.setupProjects()
            Log.info("Project successfully created")
        } catch (e: Exception) {
            Log.severe("Failed to run job: ${e.message}")
            e.printStackTrace()
            exitProcess(-1)
        }
    }
}