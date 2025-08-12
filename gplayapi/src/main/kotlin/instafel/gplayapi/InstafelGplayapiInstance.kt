package instafel.gplayapi

import me.mamiiblt.instafel.gplayapi.utils.AppInfo
import me.mamiiblt.instafel.gplayapi.utils.General
import me.mamiiblt.instafel.gplayapi.utils.Log

import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.File
import com.aurora.gplayapi.helpers.AppDetailsHelper
import com.aurora.gplayapi.helpers.PurchaseHelper

class InstafelGplayapiInstance(private val packageName: String) {

    private val authData: AuthData = General.authenticateUser(Env.email, Env.aas_token, Env.deviceProperties)

    @Throws(Exception::class)
    fun getIgApk(): AppInfo? {
        val appInfo = AppInfo(AppDetailsHelper(authData).getAppByPackageName(packageName))
        val files: List<File> = PurchaseHelper(authData).purchase(
            appInfo.getApp().packageName,
            appInfo.getApp().versionCode,
            appInfo.getApp().offerType
        )

        for (file in files) {
            Log.println("I", "File found, ${file.name}")
            when {
                file.name == "com.instagram.android.apk" -> appInfo.addApkInfo("base_apk", file.url, file.size)
                file.name.contains("config") && file.name.contains("dpi.apk") -> appInfo.addApkInfo("rconf_apk", file.url, file.size)
            }
        }

        return if (appInfo.ifApkExist("base_apk") && appInfo.ifApkExist("rconf_apk")) appInfo else null
    }
}
