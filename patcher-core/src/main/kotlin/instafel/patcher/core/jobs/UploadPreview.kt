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
import okhttp3.OkHttpClient
import okhttp3.Request
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.transfer.s3.S3TransferManager
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest
import software.amazon.awssdk.transfer.s3.model.UploadRequest
import java.net.URI

object UploadPreview : CLIJob {

    lateinit var F_BUILD_INFO: File
    lateinit var APK_UC: File
    lateinit var APK_C: File
    lateinit var buildInfo: BuildInfo
    lateinit var buildFolder: File
    lateinit var GITHUB_PAT: String
    lateinit var SERVER_SESSION_TOKEN: String
    lateinit var S3_ACCESS_KEY_ID: String
    lateinit var S3_SECRET_KEY: String

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
        S3_ACCESS_KEY_ID = Env.Config.s3AccessKeyId
        S3_SECRET_KEY = Env.Config.s3SecretKey

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

        val success = uploadBuildArtifactsIntoCdn(
            buildInfo.patcherData.generationId,
            listOf(APK_C, APK_UC)
        )

        if (!success) {
            Log.severe("Upload failed.")
            exitProcess(-1)
        }

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
                .url("http://localhost:3001/madmin/content/instafel/preview/create")
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

    fun uploadBuildArtifactsIntoCdn(
        generationId: String,
        files: List<File>
    ): Boolean {
        return try {
            val s3 = S3AsyncClient.builder()
                .endpointOverride(URI.create("http://195.85.201.93:9000"))
                .region(Region.of("tr-west-1"))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                            S3_ACCESS_KEY_ID,
                            S3_SECRET_KEY
                        )
                    )
                )
                .serviceConfiguration(
                    S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
                .build()

            for (file in files) {
                Log.info("Uploading '${file.name}' into bucket...")
                val fileKey = "previews/$generationId/${file.name}"
                val start = System.currentTimeMillis()

                val transferManager = S3TransferManager.builder()
                    .s3Client(s3)
                    .build()
                val upload = transferManager.uploadFile(
                    UploadFileRequest.builder()
                        .putObjectRequest(
                            PutObjectRequest.builder()
                                .bucket("instafel")
                                .key(fileKey)
                                .build()
                        )
                        .source(Paths.get(file.absolutePath))

                        .build()
                )

                upload.completionFuture().join()

                val elapsed = System.currentTimeMillis() - start

                val minutes = elapsed / 60_000
                val seconds = (elapsed % 60_000) / 1_000
                val millis = elapsed % 1_000

                Log.info("File '${file.name}' uploaded successfully in ${minutes}m ${seconds}s ${millis}ms")
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()

            false
        }
    }
}