package instafel.app.api.requests;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import instafel.app.api.models.InstafelResponse;

public class ApiPostAdmin extends AsyncTask<String, Void, InstafelResponse> {

    private String uname, pass;
    private ApiCallbackInterface apiCallbackInterface = null;
    private int taskId = 0;
    private JSONObject requestBody;

    public ApiPostAdmin(ApiCallbackInterface apiCallbackInterface, int taskId, String username, String  password, JSONObject requestBody) {
        this.apiCallbackInterface = apiCallbackInterface;
        this.taskId = taskId;
        this.uname = username;
        this.pass = password;
        this.requestBody = requestBody;
    }

    @Override
    protected InstafelResponse doInBackground(String... f_url) {
        InstafelResponse instafelResponse = null;
        try {
            URL url = new URL(f_url[0]);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("ifl-admin-username", uname);
            connection.setRequestProperty("ifl-admin-password", pass);
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

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
