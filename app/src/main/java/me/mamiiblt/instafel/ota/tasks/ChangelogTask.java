package me.mamiiblt.instafel.ota.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;

public class ChangelogTask extends AsyncTask<String, Void, String> {

    private final Activity act;
    private int ifl_version = 0;

    public ChangelogTask(Activity activity, int _ifl_version) {
        this.ifl_version = _ifl_version;
        this.act = activity;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return sendGetRequest(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "Couldn't connect to the server";
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String result) {
        try {
            PreferenceManager preferenceManager = new PreferenceManager(act);
            JSONObject mcqJson = new JSONObject(result);
            byte[] dataBytes = Base64.decode(mcqJson.getString("content"), Base64.DEFAULT);
            int clogVersion = new JSONObject(new String(dataBytes)).getInt("llog_ver");
            String logUrl = "https://api.github.com/repos/instafel/instafel/contents/clogs/clog_" + clogVersion + ".txt";
            boolean disableVersionControl = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_clog_disable_version_control, false);

            if (!disableVersionControl) {
                if (ifl_version >= clogVersion) {
                    int lastShownClogVersion = preferenceManager.getPreferenceInt(PreferenceKeys.ifl_clog_last_shown_version, 0);

                    if (lastShownClogVersion != ifl_version) {
                        new ChangelogContentTask(act, ifl_version, clogVersion).execute(logUrl);
                    }
                }
            } else {
                Toast.makeText(act, "Disable version control is true, skipping version control.", Toast.LENGTH_SHORT).show();
                new ChangelogContentTask(act, ifl_version, clogVersion).execute(logUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(15000);
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            urlConnection.disconnect();
        }
    }
}