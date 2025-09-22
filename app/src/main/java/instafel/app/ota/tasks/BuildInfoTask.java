package instafel.app.ota.tasks;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import instafel.app.R;
import instafel.app.managers.NotificationOtaManager;
import instafel.app.managers.PreferenceManager;
import instafel.app.utils.localization.LocalizationUtils;
import instafel.app.utils.localization.LocalizedStringGetter;
import instafel.app.utils.types.PreferenceKeys;
import instafel.app.utils.dialog.InstafelDialog;
import instafel.app.utils.dialog.InstafelDialogMargins;
import instafel.app.utils.dialog.InstafelDialogTextType;

public class BuildInfoTask extends AsyncTask<String, Void, String> {

    private final Activity act;

    InstafelDialog versionDialog;
    InstafelDialog instafelDialog;
    int ifl_version = 0;
    String ifl_type = "non_set";
    int lastVersion = 0;
    boolean checkType;

    public BuildInfoTask(Activity activity, int _ifl_version, String _ifl_type, int _lastVersion, InstafelDialog versionDialog, boolean checkType) {
        this.lastVersion = _lastVersion;
        this.ifl_version = _ifl_version;
        this.ifl_type = _ifl_type;
        this.act = activity;
        this.versionDialog = versionDialog;
        this.instafelDialog = new InstafelDialog(act);
        this.checkType = checkType;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return sendGetRequest(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Instafel", e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        versionDialog.dismiss();

        try {
            JSONObject buildInfo = new JSONObject(result);

            String igVersion = buildInfo.getJSONObject("patcher_data").getJSONObject("ig").getString("version");

            String apkContentLink = "";
            if (ifl_type.equals("Unclone")) {
                apkContentLink = buildInfo.getJSONObject("links").getString("unclone");
            } else if (ifl_type.equals("Clone")) {
                apkContentLink = buildInfo.getJSONObject("links").getString("clone");
            }


            if (lastVersion > ifl_version) {
                String languageCode = LocalizationUtils.getIflLocale(act);

                String localizedUpdateText = LocalizedStringGetter.getLocalizedString(
                        act,
                        LocalizationUtils.getIflLocale(act),
                        "ifl_d1_02",
                        lastVersion,
                        igVersion
                );
                if (localizedUpdateText == null) localizedUpdateText = "ifl_err";

                instafelDialog.addSpace("top_space", 25);
                instafelDialog.addTextView(
                        "dialog_title",
                        LocalizedStringGetter.getDialogLocalizedString(act, languageCode, "ifl_d1_01"),
                        30,
                        0,
                        InstafelDialogTextType.TITLE,
                        new InstafelDialogMargins(act, 0, 0));
                instafelDialog.addSpace("mid_space", 20);
                instafelDialog.addTextView(
                        "dialog_desc",
                        localizedUpdateText,
                        16,
                        310,
                        InstafelDialogTextType.DESCRIPTION,
                        new InstafelDialogMargins(act, 24, 24));
                instafelDialog.addSpace("button_top_space", 20);
                String finalApkContentLink = apkContentLink;
                instafelDialog.addPozitiveAndNegativeButton(
                        "buttons",
                        LocalizedStringGetter.getDialogLocalizedString(act, languageCode, "ifl_d1_04"),
                        LocalizedStringGetter.getDialogLocalizedString(act, languageCode, "ifl_d1_03"),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PreferenceManager preferenceManager = new PreferenceManager(act);
                                boolean backgroundStatus = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_ota_background_enable, false);
                                if (backgroundStatus) {
                                    NotificationOtaManager notificationOtaManager = new NotificationOtaManager(act);
                                    notificationOtaManager.createNotificationChannel();

                                    notificationOtaManager.sendNotification(act);

                                    instafelDialog.dismiss();
                                    DownloadUpdateTask fileDownloadTask = new DownloadUpdateTask("NEW", act, null, null, null, null);
                                    fileDownloadTask.execute(finalApkContentLink);

                                } else {
                                    InstafelDialog instafelDownloadTrackDialog = new InstafelDialog(act);
                                    instafelDownloadTrackDialog.addSpace("top_space", 25);
                                    instafelDownloadTrackDialog.addTextView(
                                            "dialog_title",
                                            "ifl_status",
                                            30,
                                            0,
                                            InstafelDialogTextType.TITLE,
                                            new InstafelDialogMargins(act, 0, 0));
                                    instafelDownloadTrackDialog.addSpace("mid_space", 3);
                                    instafelDownloadTrackDialog.addTextView(
                                            "dialog_byte_status",
                                            "ifl_size",
                                            18,
                                            0,
                                            InstafelDialogTextType.SUBTEXT,
                                            new InstafelDialogMargins(act, 0, 0));
                                    instafelDownloadTrackDialog.addSpace("mid_space", 20);

                                    ProgressBar progressBar = new ProgressBar(act, null, android.R.attr.progressBarStyleHorizontal);
                                    LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(
                                            instafelDownloadTrackDialog.convertToDp(310),
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    );
                                    float scale = act.getResources().getDisplayMetrics().density;
                                    int widthInPx = (int) (310 * scale + 0.5f);
                                    progressBarParams.width = widthInPx;
                                    progressBarParams.setMarginStart(instafelDialog.convertToDp(24));
                                    progressBarParams.setMarginEnd(instafelDialog.convertToDp(24));
                                    progressBar.setLayoutParams(progressBarParams);
                                    progressBar.setIndeterminate(false);
                                    progressBar.setMax(100);
                                    progressBar.setProgress(21);
                                    Drawable drawable = null;
                                    if (instafelDownloadTrackDialog.getDialogThemeMode() == 2) {
                                        drawable = act.getResources().getDrawable(R.drawable.ifl_progress_background_light);
                                    } else {
                                        drawable = act.getResources().getDrawable(R.drawable.ifl_progress_background_dark);
                                    }
                                    progressBar.setProgressDrawable(drawable);

                                    instafelDownloadTrackDialog.addCustomView("progress_bar", progressBar);
                                    instafelDownloadTrackDialog.addSpace("bottom_space", 25);
                                    instafelDialog.dismiss();
                                    instafelDownloadTrackDialog.show();

                                    progressBar.setProgress(0);
                                    TextView sizeProccess = instafelDownloadTrackDialog.getTextView("dialog_byte_status");
                                    TextView downloadProcess = instafelDownloadTrackDialog.getTextView("dialog_title");

                                    downloadProcess.setText("0%");
                                    sizeProccess.setText("0/0 MB");
                                    progressBar.setMax(100);
                                    progressBar.setProgress(0);

                                    DownloadUpdateTask fileDownloadTask = new DownloadUpdateTask("OLD", act, progressBar, downloadProcess, instafelDownloadTrackDialog, sizeProccess);
                                    fileDownloadTask.execute(finalApkContentLink);
                                }
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                instafelDialog.dismiss();
                            }
                        });
                instafelDialog.addSpace("bottom_space", 27);
                instafelDialog.show();
            } else {
                if (checkType) {
                    Dialog dialog1 = new Dialog(act);
                    dialog1.setContentView(R.layout.ifl_dg_ota_uptodate);
                    dialog1.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog1.getWindow().setBackgroundDrawable(act.getDrawable(R.drawable.ifl_dg_ota_background));
                    dialog1.setCancelable(false);

                    LinearLayout button1 = dialog1.findViewById(R.id.ifl_dg_button_okay);
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });
                    dialog1.show();
                }
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