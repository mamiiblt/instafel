package me.mamiiblt.instafel.api.requests;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.mamiiblt.instafel.api.models.InstafelResponse;

public class ApiGetAdmin extends AsyncTask<String, Void, InstafelResponse> {

    private String uname, pass;
    private ApiCallbackInterface apiCallbackInterface = null;
    private int taskId = 0;

    public ApiGetAdmin(ApiCallbackInterface apiCallbackInterface, int taskId, String username, String  password) {
        this.apiCallbackInterface = apiCallbackInterface;
        this.taskId = taskId;
        this.uname = username;
        this.pass = password;
    }

    @Override
    protected InstafelResponse doInBackground(String... f_url) {
        InstafelResponse instafelResponse = null;
        try {
            URL url = new URL(f_url[0]);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("ifl-admin-username", uname);
            connection.setRequestProperty("ifl-admin-password", pass);

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
