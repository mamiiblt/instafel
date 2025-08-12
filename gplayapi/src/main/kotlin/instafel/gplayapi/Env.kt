package instafel.gplayapi

import instafel.gplayapi.utils.AppInfo
import instafel.gplayapi.utils.General
import instafel.gplayapi.utils.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

class Env {
    companion object {
        var email: String? = null; var aas_token: String? = null; var github_releases_link: String? = null; var github_pat: String? = null; var telegram_api_key: String? = null
        lateinit var deviceProperties: Properties
        lateinit var client: OkHttpClient

        fun updateEnvironment() {
            val props = Properties()
            client = OkHttpClient()
            try {
                val configFile = File(General.mergePaths(System.getProperty("user.dir"), "config.properties"))
                if (!configFile.exists()) {
                    Log.println("E", "Please set config.properties file in your current directory")
                    exitProcess(-1)
                }

                val input: FileInputStream = configFile.inputStream()
                props.load(input)

                val emailP = props.getProperty("email", null)
                val aasTokenP = props.getProperty("aas_token", null)
                val githubRelLink = props.getProperty("github_releases_link", null)
                val githubPatP = props.getProperty("github_pat", null)

                if (emailP != null && aasTokenP != null && githubRelLink != null && githubPatP != null) {
                    email = emailP
                    aas_token = aasTokenP
                    github_releases_link = githubRelLink
                    github_pat = githubPatP

                    Log.println("I", "User ($email) read from config file.")
                } else {
                    Log.println("E", "Error while reading email & aas token property from gplayapi.properties")
                    exitProcess(-1)
                }
            } catch (e: Exception) {
                e.printStackTrace();
                Log.println("E", "Error while updating environment")
                exitProcess(-1)
            }
        }

        fun updateDeviceProp(propName: String) {
            try {
                val input: InputStream? = Env::class.java.classLoader.getResourceAsStream(
                    Paths.get("device_props", propName).toString()
                )

                if (input == null) {
                    Log.println("E", "Please write a valid device config filename")
                    exitProcess(-1)
                }

                deviceProperties = Properties()
                deviceProperties.load(input)
            } catch (e: Exception) {
                e.printStackTrace();
                Log.println("E", "Error while updating device properties")
            }
        }

        fun startChecker() {
            val timer = Timer()

            val lastCheckedVersion = arrayOf("")
            val checkTime = intArrayOf(0)

            val task = object : TimerTask() {
                override fun run() {
                    try {
                        checkTime[0]++
                        Log.println("I", "${checkTime[0]} check started.")
                        val instance = InstafelGplayapiInstance("com.instagram.android")
                        val appInfo = instance.getIgApk()

                        if (appInfo?.getVer_name()?.contains(".0.0.0.") == true) {
                            if (lastCheckedVersion[0] != appInfo.getVer_name()) {
                                lastCheckedVersion[0] = appInfo.getVer_name()
                                val latestIflVersion = getLatestInstafelVersion()
                                if (latestIflVersion != null && latestIflVersion != appInfo.getVer_name()) {
                                    Log.println("I", "Triggering update, $latestIflVersion -> ${appInfo.getVer_name()}")
                                    triggerUpdate(appInfo)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.println("E", "Error while checking ig updates.")
                    }
                }
            }

            val delayMs = 900_000L
            timer.scheduleAtFixedRate(task, 0, delayMs)
        }


        @Throws(Exception::class)
        private fun triggerUpdate(appInfo: me.mamiiblt.instafel.gplayapi.utils.AppInfo) {
            val workflowData = JSONObject().apply {
                put("event_type", "generate_instafel")
                put("client_payload", JSONObject().apply {
                    put("base", JSONObject().put("url", appInfo.getApkUrl("base_apk")))
                    put("rconf", JSONObject().put("url", appInfo.getApkUrl("rconf_apk")))
                })
            }

            Log.println("I", "Calling patcher for new version: ${appInfo.getVer_name()}")

            val request = Request.Builder()
                .url("https://api.github.com/repos/mamiiblt/instafel_patch_runner/dispatches")
                .post(RequestBody.create("application/json".toMediaTypeOrNull(), workflowData.toString()))
                .addHeader("Authorization", "Bearer $github_pat")
                .addHeader("Accept", "application/vnd.github+json")
                .addHeader("X-GitHub-Api-Version", "2022-11-28")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Error while triggering patcher for version ${appInfo.getVer_name()} (${response.code})")
                Log.println("I", "Generator succesfully triggered for ${appInfo.getVer_name()} (status: ${response.code})")
            }
        }

        fun getLatestInstafelVersion(): String? {
            val request = Request.Builder()
                .url(github_releases_link ?: "")
                .addHeader("Authorization", "Bearer $github_pat")
                .addHeader("Accept", "application/vnd.github+json")
                .addHeader("X-GitHub-Api-Version", "2022-11-28")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Instafel Release response code is ${response.code}")

                val responseObject = JSONObject(response.body!!.string())
                val releaseBody: List<String> = responseObject.getString("body").split("\n")

                for (line in releaseBody) {
                    if (line.contains("app.version_name")) {
                        val verNameLines: List<String> = line.split("\\|");
                        for (part in verNameLines) {
                            if (!part.isEmpty() && General.isNumeric(part)) {
                                return part.trim();
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
}