package instafel.patcher.core.utils

import instafel.patcher.core.utils.modals.pojo.PatcherLocaleInfo
import org.json.JSONArray
import java.io.InputStream
import java.util.Locale

object LocaleUtils {
    fun loadSupportedLocalesFromAssets(): JSONArray {
        return JSONArray(
            slurp(LocaleUtils::class.java.getResourceAsStream("/supported_locales.json"))
        )
    }

    fun parseLocalesFromLangCodes(): List<PatcherLocaleInfo> {
        val localeList = mutableListOf<PatcherLocaleInfo>()
        val languageCodes = loadSupportedLocalesFromAssets()

        for (i in 0 until languageCodes.length()) {
            val languageCode = languageCodes.getString(i)
            val parts = languageCode.split("-")

            val locale = when (parts.size) {
                1 -> Locale(parts[0])
                2 -> Locale(parts[0], parts[1])
                else -> Locale(parts[0])
            }

            localeList.add(PatcherLocaleInfo("${parts[0]}-r${parts[1]}", locale))
        }

        return localeList
    }

    fun getDisplayString(locale: Locale): String {
        return "${locale.displayLanguage} (${locale.displayCountry})"
    }

    // Utility functions
    private fun slurp(inputStream: InputStream): String = inputStream.bufferedReader().use { it.readText() }
}