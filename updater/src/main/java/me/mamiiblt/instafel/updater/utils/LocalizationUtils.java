package me.mamiiblt.instafel.updater.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import java.util.Locale;

import me.mamiiblt.instafel.updater.MainActivity;
import okhttp3.Response;

public class LocalizationUtils {

    private Context ctx;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public LocalizationUtils(Context ctx) {
        this.ctx = ctx;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        this.editor = preferences.edit();
    }

    public String getLanguage() {
        String selectedLanguage = preferences.getString("language", "Use Device Language");
        switch (selectedLanguage) {
            case "Türkçe (tr_TR)":
                return "tr";
            case "Polish (pl_PL)":
                return "pl";
            case "Use Device Language":
                return Resources.getSystem().getConfiguration().locale.getLanguage();
            case "English (en_US)":
            default:
                return "en";
        }
    }

    public void updateAppLanguage() {
        String selectedLanguage = preferences.getString("language", "Use Device Language");
        Locale locale;
        switch (selectedLanguage) {
            case "Türkçe (tr_TR)":
                locale = new Locale("tr");
                break;
            case "Polish (pl_PL)":
                locale = new Locale("pl");
                break;
            case "Use Device Language":
                locale = new Locale(Resources.getSystem().getConfiguration().locale.getLanguage());
                break;
            case "English (en_US)":
            default:
                locale = new Locale("en");
                break;
        }
        Resources resources = ctx.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
