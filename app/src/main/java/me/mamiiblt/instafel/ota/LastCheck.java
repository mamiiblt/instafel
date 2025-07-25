package me.mamiiblt.instafel.ota;

import android.app.Activity;
import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;

public class LastCheck {
    public static String get(Activity activity, Locale locale) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        long prefData = preferenceManager.getPreferenceLong(PreferenceKeys.ifl_ota_last_check, 0);
        if (prefData != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", locale);
            return activity.getString(R.string.ifl_a5_02, sdf.format(new Date(prefData)));
        } else {
            return activity.getString(R.string.ifl_a5_02, activity.getString(R.string.ifl_a5_05));
        }
    }

    public static void update(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        preferenceManager.setPreferenceLong(PreferenceKeys.ifl_ota_last_check, System.currentTimeMillis());
    }

    public static void updateUi(Activity activity) {
        TileLarge tileCheck = activity.findViewById(R.id.ifl_tile_ota_check);
        tileCheck.setSubtitleText(get(activity, Locale.getDefault()));
    }
}
