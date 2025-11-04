package instafel.gplayapi

import instafel.gplayapi.utils.EnvConfig
import instafel.gplayapi.utils.LatestReleaseInfo
import instafel.gplayapi.utils.Log
import okhttp3.OkHttpClient
import java.io.InputStream
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

class Env {
    companion object {
        lateinit var config: EnvConfig
        lateinit var deviceProp: Properties
        lateinit var client: OkHttpClient

        fun updateEnvironment(config: EnvConfig) {
            client = OkHttpClient()
            this.config = config
            Log.println("I", "User (${config.email}) read from config file.")
        }

        fun updateDeviceProp() {
            val input: InputStream? = Env::class.java.classLoader.getResourceAsStream(
                Paths.get("device_props", "gplayapi_px_3a.properties").toString()
            )
            deviceProp = Properties()
            deviceProp.load(input)
        }

        fun getLatestRelease(): LatestReleaseInfo {
            try {
                val instance = InstafelGplayapiInstance("com.instagram.android")
                val appInfo = instance.fetchData()
                    ?: throw Exception("An error occurred while getting latest release info from GPlayAPI")

                return LatestReleaseInfo(
                    versionName = appInfo.getVer_name(),
                    versionCode = appInfo.getVer_code(),
                    updatedOn = appInfo.getUpdated_on(),
                    apkUrlBase = appInfo.getApkUrl("baseApk"),
                    apkUrlRes = appInfo.getApkUrl("resConfigApk"),
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.println("E", e.message.toString())
                exitProcess(-1)
            }
        }
    }
}