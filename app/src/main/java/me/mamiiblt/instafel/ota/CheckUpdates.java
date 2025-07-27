package me.mamiiblt.instafel.ota;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONObject;

import me.mamiiblt.instafel.api.models.AutoUpdateInfo;
import me.mamiiblt.instafel.api.tasks.BackupUpdateTask;
import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ota.tasks.ChangelogTask;
import me.mamiiblt.instafel.ota.tasks.VersionTask;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.localization.LocalizationUtils;
import me.mamiiblt.instafel.utils.localization.LocalizedStringGetter;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;
import me.mamiiblt.instafel.utils.dialog.InstafelDialogMargins;
import me.mamiiblt.instafel.utils.dialog.InstafelDialogTextType;
import me.mamiiblt.instafel.utils.types.Types;

public class CheckUpdates {
    public static void set(Activity activity, TileLarge checkUpdates) {
        checkUpdates.setOnClickListener(v -> {
            try {
                check(activity, true);
            } catch (Exception e) {
                Toast.makeText(activity, "Error while checking updates.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void checkUpdates(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        boolean welcomeState = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_welcome_message, false);
        boolean updateBackupState = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_enable_auto_update, false);
        if (!welcomeState) {
            showWelcomeDialog(activity);
        } else {
            if (InstafelEnv.PRODUCTION_MODE) {
                long oneDayMs = 24 * 60 * 60 * 1000;
                long currentTimeStamp = System.currentTimeMillis();

                if (updateBackupState) {
                    long backupLastCheckStamp = preferenceManager.getPreferenceLong(PreferenceKeys.ifl_backup_last_check, 0);
                    long elapsedTime = currentTimeStamp - backupLastCheckStamp;
                    if (elapsedTime >= oneDayMs) {
                        checkBackupUpdate(activity);
                    }
                }

                long lastSuccessUpdateTimestamp = preferenceManager.getPreferenceLong(PreferenceKeys.ifl_ota_last_success_install, 0);
                if (lastSuccessUpdateTimestamp != 0) {
                    checkChangelog(activity, currentTimeStamp, lastSuccessUpdateTimestamp);
                }

                boolean updateState = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_ota_setting, false);
                int freqData = preferenceManager.getPreferenceInt(PreferenceKeys.ifl_ota_freq, Types.FreqLabels.EVERY_OPEN);
                long lastCheckStamp = preferenceManager.getPreferenceLong(PreferenceKeys.ifl_ota_last_check, 0);

                if (updateState) {
                    long elapsedTime = currentTimeStamp - lastCheckStamp;

                    boolean requireCheck = false;
                    switch (freqData) {
                        case 0:
                            requireCheck = true;
                            break;
                        case 1:
                            requireCheck = false;
                            break;
                        case 2:
                            if (elapsedTime >= oneDayMs) {
                                requireCheck = true;
                            }
                            break;
                        case 3:
                            if (elapsedTime >= oneDayMs * 3) {
                                requireCheck = true;
                            }
                            break;
                        case 4:
                            if (elapsedTime >= oneDayMs * 5) {
                                requireCheck = true;
                            }
                            break;
                        case 5:
                            if (elapsedTime >= oneDayMs * 7) {
                                requireCheck = true;
                            }
                            break;
                    }

                    if (requireCheck) {
                        check(activity, false);
                    }
                }
            }
        }
    }

    public static void checkBackupUpdate(Activity activity) {
        String languageCode = LocalizationUtils.getIflLocale(activity);
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        try {
            JSONObject jsonObject = new JSONObject(preferenceManager.getPreferenceString(PreferenceKeys.ifl_backup_update_value, "[]"));
            if (jsonObject.has("id") && jsonObject.has("name") && jsonObject.has("current_version")) {
                AutoUpdateInfo autoUpdateInfo = new AutoUpdateInfo(jsonObject.getString("id"), jsonObject.getString("name"), jsonObject.getInt("current_version"));
                new BackupUpdateTask(activity, preferenceManager, autoUpdateInfo, languageCode)
                        .execute("https://raw.githubusercontent.com/instafel/backups/main/" + autoUpdateInfo.getBackup_id() + "/manifest.json");
            } else {
                Toast.makeText(activity, LocalizedStringGetter.getDialogLocalizedString(activity, languageCode, "ifl_a11_26"), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, LocalizedStringGetter.getDialogLocalizedString(activity, languageCode, "ifl_a11_26"), Toast.LENGTH_SHORT).show();
        }
    }

    private static void checkChangelog(Activity activity, long j, long j2) {
        int ifl_version = IflEnvironment.getIflVersion(activity);
        if (j - j2 < 3600000 || ifl_version == 204) {
            new ChangelogTask(activity, ifl_version).execute("https://api.github.com/repos/instafel/instafel/contents/mcq.json");
        }
    }
    
    public static void check(Activity activity, boolean checkType) {
        int ifl_version = IflEnvironment.getIflVersion(activity);
        String ifl_type = IflEnvironment.getType(activity);
        new VersionTask(activity, ifl_type, ifl_version, checkType).execute("https://api.github.com/repos/mamiiblt/instafel/releases/latest");
    }

    public static void showBackupUpdateDialog(Activity activity, String languageCode, String backupId) {
        InstafelDialog instafelDialog = new InstafelDialog(activity);
        instafelDialog.addSpace("top_space", 25);
        instafelDialog.addTextView(
                "dialog_title",
                LocalizedStringGetter.getDialogLocalizedString(activity, languageCode, "ifl_a11_27"),
                30,
                0,
                InstafelDialogTextType.TITLE,
                new InstafelDialogMargins(activity, 0, 0));
        instafelDialog.addSpace("mid_space", 20);
        instafelDialog.addTextView(
                "dialog_desc",
                "You can see changelog from website.",
                16,
                310,
                InstafelDialogTextType.DESCRIPTION,
                new InstafelDialogMargins(activity, 24, 24));
        instafelDialog.addSpace("button_top_space", 20);
        instafelDialog.addPozitiveAndNegativeButton(
                "buttons",
                LocalizedStringGetter.getDialogLocalizedString(activity, languageCode, "ifl_a11_28"),
                "View Changelog",
                v -> {
                    System.exit(0);
                    instafelDialog.dismiss();
                },
                view -> GeneralFn.openInWebBrowser(activity, "https://instafel.app/backup?id=" + backupId));
        instafelDialog.addSpace("bottom_space", 27);
        instafelDialog.show();
    }

    private static void showWelcomeDialog(Activity activity) {
        PreferenceManager preferenceManager = new PreferenceManager(activity);
        String languageCode = LocalizationUtils.getIflLocale(activity);

        InstafelDialog instafelDialog = new InstafelDialog(activity);
        instafelDialog.addSpace("top_space", 25);
        instafelDialog.addTextView(
                "dialog_title",
                LocalizedStringGetter.getDialogLocalizedString(activity, languageCode, "ifl_d4_01"),
                30,
                0,
                InstafelDialogTextType.TITLE,
                new InstafelDialogMargins(activity, 0, 0));
        instafelDialog.addSpace("mid_space", 20);
        instafelDialog.addTextView(
                "dialog_desc",
                LocalizedStringGetter.getDialogLocalizedString(activity, languageCode, "ifl_d4_02"),
                16,
                310,
                InstafelDialogTextType.DESCRIPTION,
                new InstafelDialogMargins(activity, 24, 24));
        instafelDialog.addSpace("button_top_space", 20);
        instafelDialog.addPozitiveAndNegativeButton(
                "buttons",
                LocalizedStringGetter.getDialogLocalizedString(activity, languageCode, "ifl_d3_02"),
                null,
                v -> {
                    instafelDialog.dismiss();
                    preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_welcome_message, true);
                },
                null);
        instafelDialog.addSpace("bottom_space", 27);
        instafelDialog.show();

    }
}

