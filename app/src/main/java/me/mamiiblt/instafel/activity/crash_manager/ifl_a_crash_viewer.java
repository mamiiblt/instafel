package me.mamiiblt.instafel.activity.crash_manager;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONObject;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.ui.TileCompact;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.crashlog.CLogDataTypes;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;
import me.mamiiblt.instafel.utils.crashlog.Crashlog;

public class ifl_a_crash_viewer extends AppCompatActivity {

    private TileLarge tileInfo, tileStackTrace, tileAndroidVersion, tileIflIg, tileCrashDate;
    private TileCompact tileCopy, tileShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_crash_viewer);

        tileInfo = findViewById(R.id.ifl_tile_crashviewer_msg);
        tileStackTrace = findViewById(R.id.ifl_tile_crashviewer_stacktrace);
        tileAndroidVersion = findViewById(R.id.ifl_tile_crashviewer_aver);
        tileIflIg = findViewById(R.id.ifl_tile_crashviewer_iflIgVersion);
        tileCopy = findViewById(R.id.ifl_tile_crashviewer_copy);
        tileShare = findViewById(R.id.ifl_tile_crashviewer_share);
        tileCrashDate = findViewById(R.id.ifl_tile_crashviewer_crashDate);
        
        try {
            Intent intent = this.getIntent();
            String data = intent.getStringExtra("data");

            JSONObject crashObject = new JSONObject(data);
            Crashlog crashlog = new Crashlog(
                    new CLogDataTypes.AppData(
                            crashObject.getJSONObject("appData").get("ifl_ver"),
                            crashObject.getJSONObject("appData").get("ig_ver"),
                            crashObject.getJSONObject("appData").get("ig_ver_code"),
                            crashObject.getJSONObject("appData").get("ig_itype")
                    ),
                    new CLogDataTypes.DeviceData(
                            crashObject.getJSONObject("deviceData").get("aver"),
                            crashObject.getJSONObject("deviceData").get("sdk"),
                            crashObject.getJSONObject("deviceData").get("model"),
                            crashObject.getJSONObject("deviceData").get("brand"),
                            crashObject.getJSONObject("deviceData").get("product")

                    ),
                    new CLogDataTypes.CrashData(
                            crashObject.getJSONObject("crashData").get("msg"),
                            crashObject.getJSONObject("crashData").get("trace"),
                            crashObject.getJSONObject("crashData").get("class")
                    ),
                    crashObject.get("date")
            );
            String valueIsNull = "value is null";
            if (crashlog.getDeviceData().getAver() != JSONObject.NULL) {
                tileAndroidVersion.setSubtitleText("Android " + crashlog.getDeviceData().getAver().toString() + " (SDK " + crashlog.getDeviceData().getSdk().toString() + ")");
            } else {
                tileAndroidVersion.setSubtitleText(valueIsNull);
            }
            if (crashlog.getCrashData().getTrace() != JSONObject.NULL) {
                tileStackTrace.setSubtitleText(crashlog.getCrashData().getTrace().toString());
            } else {
                tileStackTrace.setSubtitleText(valueIsNull);
            }

            if (crashlog.getAppData().getIfl_ver() != JSONObject.NULL && crashlog.getAppData().getIg_ver() != JSONObject.NULL && crashlog.getAppData().getIg_ver_code() != JSONObject.NULL) {
                tileIflIg.setSubtitleText(crashlog.getAppData().getIg_ver() + " (v" + crashlog.getAppData().getIfl_ver().toString() + ")");
            } else {
                tileIflIg.setSubtitleText(valueIsNull);
            }
            if (crashlog.getDate() != JSONObject.NULL) {
                tileCrashDate.setSubtitleText(crashlog.getDate().toString());
            } else {
                tileCrashDate.setSubtitleText(valueIsNull);
            }
            if (crashlog.getCrashData().getMsg() != JSONObject.NULL) {
                tileInfo.setSubtitleText(crashlog.getCrashData().getMsg().toString());
            } else {
                tileInfo.setSubtitleText(valueIsNull);
            }

            this.tileStackTrace.setOnClickListener(view -> ((ClipboardManager) ifl_a_crash_viewer.this.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("ifl_log_clip", ifl_a_crash_viewer.this.tileStackTrace.getSubtitle())));
            this.tileInfo.setOnClickListener(view -> ((ClipboardManager) ifl_a_crash_viewer.this.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("ifl_log_clip", ifl_a_crash_viewer.this.tileInfo.getSubtitle())));
            String stringLog = createStringLog(crashlog);
            tileCopy.setOnClickListener(v -> copyText(stringLog));

            tileShare.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, stringLog);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            });
        } catch (Exception e) {
            InstafelDialog.createSimpleAlertDialog(this, "Alert", "Error while loading crashlog");
            e.printStackTrace();
        }
    }

    public String createStringLog(Crashlog crashlog) {
        String _stringContent = "";
        _stringContent = _stringContent + "Instafel Crashlog\n";
        _stringContent = _stringContent + crashlog.getDate().toString() + "\n";
        _stringContent = _stringContent + "\n";
        _stringContent = _stringContent + "# APP\n";
        _stringContent = addValue(_stringContent, "IFL_VER", crashlog.getAppData().getIfl_ver().toString());
        _stringContent = addValue(_stringContent, "IG_VER",  crashlog.getAppData().getIg_ver().toString());
        _stringContent = addValue(_stringContent, "IG_VER_CODE",  crashlog.getAppData().getIg_ver_code().toString());
        _stringContent = addValue(_stringContent, "TYPE",  crashlog.getAppData().getIg_itype().toString());

        _stringContent = _stringContent + "\n";
        _stringContent = _stringContent + "# DEVICE\n";
        _stringContent = addValue(_stringContent, "ANDROID_VERSION",  crashlog.getDeviceData().getAver().toString());
        _stringContent = addValue(_stringContent, "ANDROID_SDK",  crashlog.getDeviceData().getSdk().toString());
        _stringContent = addValue(_stringContent, "MODEL",  crashlog.getDeviceData().getModel().toString());
        _stringContent = addValue(_stringContent, "PRODUCT",  crashlog.getDeviceData().getProduct().toString());
        _stringContent = addValue(_stringContent, "BRAND",  crashlog.getDeviceData().getBrand().toString());

        _stringContent = _stringContent + "\n";
        _stringContent = _stringContent + "# CRASH\n";
        _stringContent = addValue(_stringContent, "MSG", crashlog.getCrashData().getMsg().toString());
        _stringContent = addValue(_stringContent, "CLASS", crashlog.getCrashData().getClassName().toString());
        _stringContent = addValue(_stringContent, "TRACE", crashlog.getCrashData().getTrace().toString());

        return _stringContent;
    }

    public void copyText(String string) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ifl_log_clip", string);
        clipboard.setPrimaryClip(clip);
    }

    private String addValue(String s1, String key, String value) {
        return s1 + key + "=" + value + "\n";
    }
}