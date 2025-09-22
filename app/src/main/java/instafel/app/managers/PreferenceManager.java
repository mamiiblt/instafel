package instafel.app.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private Context ctx;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context _ctx) {
        this.ctx = _ctx;
        this.preferences = ctx.getSharedPreferences("ifl_prefs", Context.MODE_PRIVATE);
        this.editor = this.preferences.edit();
    }

    public Boolean exists(String prefKey) {
        if (preferences.getAll().get(prefKey) != null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getPreferenceBoolean(String prefKey, boolean def) {
        return preferences.getBoolean(prefKey, def);
    }

    public String getPreferenceString(String prefKey, String def) {
        return preferences.getString(prefKey, def);
    }

    public void setPreferenceBoolean(String prefKey, boolean value) {
        editor.putBoolean(prefKey, value);
        editor.apply();
    }

    public void setPreferenceString(String prefKey, String value) {
        editor.putString(prefKey, value);
        editor.apply();
    }

    public long getPreferenceLong(String prefKey, long def) {
        return preferences.getLong(prefKey, def);
    }

    public void setPreferenceLong(String prefKey, long value) {
        editor.putLong(prefKey, value);
        editor.apply();
    }

    public int getPreferenceInt(String prefKey, int def) {
        return preferences.getInt(prefKey, def);
    }

    public void setPreferenceInt(String prefKey, int value) {
        editor.putInt(prefKey, value);
        editor.apply();
    }
}
