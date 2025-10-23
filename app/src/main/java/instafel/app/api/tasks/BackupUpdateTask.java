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
import instafel.app.managers.PreferenceManager;
import instafel.app.utils.localization.LocalizedStringGetter;

public class BackupUpdateTask extends AsyncTask<String, Void, String> {

    private Activity activity;
    private PreferenceManager preferenceManager;
    private AutoUpdateInfo autoUpdateInfo;
    private Locale locale;

    public BackupUpdateTask(Activity activity, PreferenceManager preferenceManager, AutoUpdateInfo autoUpdateInfo, Locale locale) {
        this.activity = activity;
        this.preferenceManager = preferenceManager;
        this.autoUpdateInfo = autoUpdateInfo;
        this.locale = locale;
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
                JSONObject jsonObject = new JSONObject(responseString);
                JSONObject manifestObject = jsonObject.getJSONObject("manifest");
                if (manifestObject.getInt("backup_version") > autoUpdateInfo.getCurrent_backup_version()) {
                    new BackupUpdateDownloadTask(activity, preferenceManager, autoUpdateInfo, locale, manifestObject)
                            .execute("https://raw.githubusercontent.com/instafel/backups/main/" + autoUpdateInfo.getBackup_id() + "/backup.ibackup");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, LocalizedStringGetter.getDialogLocalizedString(activity, locale, "ifl_a11_26"), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, LocalizedStringGetter.getDialogLocalizedString(activity, locale, "ifl_a11_26"), Toast.LENGTH_SHORT).show();
        }
    }
}
