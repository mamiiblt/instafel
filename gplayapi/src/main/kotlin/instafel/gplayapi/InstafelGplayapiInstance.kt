package instafel.gplayapi

import instafel.gplayapi.utils.AppInfo
import instafel.gplayapi.utils.Log

import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.File
import com.aurora.gplayapi.helpers.AppDetailsHelper
import com.aurora.gplayapi.helpers.AuthHelper
import com.aurora.gplayapi.helpers.PurchaseHelper
import java.util.Properties
import kotlin.system.exitProcess

class InstafelGplayapiInstance(private val packageName: String) {

    lateinit var authData: AuthData

    @Throws(Exception::class)
    fun fetchData(): AppInfo? {

        authData = authenticateUser(Env.config.email, Env.config.aasToken, Env.deviceProp)!!

        val appInfo = AppInfo(AppDetailsHelper(authData).getAppByPackageName(packageName))
        val files: List<File> = PurchaseHelper(authData).purchase(
            appInfo.app.packageName,
            appInfo.app.versionCode,
            appInfo.app.offerType
        )

        for (file in files) {
            Log.println("I", "File found, ${file.name}")
            when {
                file.name == "com.instagram.android.apk" -> appInfo.addApkInfo("baseApk", file.url, file.size)
                file.name.contains("config") && file.name.contains("dpi.apk") -> appInfo.addApkInfo("resConfigApk", file.url, file.size)
            }
        }

        return if (appInfo.ifApkExist("baseApk") && appInfo.ifApkExist("resConfigApk")) appInfo else null
    }

    fun authenticateUser (email: String, aasKey: String, deviceProp: Properties): AuthData? {
        try {
            return AuthHelper.build(email, aasKey, deviceProp)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.println("E", "An error occurred while authenticating user with GPlay servers.")
            exitProcess(-1)
        }
    }
}
