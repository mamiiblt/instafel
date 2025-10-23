package instafel.app.utils.localization;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.widget.Toast;

import java.util.Locale;
import java.util.Map;

import instafel.app.InstafelEnv;
import instafel.app.managers.PreferenceManager;
import instafel.app.utils.types.PreferenceKeys;

public class LocalizationUtils {

    public static void updateIflLocale(Activity activity, Boolean status) {
        try {
            Locale iflLocale = getIflLocale(activity);
            if (status) {
                InstafelEnv.IFL_LANG = iflLocale;
                setLocale(activity, iflLocale);
            } else {
                setLocale(activity, iflLocale);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLocale(Activity activity, Locale newLocale) {
        try {
            Locale.setDefault(newLocale);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(newLocale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        } catch (Exception e) {
            Toast.makeText(activity, "Error while setting locale: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public static Locale getDeviceLocale() {
        Locale systemLocale = Resources.getSystem().getConfiguration().locale;

        for (int i = 0; i < InstafelEnv.getSupportedLocaleList().size(); i++) {
            Locale locale =  InstafelEnv.getSupportedLocaleList().get(i);
            if (systemLocale.equals(locale)) {
                return locale;
            }
        }

        return InstafelEnv.getSupportedLocaleList().get(0);
    }

    public static Locale getIflLocale(Context ctx) {
        try {
            PreferenceManager preferenceManager = new PreferenceManager(ctx);
            String prefData = preferenceManager.getPreferenceString(PreferenceKeys.ifl_lang_rw, "def");
            if (prefData.equals("def")) {
                return getDeviceLocale();
            } else {
                return convertToLocale(prefData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return InstafelEnv.getSupportedLocaleList().get(0);
        }
    }

    public static void writeLangToSP(Context ctx, String lang) {
        PreferenceManager preferenceManager = new PreferenceManager(ctx);
        preferenceManager.setPreferenceString(PreferenceKeys.ifl_lang_rw, lang);
    }

    public static void setStateOfDevice(Activity act, boolean state) {
        writeLangToSP(act, state ? "def" : convertToLangCode(LocalizationUtils.getDeviceLocale()));
        act.recreate();
    }

    public static void setLanguageClickListeners(Activity act, Map<Locale, LocaleInfoTile> localeInfos) {
        for (Map.Entry<Locale, LocaleInfoTile> entry : localeInfos.entrySet()) {
            entry.getValue().localeTile.setOnClickListener(view -> {
                writeLangToSP(act, convertToLangCode(entry.getKey()));
                setSubIconVisibilityOfLocale(convertToLangCode(entry.getKey()), true, localeInfos);
                act.recreate();
            });
        }
    }

    public static void setVisibilityOfAllLocales(boolean state, Map<Locale, LocaleInfoTile> localeInfos) {
        for (Map.Entry<Locale, LocaleInfoTile> entry : localeInfos.entrySet()) {
            entry.getValue().setTileVisibility(state);
        }
    }

    public static void setSubIconVisibilityOfLocale(String switchingLangCode, boolean state, Map<Locale, LocaleInfoTile> localeInfos) {
        for (Map.Entry<Locale, LocaleInfoTile> entry : localeInfos.entrySet()) {
            if (convertToLangCode(entry.getKey()).equals(switchingLangCode)) {
                entry.getValue().setTickStatus(state);
            } else {
                entry.getValue().setTickStatus(false);
            }
        }
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

    public static Locale getActivityLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return config.getLocales().get(0);
        } else {
            return config.locale;
        }
    }
}
