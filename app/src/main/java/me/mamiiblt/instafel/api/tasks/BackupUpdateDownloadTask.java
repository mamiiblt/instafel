package me.mamiiblt.instafel.api.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.mamiiblt.instafel.api.models.AutoUpdateInfo;
import me.mamiiblt.instafel.managers.OverridesManager;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ota.CheckUpdates;
import me.mamiiblt.instafel.utils.Localizator;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;

public class BackupUpdateDownloadTask extends AsyncTask<String, Void, String> {

    private Activity activity;
    private PreferenceManager preferenceManager;
    private AutoUpdateInfo autoUpdateInfo;
    private String languageCode;
    private JSONObject updateManifest;
    private OverridesManager overridesManager;

    public BackupUpdateDownloadTask(Activity activity, PreferenceManager preferenceManager, AutoUpdateInfo autoUpdateInfo, String languageCode, JSONObject updateManifest) {
        this.activity = activity;
        this.preferenceManager = preferenceManager;
        this.autoUpdateInfo = autoUpdateInfo;
        this.languageCode = languageCode;
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
                CheckUpdates.showBackupUpdateDialog(activity, languageCode, autoUpdateInfo.getBackup_id());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, Localizator.getDialogLocalizedString(activity, languageCode, "ifl_a11_26"), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, Localizator.getDialogLocalizedString(activity, languageCode, "ifl_a11_26"), Toast.LENGTH_SHORT).show();
        }
    }
}
