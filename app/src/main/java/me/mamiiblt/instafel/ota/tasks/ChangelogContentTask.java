package me.mamiiblt.instafel.ota.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.utils.localization.LocalizationUtils;
import me.mamiiblt.instafel.utils.localization.LocalizedStringGetter;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;
import me.mamiiblt.instafel.utils.dialog.InstafelDialogMargins;
import me.mamiiblt.instafel.utils.dialog.InstafelDialogTextType;

public class ChangelogContentTask extends AsyncTask<String, Void, String> {

    private final Activity act;
    private InstafelDialog instafelDialog;
    private int ifl_version = 0;
    private int clog_version = 0;

    public ChangelogContentTask(Activity activity, int _ifl_version, int clog_version) {
        this.ifl_version = _ifl_version;
        this.instafelDialog = new InstafelDialog(activity);
        this.act = activity;
        this.clog_version = clog_version;
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
            JSONObject apiResponse = new JSONObject(result);
            byte[] dataBytes = Base64.decode(apiResponse.getString("content"), Base64.DEFAULT);
            String content = new String(dataBytes);

            PreferenceManager preferenceManager = new PreferenceManager(act);
            String languageCode = LocalizationUtils.getIflLocale(act);

            InstafelDialog instafelDialog = new InstafelDialog(act);
            instafelDialog.addSpace("top_space", 25);
            instafelDialog.addTextView(
                    "dialog_title",
                    "Changelog v" + clog_version,
                    30,
                    0,
                    InstafelDialogTextType.TITLE,
                    new InstafelDialogMargins(act, 0, 0));
            instafelDialog.addSpace("mid_space", 20);
            instafelDialog.addTextView(
                    "dialog_desc_left",
                    content,
                    16,
                    310,
                    InstafelDialogTextType.DESCRIPTION,
                    new InstafelDialogMargins(act, 24, 24));
            instafelDialog.addSpace("button_top_space", 20);
            instafelDialog.addPozitiveAndNegativeButton(
                    "buttons",
                    LocalizedStringGetter.getDialogLocalizedString(act, languageCode, "ifl_d3_02"),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            instafelDialog.dismiss();
                            preferenceManager.setPreferenceInt(PreferenceKeys.ifl_clog_last_shown_version, ifl_version);
                        }
                    },
                    null);
            instafelDialog.addSpace("bottom_space", 27);
            instafelDialog.show();
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