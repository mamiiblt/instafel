/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package dev.mamii.instafel.updater;

import android.app.Application;

import android.content.Context;
import dev.mamii.instafel.updater.utils.LocalizationUtils;

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
