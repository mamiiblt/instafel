package me.mamiiblt.instafel.updater.utils;

import android.app.LocaleManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.os.Build;
import android.os.LocaleList;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class LocalizationUtils {

    public static String getLanguageDisplayName(String langCode) {
        String[] parts = langCode.split("-");

        String languageCode = parts[0];
        String countryCode = parts.length > 1 ? parts[1] : "";

        Locale locale = countryCode.isEmpty()
                ? new Locale(languageCode)
                : new Locale(languageCode, countryCode.substring(1));

        String languageName = locale.getDisplayLanguage(locale);
        String countryName = locale.getDisplayCountry(locale);

        String displayName;
        if (!countryName.isEmpty() && !langCode.trim().equalsIgnoreCase("en-rEN")) {
            displayName = languageName + " (" + countryName + ")";
        } else {
            displayName = languageName;
        }

        if (!displayName.isEmpty()) {
            displayName = displayName.substring(0, 1).toUpperCase(locale) + displayName.substring(1);
        }

        return displayName;
    }

    private static final String KEY_LANGUAGE = "language";

    public static Context applyLocale(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String langCode = prefs.getString(KEY_LANGUAGE, "en-rEN");
        if (langCode == null) langCode = "en-rEN";
        return updateResources(context, langCode);
    }

    public static void setAppLocale(Context context, String langCode) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LANGUAGE, langCode)
                .apply();

        updateResources(context, langCode);
    }

    private static Context updateResources(Context context, String langCode) {
        String language;
        String country;

        if (langCode.contains("-r")) {
            String[] parts = langCode.split("-r");
            language = parts[0];
            country = parts.length > 1 ? parts[1] : "";
        } else if (langCode.contains("-")) {
            String[] parts = langCode.split("-");
            language = parts[0];
            country = parts.length > 1 ? parts[1] : "";
        } else {
            language = langCode;
            country = "";
        }

        Locale locale = country.isEmpty() ? new Locale(language) : new Locale(language, country);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and Up (New App Localization API)
            LocaleManager localeManager = context.getSystemService(LocaleManager.class);
            if (localeManager != null) {
                localeManager.setApplicationLocales(new LocaleList(locale));
            }
            return context;
        } else {
            // For Android 7
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        }
    }
}
