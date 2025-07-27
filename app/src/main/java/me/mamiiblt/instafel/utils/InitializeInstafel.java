package me.mamiiblt.instafel.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import me.mamiiblt.instafel.activity.ifl_a_menu;
import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.ota.CheckUpdates;
import me.mamiiblt.instafel.utils.localization.LocalizationUtils;
import me.mamiiblt.instafel.utils.localization.LocalizedStringGetter;

public class InitializeInstafel {
    public static Context ctx;

    public static void setContext(Application application) {
        ctx = application;
        String iflLocale = LocalizationUtils.getIflLocale(ctx);
        InstafelEnv.IFL_LANG = iflLocale;
        Log.v("IFL", "InstafelEnv.IFL_LANG is set to " + iflLocale);
    }

    public static void triggerCheckUpdates(Activity activity) {
        CheckUpdates.checkUpdates(activity);
    }

    public static void startInstafel() {
        try {
            Intent intent = new Intent(ctx, ifl_a_menu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ctx, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
