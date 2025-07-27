package me.mamiiblt.instafel.activity.crash_manager;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.localization.LocalizationUtils.updateIflLocale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.FileUriExposedException;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.managers.CrashManager;
import me.mamiiblt.instafel.ui.TileCompact;
import me.mamiiblt.instafel.ui.TileLarge;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.crashlog.CLogDataTypes;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;
import me.mamiiblt.instafel.utils.crashlog.Crashlog;

public class ifl_a_crash_reports extends AppCompatActivity {

    LinearLayout layoutLogs;
    List<Crashlog> crashlogs = new ArrayList<Crashlog>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_crash_reports);

        layoutLogs = findViewById(R.id.ifl_logs_layout);

        TileCompact tileDelete = findViewById(R.id.ifl_tile_crashlog_delete_logs);

        tileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashManager.removeReports(ifl_a_crash_reports.this);
                recreate();
            }
        });

        try {
            JSONArray logs = CrashManager.getLogsFromCrashFile(this);

            if (logs != null) {
                for (int i = 0; i < logs.length(); i++) {
                    JSONObject crashObject = logs.getJSONObject(i);
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
                    crashlogs.add(crashlog);
                }

                layoutLogs.removeAllViews();
                for (Crashlog crashlog : crashlogs) {
                    TileLarge crashTile = new TileLarge(this);
                    crashTile.setIconRes(R.drawable.ifl_crashlog);
                    crashTile.setTitleText(crashlog.getCrashData().getMsg().toString());
                    crashTile.setSubIconRes(R.drawable.ifl_open_in_browser);
                    crashTile.setSubtitleText("Caught at " + crashlog.getDate().toString());
                    crashTile.setSpaceBottom("visible");
                    crashTile.setOnClickListener(view -> GeneralFn.startIntentWithString(ifl_a_crash_reports.this, ifl_a_crash_viewer.class, crashlog.convertToString()));
                    layoutLogs.addView(crashTile);
                }
            } else {
                InstafelDialog.createSimpleAlertDialog(this, "Alert", "Error while loading crashlogs");
            }
        } catch (Exception e) {
            e.printStackTrace();
            InstafelDialog.createSimpleAlertDialog(this, "Alert", "Error while loading crashlogs");
        }
    }

    public void triggerCrash(View view) {
        throw new FileUriExposedException("Example Crash");
    }
}