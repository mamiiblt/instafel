package me.mamiiblt.instafel.updater;

import android.app.Application;

import android.content.Context;
import me.mamiiblt.instafel.updater.utils.LocalizationUtils;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        Context localizedContext = LocalizationUtils.applyLocale(base);
        super.attachBaseContext(localizedContext);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocalizationUtils.applyLocale(this);
    }
}
