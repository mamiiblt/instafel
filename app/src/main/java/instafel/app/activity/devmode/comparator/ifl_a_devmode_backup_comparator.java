package instafel.app.activity.devmode.comparator;

import static instafel.app.utils.GeneralFn.updateIflUi;
import static instafel.app.utils.localization.LocalizationUtils.updateIflLocale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import instafel.app.R;
import instafel.app.managers.OverridesManager;
import instafel.app.ui.PageContentArea;

public class ifl_a_devmode_backup_comparator extends AppCompatActivity {

    private OverridesManager overridesManager;
    private PageContentArea layoutLoading, layoutContents;
    private LinearLayout layoutItems;
    private JSONObject mappingContent, backup1, backup2;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, false);
        setContentView(R.layout.ifl_at_devmode_backup_comparator);

        overridesManager = new OverridesManager(this);
        layoutContents = findViewById(R.id.ifl_page_area);
        layoutLoading = findViewById(R.id.ifl_loading_area);
        layoutItems = findViewById(R.id.ifl_flags_layout);
    }

    private boolean isResumed = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (!isResumed) {
            isResumed = true;

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    int taskResult = 0;
                    String resultString = null;
                    try {
                        if (overridesManager.existsMappingFile()) {
                            Intent intent = getIntent();
                            backup1 = new JSONObject(intent.getStringExtra("backup1"));
                            backup2 = new JSONObject(intent.getStringExtra("backup2"));
                            mappingContent = overridesManager.parseMappingFile(overridesManager.readMappingFile());


                        } else {
                            taskResult = -1;
                            resultString = ifl_a_devmode_backup_comparator.this.getString(R.string.ifl_a11_34);
                        }
                    } catch (Exception e) {
                        taskResult = -1;
                        resultString = ifl_a_devmode_backup_comparator.this.getString(R.string.ifl_a11_77);
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}