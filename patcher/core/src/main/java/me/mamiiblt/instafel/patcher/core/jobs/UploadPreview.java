package me.mamiiblt.instafel.patcher.core.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import me.mamiiblt.instafel.patcher.core.source.WorkingDir;
import me.mamiiblt.instafel.patcher.core.utils.Env;
import me.mamiiblt.instafel.patcher.core.utils.Log;
import me.mamiiblt.instafel.patcher.core.utils.Utils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadPreview {

    private final OkHttpClient httpClient = new OkHttpClient();
    private boolean isProdMode = false;
    private File F_BUILD_INFO, APK_UC, APK_C;
    private JSONObject buildInfo = null;
    private File buildFolder = null;
    private String GEN_ID;
    private String GITHUB_PAT;

    public void run(File workingDir, String PVERSION, String PCOMMIT) {
        try {
            Env.PROJECT_DIR = WorkingDir.getExistsWorkingDir(workingDir);
            Env.Project.setupEnv();
            ;
            Env.Config.setupConfig();

            isProdMode = Env.Config.getBoolean(Env.Config.Keys.prod_mode, false);
            GITHUB_PAT = Env.Config.getString(Env.Config.Keys.github_pat, "NULL");

            if (isProdMode) {
                this.buildFolder = new File(Utils.mergePaths(Env.PROJECT_DIR, "build"));
                if (buildFolder.exists()) {
                    loadFiles();
                    createRelease(PVERSION, PCOMMIT);
                } else {
                    Log.severe("/build folder doesn't exist.");
                }
            } else {
                Log.severe("You are not using production env..!:");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.severe("Error while uploading preview.");
        }
    }

    private void loadFiles() throws IOException {
        Log.info("Loading build files...");
        this.F_BUILD_INFO = new File(Utils.mergePaths(buildFolder.getAbsolutePath(), "build_info.json"));
        String jsonStr = new String(Files.readAllBytes(Paths.get(F_BUILD_INFO.getAbsolutePath())));
        this.buildInfo = new JSONObject(jsonStr);
        JSONObject fnames = buildInfo.getJSONObject("fnames");
        this.APK_UC = new File(Utils.mergePaths(buildFolder.getAbsolutePath(), fnames.getString("unclone")));
        this.APK_C = new File(Utils.mergePaths(buildFolder.getAbsolutePath(), fnames.getString("clone")));
        JSONObject patcherData = buildInfo.getJSONObject("patcher_data");
        this.GEN_ID = patcherData.getJSONObject("ifl").getString("gen_id");
        Log.info("Build files loaded");
    }

    private void createRelease(String PVERSION, String PCOMMIT) throws IOException {
        JSONObject pData = buildInfo.getJSONObject("patcher_data");
        String[] bLines = {
                "# Build Information",
                "| PROPERTY  | VALUE |",
                "| ------------- | ------------- |",
                "| GENERATION_ID  | " + pData.getJSONObject("ifl").getString("gen_id") + " |",
                "| BUILD_TS  | " + pData.getString("build_date") + " |",
                "| IFL_VERSION  | " + pData.getJSONObject("ifl").getInt("version") + " |",
                "| IG_VERSION  | " + pData.getJSONObject("ig").getString("version") + " |",
                "| IG_VER_CODE  | " + pData.getJSONObject("ig").getString("ver_code") + " |",
                "| MD5_HASH_UC | " + buildInfo.getJSONObject("hash").getString("unclone") + " |",
                "| MD5_HASH_C | " + buildInfo.getJSONObject("hash").getString("clone") + " |\n",
                "Generated with **Instafel Patcher** v" + PVERSION + " (" + PCOMMIT + "/release" + ")"
        };
        String body = String.join("\n", bLines);
        JSONObject req = new JSONObject();
        req.put("tag_name", GEN_ID);
        req.put("name", "Preview of " + pData.getJSONObject("ig").getString("version"));
        req.put("body", body);
        req.put("draft", false);
        req.put("prerelease", false);
        req.put("generate_release_notes", false);

        RequestBody requestBody = RequestBody.create(req.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/mamiiblt/instafel_previews/releases")
                .addHeader("Authorization", "Bearer " + GITHUB_PAT)
                .addHeader("Accept", "application/vnd.github+json")
                .addHeader("X-GitHub-Api-Version", "2022-11-28")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject resp = new JSONObject(response.body().string());
                Log.info("Release created!");
                String uploadUrl = resp.getString("upload_url").replace("{?name,label}", "?name=%s");

                Log.info("Uploading assets...");
                uploadAsset(uploadUrl, APK_UC);
                uploadAsset(uploadUrl, APK_C);
                uploadAsset(uploadUrl, F_BUILD_INFO);
                Log.info("Assets uploaded");
                sendLogToTelegram();
            } else {
                Log.severe("Error while creating release: " + response.code() + " " + response.message());
                Log.severe(response.body().string());
            }
        }
    }

    private void sendLogToTelegram() {
        String url = "https://api.mamii.me/ifl/manager_new/sendGeneratedLogTg";
        RequestBody requestBody = RequestBody.create(buildInfo.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", Env.Config.getString(Env.Config.Keys.manager_token, "null"))
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Request unsuccesfull: " + response.code());
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.severe("Error while sending request.");
        }
    }

    private String uploadAsset(String assetUploadUrl, File file) throws IOException {
        Log.info("Uploading file " + file.getName());
        String url = String.format(assetUploadUrl, file.getName());

        RequestBody reqBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + GITHUB_PAT)
                .header("Accept", "application/vnd.github+json")
                .post(reqBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Log.info("Asset " + file.getName() + " uploaded.");
                JSONObject resp = new JSONObject(response.body().string());
                return resp.getString("browser_download_url");
            } else {
                Log.severe("Error while uploading asset: " + file.getName() + " - " + response.code() + " - "
                        + response.body().string());
            }
        }

        return null;
    }
}
