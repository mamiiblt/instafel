/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.patcher.core.jobs

import instafel.patcher.core.source.WorkingDir
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.modals.CLIJob
import instafel.patcher.core.utils.modals.pojo.BuildInfo
import instafel.patcher.core.utils.modals.pojo.PreviewCreateRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.SFTPClient
import net.schmizz.sshj.sftp.SFTPException
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import okhttp3.OkHttpClient
import okhttp3.Request

object UploadPreview : CLIJob {

    lateinit var F_BUILD_INFO: File
    lateinit var APK_UC: File
    lateinit var APK_C: File
    lateinit var buildInfo: BuildInfo
    lateinit var buildFolder: File
    lateinit var GITHUB_PAT: String
    lateinit var SERVER_SESSION_TOKEN: String
    lateinit var SFTP_HOST: String
    lateinit var SFTP_PORT: String
    lateinit var SFTP_USERNAME: String
    lateinit var SFTP_PASSWORD: String

    val httpClient = OkHttpClient()
    var isProdMode = false

    override fun runJob(vararg args: Any) {
        val workingDir = args.getOrNull(0) as? File
        val patcherVersion = args.getOrNull(1) as? String
        val patcherCommit = args.getOrNull(2) as? String

        if (workingDir !is File || patcherCommit !is String || patcherVersion !is String) {
            Log.severe("Wrong arguments given by CLI")
            exitProcess(-1)
        }

        Env.PROJECT_DIR = WorkingDir.getExistsWorkingDir(workingDir)
        Env.setupConfig()
        Env.setupProject()

        isProdMode = Env.Config.productionMode
        GITHUB_PAT = Env.Config.githubPatToken
        SERVER_SESSION_TOKEN = Env.Config.serverSessionToken
        SFTP_HOST = Env.Config.sftpAddress
        SFTP_PORT = Env.Config.sftpPort
        SFTP_USERNAME = Env.Config.sftpUsername
        SFTP_PASSWORD = Env.Config.sftpPassword

        if (isProdMode) {
            buildFolder = File(Utils.mergePaths(Env.PROJECT_DIR, "build"))
            if (buildFolder.exists()) {
                loadFiles()
                createRelease(patcherVersion, patcherCommit)
            } else {
                Log.severe("/build folder doesn't exist.")
            }
        } else {
            Log.severe("You are not using production env..!:")
        }
    }

    fun loadFiles() {
        Log.info("Loading build files...")

        F_BUILD_INFO = File(Utils.mergePaths(buildFolder.absolutePath, "build_info.json"))
        val jsonStr =
                Files.readAllBytes(Paths.get(F_BUILD_INFO.absolutePath)).toString(Charsets.UTF_8)
        buildInfo = Env.gson.fromJson(jsonStr, BuildInfo::class.java)
        APK_UC =
                File(
                        Utils.mergePaths(
                                buildFolder.absolutePath,
                                buildInfo.fileInfos.unclone.fileName
                        )
                )
        APK_C = File(Utils.mergePaths(buildFolder.absolutePath, buildInfo.fileInfos.clone.fileName))
        Log.info("Build files & properties loaded")
    }

    fun createRelease(patcherVersion: String, patcherCommit: String) {
        Log.info("Uploading build files to CDN...")
        uploadApks(
                host = SFTP_HOST,
                port = SFTP_PORT.toInt(),
                username = SFTP_USERNAME,
                password = SFTP_PASSWORD,
                remoteDirectory =
                        "/mamii-cdn-files/instafel/previews/${buildInfo.patcherData.generationId}",
                uncloneApk = APK_UC,
                cloneApk = APK_C
        )

        Log.info("Creating preview in API side...")
        val body =
                PreviewCreateRequest(
                        patcherVersion = patcherVersion,
                        patcherCommit = patcherCommit,
                        buildInfo = Env.gson.toJson(buildInfo)
                )

        val requestBody = Env.gson.toJson(body).toRequestBody("application/json".toMediaType())

        val request =
                Request.Builder()
                        .url("https://api.mamii.dev/madmin/content/instafel/preview/create")
                        .addHeader("Authorization", "Token $SERVER_SESSION_TOKEN")
                        .post(requestBody)
                        .build()

        httpClient.newCall(request).execute().use { response ->
            val resp = response.body.string()
            Log.info(resp)

            if (!response.isSuccessful) {
                Log.severe("Error while creating preview, aborting.")
                exitProcess(-1)
            }
        }
    }
}

fun uploadApks(
        host: String,
        port: Int = 22,
        username: String,
        password: String,
        remoteDirectory: String,
        uncloneApk: File,
        cloneApk: File
) {
    val ssh = SSHClient()
    ssh.addHostKeyVerifier(PromiscuousVerifier())

    try {
        ssh.connect(host, port)
        ssh.authPassword(username, password)

        ssh.newSFTPClient().use { sftp ->
            var createdDirectory = false

            try {
                if (!exists(sftp, remoteDirectory)) {
                    sftp.mkdir(remoteDirectory)
                    Log.info("Generation preview directory created successfully.")
                    createdDirectory = true
                }

                upload(sftp, uncloneApk, "$remoteDirectory/${uncloneApk.name}", "unclone")
                upload(sftp, cloneApk, "$remoteDirectory/${cloneApk.name}", "clone")
                Log.info("All variants uploaded to cdn successfully.")
            } catch (e: Exception) {
                if (createdDirectory) {
                    runCatching { sftp.rmdir(remoteDirectory) }
                }
                throw e
            }
        }
    } finally {
        if (ssh.isConnected) {
            ssh.disconnect()
        }
        ssh.close()
    }
}

private fun exists(sftp: SFTPClient, path: String): Boolean =
        try {
            sftp.stat(path)
            true
        } catch (_: SFTPException) {
            false
        }

private fun upload(sftp: SFTPClient, local: File, remote: String, apkType: String) {
    require(local.exists()) { "File does not exist: ${local.absolutePath}" }
    Log.info("Uplading $apkType variant into CDN..")
    sftp.put(local.absolutePath, remote)
    Log.info("$apkType variant uploaded to CDN successfully.")
}
