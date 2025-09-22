package instafel.app.api.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import instafel.app.api.models.InstafelResponse;

public class ApiGet extends AsyncTask<String, Void, InstafelResponse> {

    private Activity activity;
    private int taskId = 0;
    private ApiCallbackInterface apiCallbackInterface = null;

    public ApiGet(Activity activity, ApiCallbackInterface apiCallbackInterface, int taskId) {
        this.activity = activity;
        this.apiCallbackInterface = apiCallbackInterface;
        this.taskId = taskId;
    }

    @Override
    protected InstafelResponse doInBackground(String... f_url) {
        InstafelResponse instafelResponse = null;
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
                instafelResponse = new InstafelResponse(response.toString());
            } else {
                Log.v("Instafel", "Request failed with code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instafelResponse;
    }

    @Override
    protected void onPostExecute(InstafelResponse instafelResponse) {
        if (apiCallbackInterface != null) {
            apiCallbackInterface.getResponse(instafelResponse, taskId);
        }
    }
}
