package instafel.app.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import instafel.app.InstafelEnv;
import instafel.app.api.models.InstafelResponse;
import instafel.app.api.requests.ApiCallbackInterface;
import instafel.app.api.requests.ApiPostAdmin;
import instafel.app.api.requests.admin.AdminUploadMapping;
import instafel.app.managers.OverridesManager;
import instafel.app.managers.PreferenceManager;
import instafel.app.utils.types.PreferenceKeys;
import org.json.JSONObject;

import java.io.*;
import java.security.NoSuchAlgorithmException;

public class UploadMapping implements ApiCallbackInterface {

    private final String BASE_URL = "https://api.mamii.me";
    private final String LOG_TAG = "IFL_ADMIN_MPU";
    private final Activity act;
    private final OverridesManager overridesManager;
    private final PreferenceManager preferenceManager;
    private String newMappingHash;
    private String aUsername, aPassword;

    public UploadMapping(Activity activity) {
        this.act = activity;
        this.overridesManager = new OverridesManager(act);
        this.preferenceManager = new PreferenceManager(activity);
        startCheck();
    }

    private void startCheck() {
        try {
            if (isUpdateNeeded()) {
                Log.i(LOG_TAG, "Mapping file uploading into repository");
                aUsername = preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_username, "null");
                aPassword = preferenceManager.getPreferenceString(PreferenceKeys.ifl_admin_password, "null");

                JSONObject reqBody = new JSONObject();
                reqBody.put("ig_version", InstafelEnv.IG_VERSION);

                new ApiPostAdmin(this, 17, aUsername, aPassword, reqBody).execute(
                        BASE_URL + "/ifl/admin/user/mapping-is-exists"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "Error occurred while uploading mapping file into server.");
            Toast.makeText(act, "Error occurred while uploading mapping file into server.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isUpdateNeeded() throws IOException, NoSuchAlgorithmException {
        String savedMappingHash = preferenceManager.getPreferenceString(PreferenceKeys.ifl_mapping_file_hash, "");
        File mappingFile = overridesManager.getMappingFile();
        if (mappingFile == null || !mappingFile.exists()) {
            Log.i(LOG_TAG, "Mapping file isn't downloaded yet by MobileConfig, it will be checked in next start.");
            return false;
        }
        newMappingHash = GeneralFn.getFileHash(mappingFile);

        return !newMappingHash.equals(savedMappingHash);
    }

    @Override
    public void getResponse(InstafelResponse instafelResponse, int taskId) {
        if (instafelResponse != null) {

            switch (taskId) {
                case 17:
                    switch (instafelResponse.getStatus()) {
                        case "EXIST":
                            Log.i(LOG_TAG, "Mapping already uploaded by other admin, don't needed.");
                            break;
                        case "FAILURE":
                            Toast.makeText(act, "Error while checking mapping status.", Toast.LENGTH_LONG).show();
                            break;
                        case "NOT_EXIST":
                            Log.i(LOG_TAG, "Mapping uploading request is sending...");
                            new AdminUploadMapping(this, 10,
                                    aUsername, aPassword, overridesManager.getMappingFile()).execute(
                                    BASE_URL + "/ifl/admin/user/upload-mapping"
                            );
                            break;
                    }
                    break;
                case 10:
                    if (instafelResponse.getStatus().equals("SUCCESS")) {
                        Toast.makeText(act, "Mapping uploaded into server successfully.", Toast.LENGTH_LONG).show();
                        preferenceManager.setPreferenceString(PreferenceKeys.ifl_mapping_file_hash, newMappingHash);
                    } else {
                        Toast.makeText(act, "Error occurred while uploading mapping to server.", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } else {
            Toast.makeText(act, "Error while parsing upload mapping response", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void getResponse(String rawResponse, int taskId) { }
}