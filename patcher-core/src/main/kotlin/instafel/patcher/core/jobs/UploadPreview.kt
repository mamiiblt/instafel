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
import instafel.patcher.core.utils.modals.CLIJob
import instafel.patcher.core.utils.Utils
import instafel.patcher.core.utils.modals.pojo.BuildInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

object UploadPreview: CLIJob {

    lateinit var F_BUILD_INFO: File
    lateinit var APK_UC: File
    lateinit var APK_C: File
    lateinit var buildInfo: BuildInfo
    lateinit var buildFolder: File
    lateinit var GITHUB_PAT: String
    lateinit var SERVER_SESSION_TOKEN: String
    val httpClient = OkHttpClient.Builder()
        .connectTimeout(0, TimeUnit.MILLISECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(0, TimeUnit.MILLISECONDS)
        .callTimeout(0, TimeUnit.MILLISECONDS)
        .build()
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
        val jsonStr = Files.readAllBytes(Paths.get(F_BUILD_INFO.absolutePath)).toString(Charsets.UTF_8)
        buildInfo = Env.gson.fromJson(jsonStr, BuildInfo::class.java)
        APK_UC = File(Utils.mergePaths(buildFolder.absolutePath, buildInfo.fileInfos.unclone.fileName))
        APK_C = File(Utils.mergePaths(buildFolder.absolutePath, buildInfo.fileInfos.clone.fileName))
        Log.info("Build files & properties loaded")
    }

    fun createRelease(patcherVersion: String, patcherCommit: String) {
        Log.info("Adding APK(s) into FormBody for request. It may be take long time.")
        val requestBody =
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("patcher_version", patcherVersion)
                .addFormDataPart("patcher_commit", patcherCommit)
                .addFormDataPart("build_info", Env.gson.toJson(buildInfo))
                .addFormDataPart("files",
                    APK_UC.name,
                    APK_UC.asRequestBody(
                        "application/vnd.android.package-archive".toMediaType()
                    )
                )
                .addFormDataPart(
                    "files",
                    APK_C.name,
                    APK_C.asRequestBody(
                        "application/vnd.android.package-archive".toMediaType()
                    )
                )
                .build()

        val request =
            Request.Builder()
                .url("https://api.mamii.dev/madmin/content/instafel/preview/create")
                .addHeader("Authorization", "Token $SERVER_SESSION_TOKEN")
                .post(requestBody)
                .build()

        httpClient.newCall(request).execute().use { response ->
            val resp = JSONObject(response.body.string())
            Log.info(resp.getJSONObject("data").getString("msg"))

            if (!resp.getString("status").equals("SUCCESS")) {
                Log.severe("Error while creating preview, aborting.")
                exitProcess(-1)
            }
        }
    }
}