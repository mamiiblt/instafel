package me.mamiiblt.instafel.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.environment.InstafelEnv;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ui.TileLarge;

public class Localizator {

    public static String[] supportedLangs = {"en", "tr", "el", "de", "fr", "hu", "hi", "es", "pt"};

    public static void writeLangSh(Context context, String lang) {
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.setPreferenceString(PreferenceKeys.ifl_lang, lang);
    }

    public static void enableItem(Activity activity, int itemId) {
        List<TileLarge> items = new ArrayList<>();
        items.add(activity.findViewById(R.id.ifl_tile_lang_english));
        items.add(activity.findViewById(R.id.ifl_tile_lang_turkish));
        items.add(activity.findViewById(R.id.ifl_tile_lang_deutch));
        items.add(activity.findViewById(R.id.ifl_tile_lang_greece));
        items.add(activity.findViewById(R.id.ifl_tile_lang_france));
        items.add(activity.findViewById(R.id.ifl_tile_lang_hungary));
        items.add(activity.findViewById(R.id.ifl_tile_lang_hindi));
        items.add(activity.findViewById(R.id.ifl_tile_lang_spanish));
        items.add(activity.findViewById(R.id.ifl_tile_lang_portugal));

        for (int i = 0; i < items.size(); i++) {
            if (i == itemId) {
                items.get(i).setVisiblitySubIcon("visible");
            } else {
                items.get(i).setVisiblitySubIcon("gone");
            }
        }
    }

    public static String getLocalizedString(Activity _activity, String languageCode, String resLabel, Object... params) {
        try {
            Resources appResources = GeneralFn.getAppResourcesWithConf(_activity, languageCode);
            if (params.length != 0) {
                return appResources.getString(
                        GeneralFn.getStringResId(_activity, appResources, resLabel),
                        params
                );
            } else {
                return appResources.getString(
                        GeneralFn.getStringResId(_activity, appResources, resLabel)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_activity, e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    // new method for InstafelDialog
    public static String getDialogLocalizedString(Activity _activity, String languageCode, String resourceName) {
        try {
            Resources appResources = GeneralFn.getAppResourcesWithConf(_activity, languageCode);
            return appResources.getString(
                    GeneralFn.getStringResId(_activity, appResources, resourceName)
            ).replace("\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateDialogLocale(Activity _activity, String languageCode, DialogItem... views) {
        try {
            Resources appResources = GeneralFn.getAppResourcesWithConf(_activity, languageCode);
            for (DialogItem item : views) {
                item.getTextView().setText(
                        appResources.getString(
                                GeneralFn.getStringResId(_activity, appResources, item.getStringName())
                        ).replace("\\n", "\n")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(Activity activity, String resLabel) {
        Resources appResources = GeneralFn.getAppResources(activity);
        return appResources.getString(
                GeneralFn.getStringResId(activity, appResources, resLabel)
        );
    }

    public static String getIflLocale(Context ctx) {
        try {
            PreferenceManager preferenceManager = new PreferenceManager(ctx);
            String prefData = preferenceManager.getPreferenceString(PreferenceKeys.ifl_lang, "def");
            if (prefData.equals("def")) {
                for (String supportedLang : supportedLangs) {
                    if (supportedLang.equals(Resources.getSystem().getConfiguration().locale.getLanguage())) {
                        return supportedLang;
                    }
                }
                return "en";
            } else {
                return prefData;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "en";
        }
    }

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
            Toast.makeText(activity, "err: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
