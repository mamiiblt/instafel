package me.mamiiblt.instafel.utils.localization;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.widget.Toast;

import java.util.Locale;
import java.util.Map;

import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;

public class LocalizationUtils {

    public static void updateIflLocale(Activity activity, Boolean status) {
        try {
            if (status) {
                InstafelEnv.IFL_LANG = getIflLocale(activity);
                setLocale(activity, InstafelEnv.IFL_LANG);
            } else {
                setLocale(activity, InstafelEnv.IFL_LANG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLocale(Activity activity, String languageCode) {
        try {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        } catch (Exception e) {
            Toast.makeText(activity, "Error while setting locale: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public static Locales.LocaleType getDeviceLocale() {
        for (Map.Entry<String, Locales.LocaleType> entry : Locales.SUPPORTED_LOCALES.entrySet()) {
            if (entry.getValue().langCode.equals(Resources.getSystem().getConfiguration().locale.getLanguage())) {
                return entry.getValue();
            }
        }
        return Locales.SUPPORTED_LOCALES.get("en");
    }

    public static String getIflLocale(Context ctx) {
        try {
            PreferenceManager preferenceManager = new PreferenceManager(ctx);
            String prefData = preferenceManager.getPreferenceString(PreferenceKeys.ifl_lang, "def");
            if (prefData.equals("def")) {
                return getDeviceLocale().langCode;
            } else {
                return prefData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "en";
        }
    }

    public static void writeLangToSP(Context ctx, String lang) {
        PreferenceManager preferenceManager = new PreferenceManager(ctx);
        preferenceManager.setPreferenceString(PreferenceKeys.ifl_lang, lang);
    }

    public static void setStateOfDevice(Activity act, boolean state) {
        writeLangToSP(act, state ? "def" : LocalizationUtils.getDeviceLocale().langCode);
        act.recreate();
    }

    public static void setLanguageClickListeners(Activity act, Map<String, LocalizationInfo> localeInfos) {
        for (Map.Entry<String, LocalizationInfo> entry : localeInfos.entrySet()) {
            entry.getValue().localeTile.setOnClickListener(view -> {
                writeLangToSP(act, entry.getKey());
                setSubIconVisibilityOfLocale(entry.getKey(), true, localeInfos);
                act.recreate();
            });
        }
    }

    public static void setVisibilityOfAllLocales(boolean state, Map<String, LocalizationInfo> localeInfos) {
        for (Map.Entry<String, LocalizationInfo> entry : localeInfos.entrySet()) {
            entry.getValue().setTileVisibility(state);
        }
    }

    public static void setSubIconVisibilityOfLocale(String langCode, boolean state, Map<String, LocalizationInfo> localeInfos) {
        for (Map.Entry<String, LocalizationInfo> entry : localeInfos.entrySet()) {
            if (entry.getKey().equals(langCode)) {
                entry.getValue().setTickStatus(state);
            } else {
                entry.getValue().setTickStatus(false);
            }
        }
    }
}
