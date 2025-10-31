package me.mamiiblt.instafel.updater.utils;

import android.app.Activity;
import android.app.LocaleManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.os.Build;
import android.os.LocaleList;
import androidx.preference.PreferenceManager;
import me.mamiiblt.instafel.updater.R;

import java.util.Locale;

public class LocalizationUtils {

    public static LocaleList getSupportedLocaleList(Activity activity) {
        String[] supportedLanguages = activity.getResources().getStringArray(R.array.supported_languages);
        Locale[] locales = new Locale[supportedLanguages.length];

        for (int i = 0; i < supportedLanguages.length; i++) {
            String code = supportedLanguages[i];
            String language;
            String country;

            String[] parts = code.split("-");
            language = parts[0];
            country = parts[1];

            locales[i] = new Locale(language, country);
        }

        return new LocaleList(locales);
    }

    public static String getLanguageDisplayName(Locale locale) {
        String languageName = locale.getDisplayLanguage(locale);
        String countryName = locale.getDisplayCountry(locale);

        String displayName;
        if (!countryName.isEmpty() && !convertToLangCode(locale).equalsIgnoreCase("en-US")) {
            displayName = languageName + " (" + countryName + ")";
        } else {
            displayName = languageName;
        }

        if (!displayName.isEmpty()) {
            displayName = displayName.substring(0, 1).toUpperCase(locale) + displayName.substring(1);
        }

        return displayName;
    }

    private static final String KEY_LANGUAGE = "iflu_app_lang";

    public static Context applyLocale(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String langCode = prefs.getString(KEY_LANGUAGE, "en-US");
        return updateResources(context, convertToLocale(langCode));
    }

    private static Context updateResources(Context context, Locale locale) {
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

    public static void setAppLocale(Context context, String langCode) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LANGUAGE, langCode)
                .apply();

        updateResources(context, convertToLocale(langCode));
    }

    public static String convertToLangCode(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();

        return language + "-" + country;
    }

    public static Locale convertToLocale(String langCode) {
        String[] parts = langCode.split("-");
        String language = parts[0];
        String country = parts[1];

        return new Locale(language, country);
    }
}
