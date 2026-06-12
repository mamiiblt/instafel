/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

package instafel.app.utils;

import android.app.Activity;

import instafel.app.managers.PreferenceManager;
import instafel.app.utils.types.PreferenceKeys;
import instafel.app.utils.types.Types;

public class InstafelAdminUser {

    public static void login(Activity activity, String username, String password) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        preferenceManager.setPreferenceString(PreferenceKeys.ifl_admin_username, GeneralFn.encodeString(username));
        preferenceManager.setPreferenceString(PreferenceKeys.ifl_admin_password, GeneralFn.encodeString(password));
    }

    public static void logout(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        preferenceManager.setPreferenceString(PreferenceKeys.ifl_admin_username, "def");
        preferenceManager.setPreferenceString(PreferenceKeys.ifl_admin_password, "def");
    }

    public static boolean isUserLogged(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        if (!preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_username, "def").equals("def")) {
            return true;
        } else {
            return false;
        }
    }

    public static Types.AdminUserData getCurrentUserData(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        if (!preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_username, "def").equals("def")) {
            return new Types.AdminUserData(
                    preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_username, "def"),
                    preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_password, "def"));
        } else {
            return null;
        }
    }
}

