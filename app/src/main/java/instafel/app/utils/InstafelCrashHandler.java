package instafel.app.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import instafel.app.managers.CrashManager;

public class InstafelCrashHandler implements Thread.UncaughtExceptionHandler {
    
    private Context mContext;
    public InstafelCrashHandler(Context context) {
        mContext = context;
    }
    
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Toast.makeText(mContext, "Instafel crashed, crash log saved.", Toast.LENGTH_SHORT).show();
        CrashManager crashManager = new CrashManager(mContext);
        crashManager.saveLog(e);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
