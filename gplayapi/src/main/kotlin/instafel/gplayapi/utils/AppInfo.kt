package instafel.gplayapi.utils

import com.aurora.gplayapi.data.models.App
import org.json.JSONObject

class AppInfo(val app: App) {

    private val infoObject: JSONObject = JSONObject().apply {
        put("id", app.id)
        put("ver_name", app.versionName)
        put("ver_code", app.versionCode)
        put("target_sdk", app.targetSdk)
        put("updated_on", app.updatedOn)
    }

    fun getRawJson(): JSONObject = infoObject

    fun getId(): Int = infoObject.getInt("id")

    fun addApkInfo(name: String, url: String, size: Long) {
        infoObject.put(name, JSONObject().put("url", url).put("size", size))
    }

    fun ifApkExist(name: String): Boolean = infoObject.has(name)

    fun getApkSize(name: String): Long = infoObject.getJSONObject(name).getLong("size")

    fun getApkUrl(name: String): String = infoObject.getJSONObject(name).getString("url")

    fun getVer_name(): String = infoObject.getString("ver_name")

    fun getVer_code(): Int = infoObject.getInt("ver_code")

    fun getTarget_sdk(): String = infoObject.getString("target_sdk")

    fun getUpdated_on(): String = infoObject.getString("updated_on")
}
