package instafel.app.ota.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.ViewGroup;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import instafel.app.ota.LastCheck;
import instafel.app.ui.LoadingBar;
import instafel.app.utils.dialog.InstafelDialog;

public class VersionTask extends AsyncTask<String, Void, String> {

    private final Activity act;
    private InstafelDialog instafelDialog;
    private int ifl_version = 0;
    private String ifl_type = "non_set";
    private boolean checkType;

    public VersionTask(Activity activity, String _ifl_type, int _ifl_version, boolean checkType) {
        this.ifl_type = _ifl_type;
        this.ifl_version = _ifl_version;
        this.instafelDialog = new InstafelDialog(activity);
        this.act = activity;
        this.checkType = checkType;
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
        if (this.checkType) {
            instafelDialog.addSpace("top_space", 25);
            LoadingBar loadingBar = new LoadingBar(this.act);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
            int i = (int) ((25 * this.act.getResources().getDisplayMetrics().density) + 0.5f);
            marginLayoutParams.setMargins(i, 0, i, 0);
            loadingBar.setLayoutParams(marginLayoutParams);
            instafelDialog.addCustomView("loading_bar", loadingBar);
            instafelDialog.addSpace("button_top_space", 25);
            instafelDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            LastCheck.update(act);
            if (checkType) {
                LastCheck.updateUi(act);
            }
            JSONObject jObject = new JSONObject(result);
            int lastVersion = Integer.parseInt(jObject.getString("tag_name").substring(1));
            new BuildInfoTask(act, ifl_version, ifl_type, lastVersion, instafelDialog, checkType).execute(
                    "https://api.instafel.app/content/rels/get/" + lastVersion
            );
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