package instafel.app;

import android.app.Application;

import instafel.app.utils.InstafelCrashHandler;
public class ExampleAppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new InstafelCrashHandler(this));
    }
}
