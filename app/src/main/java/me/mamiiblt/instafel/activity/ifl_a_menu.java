package me.mamiiblt.instafel.activity;

import static me.mamiiblt.instafel.utils.GeneralFn.updateIflUi;
import static me.mamiiblt.instafel.utils.Localizator.updateIflLocale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import me.mamiiblt.instafel.R;
import me.mamiiblt.instafel.activity.about.ifl_a_about;
import me.mamiiblt.instafel.activity.admin.ifl_a_admin_dashboard;
import me.mamiiblt.instafel.activity.admin.ifl_a_admin_login;
import me.mamiiblt.instafel.activity.crash_manager.ifl_a_crash_reports;
import me.mamiiblt.instafel.activity.devmode.ifl_a_devmode;
import me.mamiiblt.instafel.activity.library.ifl_a_library_menu;
import me.mamiiblt.instafel.InstafelEnv;
import me.mamiiblt.instafel.managers.PreferenceManager;
import me.mamiiblt.instafel.ui.TileSocials;
import me.mamiiblt.instafel.utils.GeneralFn;
import me.mamiiblt.instafel.utils.InstafelAdminUser;
import me.mamiiblt.instafel.utils.PreferenceKeys;
import me.mamiiblt.instafel.utils.dialog.InstafelDialog;

public class ifl_a_menu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateIflUi(this);
        updateIflLocale(this, true);
        setContentView(R.layout.ifl_at_menu);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        boolean showAdminDash = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_show_admin_dash_as_tile, false);
        if (showAdminDash) {
            findViewById(R.id.ifl_tile_menu_adminmenu).setVisibility(View.VISIBLE);
            findViewById(R.id.ifl_tile_menu_adminmenu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAdminDashboard();
                }
            });
        }

        boolean showDebugWarning = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_enable_debug_mode, false);
        if (showDebugWarning)  {
            boolean showDebugDialogWarning = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_debug_mode_warning_dialog, false);
            if (!showDebugDialogWarning) {
                InstafelDialog instafelDialog = InstafelDialog.createSimpleDialog(ifl_a_menu.this,
                        "Warning",
                        "Debug mode enabled, this mode not designed for Instagram runtime. If you don't know what are you doing, disable this mode!",
                        "Okay",
                        null,
                        null,
                        null
                );
                instafelDialog.show();
                preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_debug_mode_warning_dialog, true);
            }
        }

        TileSocials tileSocials = findViewById(R.id.ifl_tile_menu_sections);

        tileSocials.getTileLanguage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFn.startIntent(ifl_a_menu.this, ifl_a_language.class);
            }
        });

        tileSocials.getTileInfo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFn.startIntent(ifl_a_menu.this, ifl_a_about.class);
            }
        });

        tileSocials.getTileChat().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFn.openInWebBrowser(ifl_a_menu.this, "https://t.me/instafel");
            }
        });

        tileSocials.getTileGuide().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFn.openInWebBrowser(ifl_a_menu.this, "https://instafel.app/wiki");
            }
        });

        tileSocials.getTileGithub().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralFn.openInWebBrowser(ifl_a_menu.this, "https://github.com/mamiiblt/instafel");
            }
        });

        findViewById(R.id.ifl_tile_menu_crashlogs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFn.startIntent(ifl_a_menu.this, ifl_a_crash_reports.class);
            }
        });
        
        findViewById(R.id.ifl_tile_menu_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFn.startIntent(ifl_a_menu.this, ifl_a_library_menu.class);
            }
        });

        tileSocials.getTileInfo().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (InstafelEnv.PRODUCTION_MODE) {
                    openAdminDashboard();
                } else {
                    Toast.makeText(ifl_a_menu.this, "Admin dasboard isn't available on custom generations.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        findViewById(R.id.ifl_tile_menu_misc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFn.startIntent(ifl_a_menu.this, ifl_a_misc.class);
            }
        });

        findViewById(R.id.ifl_tile_menu_devopt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFn.startIntent(ifl_a_menu.this, ifl_a_devmode.class);
            }
        });

        findViewById(R.id.ifl_tile_menu_ota).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralFn.startIntent(ifl_a_menu.this, ifl_a_ota.class);
            }
        });
    }

    public void openAdminDashboard() {
        if (!InstafelAdminUser.isUserLogged(ifl_a_menu.this)) {
            GeneralFn.startIntent(ifl_a_menu.this, ifl_a_admin_login.class);
        } else {
            GeneralFn.startIntent(ifl_a_menu.this, ifl_a_admin_dashboard.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager preferenceManager = new PreferenceManager(this);
        boolean prefData = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_ui_recreate, false);
        if (prefData) {
            recreate();
            preferenceManager.setPreferenceBoolean(PreferenceKeys.ifl_ui_recreate, false);
        }
    }
}