package dev.mamii.instafel.updater.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import dev.mamii.instafel.updater.BuildConfig;
import dev.mamii.instafel.updater.IUserService;
import dev.mamii.instafel.updater.services.UserService;
import rikka.shizuku.Shizuku;

public class ShizukuInstaller {

    private static IUserService mUserService = null;

    public static boolean isShizukuSupported() {
        return Shizuku.pingBinder() && Shizuku.getVersion() >= 11 && !Shizuku.isPreV11();
    }

    public static boolean ensureUserService(Context ctx) {
        if (mUserService != null) {
            return true;
        }

        Shizuku.UserServiceArgs mUserServiceArgs = new Shizuku.UserServiceArgs(new ComponentName(BuildConfig.APPLICATION_ID, UserService.class.getName()))
                .daemon(false)
                .processNameSuffix("service")
                .debuggable(BuildConfig.DEBUG)
                .version(BuildConfig.VERSION_CODE);
        Shizuku.bindUserService(mUserServiceArgs, mServiceConnection);

        return false;
    }

    public static boolean userServiceIsAvailable() {
        if (mUserService != null) {
            return true;
        } else {
            return false;
        }
    }

    private static final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (iBinder == null || !iBinder.pingBinder()) {
                return;
            }

            mUserService = IUserService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public static String runCommand(Context ctx, String command) {
        if (ensureUserService(ctx)) {
            try {
                return mUserService.executeShellCommand(command);
            } catch (RemoteException ignored) {
                ignored.printStackTrace();
            }
        } else {
            Log.v("IFL", "ensureUserService is false");
        }
        return "";
    }
}