package instafel.app.api.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import instafel.app.api.models.AutoUpdateInfo;
import instafel.app.managers.OverridesManager;
import instafel.app.managers.PreferenceManager;
import instafel.app.ota.CheckUpdates;
import instafel.app.utils.localization.LocalizedStringGetter;
import instafel.app.utils.types.PreferenceKeys;

public class BackupUpdateDownloadTask extends AsyncTask<String, Void, String> {

    private Activity activity;
    private PreferenceManager preferenceManager;
    private AutoUpdateInfo autoUpdateInfo;
    private Locale locale;
    private JSONObject updateManifest;
    private OverridesManager overridesManager;

    public BackupUpdateDownloadTask(Activity activity, PreferenceManager preferenceManager, AutoUpdateInfo autoUpdateInfo, Locale locale, JSONObject updateManifest) {
        this.activity = activity;
        this.preferenceManager = preferenceManager;
        this.autoUpdateInfo = autoUpdateInfo;
        this.locale = locale;
        this.updateManifest = updateManifest;
        this.overridesManager = new OverridesManager(activity);
    }

    @Override
    protected String doInBackground(String... f_url) {
        String responseString = null;
        try {
            URL url = new URL(f_url[0]);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                responseString = response.toString();
            } else {
                Log.v("Instafel", "Request failed with code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String responseString) {
        if (responseString != null) {
            try {
                JSONObject newBackupContent = new JSONObject(responseString);
                overridesManager.writeContentIntoOverridesFile(newBackupContent.getJSONObject("backup"));
                autoUpdateInfo.setCurrent_backup_version(updateManifest.getInt("backup_version"));
                preferenceManager.setPreferenceString(PreferenceKeys.ifl_backup_update_value, autoUpdateInfo.exportAsJsonString());
                CheckUpdates.showBackupUpdateDialog(activity, locale, autoUpdateInfo.getBackup_id());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, LocalizedStringGetter.getDialogLocalizedString(activity, locale, "ifl_a11_26"), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, LocalizedStringGetter.getDialogLocalizedString(activity, locale, "ifl_a11_26"), Toast.LENGTH_SHORT).show();
        }
    }
}
