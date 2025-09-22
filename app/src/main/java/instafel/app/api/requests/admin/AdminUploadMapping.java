package instafel.app.api.requests.admin;

import android.os.AsyncTask;
import android.util.Log;
import instafel.app.InstafelEnv;
import instafel.app.api.models.InstafelResponse;
import instafel.app.api.requests.ApiCallbackInterface;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AdminUploadMapping extends AsyncTask<String, Void, InstafelResponse> {

    private String uname, pass;
    private ApiCallbackInterface apiCallbackInterface = null;
    private int taskId = 0;
    private File mappingFile;
    private String resp;

    public AdminUploadMapping(ApiCallbackInterface apiCallbackInterface, int taskId, String username, String  password, File mappingFile) {
        this.apiCallbackInterface = apiCallbackInterface;
        this.taskId = taskId;
        this.uname = username;
        this.pass = password;
        this.mappingFile = mappingFile;
    }

    @Override
    protected InstafelResponse doInBackground(String... f_url) {
        InstafelResponse instafelResponse = null;
        try {
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            String LINE_FEED = "\r\n";
            URL url = new URL(f_url[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ifl-admin-username", uname);
            conn.setRequestProperty("ifl-admin-password", pass);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setDoOutput(true);

            try (OutputStream output = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {

                writer.append("--").append(boundary).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"mapping\"; filename=\"")
                        .append(mappingFile.getName()).append("\"").append(LINE_FEED);
                writer.append("Content-Type: application/json").append(LINE_FEED);
                writer.append(LINE_FEED).flush();

                try (FileInputStream inputStream = new FileInputStream(mappingFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    output.flush();
                }

                writer.append(LINE_FEED).flush();

                String[][] textFields = {
                        {"ig_version", InstafelEnv.IG_VERSION},
                        {"ifl_version", InstafelEnv.IFL_VERSION}
                };

                for (String[] field : textFields) {
                    writer.append("--").append(boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"").append(field[0]).append("\"").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.append(field[1]).append(LINE_FEED).flush();
                }

                writer.append("--").append(boundary).append("--").append(LINE_FEED).flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                resp = response.toString();
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
            apiCallbackInterface.getResponse(resp, taskId);
        }
    }
}
