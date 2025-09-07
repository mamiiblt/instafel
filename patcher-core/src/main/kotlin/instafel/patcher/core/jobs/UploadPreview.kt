package instafel.patcher.core.jobs

import instafel.patcher.core.source.WorkingDir
import instafel.patcher.core.utils.Env
import instafel.patcher.core.utils.Log
import instafel.patcher.core.utils.modals.CLIJob
import instafel.patcher.core.utils.Utils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

object UploadPreview: CLIJob {

    lateinit var F_BUILD_INFO: File
    lateinit var APK_UC: File
    lateinit var APK_C: File
    lateinit var buildInfo: JSONObject
    lateinit var buildFolder: File
    lateinit var GEN_ID: String
    lateinit var GITHUB_PAT: String
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
        Env.Config.setupConfig()
        Env.Project.setupProject()

        isProdMode = Env.Config.getBoolean(Env.Config.Keys.prod_mode, false);
        GITHUB_PAT = Env.Config.getString(Env.Config.Keys.github_pat, "NONE");

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
        buildInfo = JSONObject(jsonStr)
        val fnames = buildInfo.getJSONObject("fnames")
        APK_UC = File(Utils.mergePaths(buildFolder.absolutePath, fnames.getString("unclone")))
        APK_C = File(Utils.mergePaths(buildFolder.absolutePath, fnames.getString("clone")))
        val patcherData = buildInfo.getJSONObject("patcher_data")
        GEN_ID = patcherData.getJSONObject("ifl").getString("gen_id")
        Log.info("Build files & properties loaded")
    }

    fun createRelease(patcherVersion: String, patcherCommit: String) {
        val pData = buildInfo.getJSONObject("patcher_data")

        val bLines = listOf(
            "# Build Information",
            "| PROPERTY  | VALUE |",
            "| ------------- | ------------- |",
            "| GENERATION_ID  | ${pData.getJSONObject("ifl").getString("gen_id")} |",
            "| BUILD_TS  | ${pData.getString("build_date")} |",
            "| IFL_VERSION  | ${pData.getJSONObject("ifl").getInt("version")} |",
            "| IG_VERSION  | ${pData.getJSONObject("ig").getString("version")} |",
            "| IG_VER_CODE  | ${pData.getJSONObject("ig").getString("ver_code")} |",
            "| MD5_HASH_UC | ${buildInfo.getJSONObject("hash").getString("unclone")} |",
            "| MD5_HASH_C | ${buildInfo.getJSONObject("hash").getString("clone")} |\n",
            "Generated with **Instafel Patcher** v$patcherVersion ($patcherCommit/release)"
        )

        val body = bLines.joinToString("\n")

        val req = JSONObject().apply {
            put("tag_name", GEN_ID)
            put("name", "Preview of ${pData.getJSONObject("ig").getString("version")}")
            put("body", body)
            put("draft", false)
            put("prerelease", false)
            put("generate_release_notes", false)
        }

        val requestBody = RequestBody.create(
            "application/json".toMediaType(),
            req.toString()
        )

        val request = Request.Builder()
            .url("https://api.github.com/repos/mamiiblt/instafel_previews/releases")
            .addHeader("Authorization", "Bearer $GITHUB_PAT")
            .addHeader("Accept", "application/vnd.github+json")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")
            .post(requestBody)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val resp = JSONObject(response.body.string())
                Log.info("Release created!")

                val uploadUrl = resp.getString("upload_url")
                    .replace("{?name,label}", "?name=%s")

                Log.info("Uploading assets...")
                uploadAsset(uploadUrl, APK_UC)
                uploadAsset(uploadUrl, APK_C)
                uploadAsset(uploadUrl, F_BUILD_INFO)
                Log.info("Assets uploaded")

                sendLogToTelegram()
            } else {
                Log.severe("Error while creating release: ${response.code} ${response.message}")
                Log.severe(response.body.string())
            }
        }
    }

    fun uploadAsset(assetUploadUrl: String, file: File): String {
        Log.info("Uploading file ${file.name}")

        val url = assetUploadUrl.format(file.name)
        val reqBody = file.asRequestBody("application/octet-stream".toMediaType())

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $GITHUB_PAT")
            .header("Accept", "application/vnd.github+json")
            .post(reqBody)
            .build()

        return httpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                Log.info("Asset ${file.name} uploaded.")
                val resp = JSONObject(response.body.string())
                resp.getString("browser_download_url")
            } else {
                val errorBody = response.body.string()
                Log.severe("Error while uploading asset: ${file.name} - ${response.code} - $errorBody")
                exitProcess(-1)
            }
        }
    }

    fun sendLogToTelegram() {
        val requestBody = buildInfo
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.mamii.me/ifl/manager_new/sendGeneratedLogTg")
            .addHeader("Authorization", Env.Config.getString(Env.Config.Keys.manager_token, "null"))
            .post(requestBody)
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.severe("Request unsuccessful: ${response.code}")
                    return
                }
                Log.info("Log successfully sent to Telegram.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.severe("Error while sending request.")
            exitProcess(-1)
        }
    }

}