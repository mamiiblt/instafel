package me.mamiiblt.instafel.updater.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import me.mamiiblt.instafel.updater.MainActivity;
import me.mamiiblt.instafel.updater.update.UpdateWorkHelper;
import me.mamiiblt.instafel.updater.utils.CommandOutput;
import me.mamiiblt.instafel.updater.utils.LogUtils;
import me.mamiiblt.instafel.updater.R;
import me.mamiiblt.instafel.updater.utils.RootManager;
import me.mamiiblt.instafel.updater.utils.Utils;
import rikka.shizuku.Shizuku;

public class InfoFragment extends Fragment {

    private static boolean rootStatus = false;

    private TextView viewStatusDesc, viewStatusTitle, viewIType, viewStatus, viewBatteryStatus;
    private Button viewStartBtn, viewStopBtn;
    private SharedPreferences sharedPreferences;
    public String STRING_AUTHORIZED, STRING_NOT_INSTALLED, STRING_START_SERVICE, STRING_UNAUTHORIZED, STRING_STOPPED, STRING_RESTRICTED, STRING_NOT_FOUND, STRING_UNRESTICTED;
    public ImageView iconProvider, warIconProvider, warIconBattery;
    private LogUtils logUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_info, container, false);
       viewStatusTitle = view.findViewById(R.id.viewStatusTitle);
       viewStatusDesc = view.findViewById(R.id.statusTextView);
       viewIType = view.findViewById(R.id.statusTextView3);
       viewStatus = view.findViewById(R.id.statusTextView4);
       viewStartBtn = view.findViewById(R.id.startButton);
       viewStopBtn = view.findViewById(R.id.stopButton);
       viewBatteryStatus = view.findViewById(R.id.statusTextView5);
       iconProvider = view.findViewById(R.id.iview_1);
       warIconProvider = view.findViewById(R.id.wview_1);
       warIconBattery = view.findViewById(R.id.wview_2);
       logUtils = new LogUtils(getActivity());

       Context ctx = getContext();
       STRING_AUTHORIZED = ctx.getString(R.string.authorized);
       STRING_NOT_INSTALLED = ctx.getString(R.string.not_installed);
       STRING_START_SERVICE = ctx.getString(R.string.start_service);
       STRING_UNAUTHORIZED = ctx.getString(R.string.unauthorized);
       STRING_NOT_FOUND = ctx.getString(R.string.not_found);
       STRING_STOPPED = ctx.getString(R.string.stopped);
       STRING_RESTRICTED = ctx.getString(R.string.battery_restricted);
       STRING_UNRESTICTED = ctx.getString(R.string.battery_unrestricted);

       sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
       SharedPreferences.Editor prefEditor = sharedPreferences.edit();

       int iType = sharedPreferences.getInt("install_type_i", 0);
       String iTypeString = iType == 11 ? getString(R.string.unclone) : getString(R.string.clone);
       viewIType.setText(iTypeString);

       if (Utils.getBatteryRestrictionStatus(getActivity())) {
           viewBatteryStatus.setText(STRING_RESTRICTED);
           warIconBattery.setVisibility(View.VISIBLE);
           view.findViewById(R.id.battery_status).setOnClickListener(view1 -> Utils.showBatteryDialog(getActivity()));
       } else {
           viewBatteryStatus.setText(STRING_UNRESTICTED);
           warIconBattery.setVisibility(View.GONE);
       }

       if (Utils.getMethod(getContext()) == 1) {
           viewStatusTitle.setText(this.getString(R.string.root_status));
           iconProvider.setImageDrawable(getActivity().getDrawable(R.drawable.root));
           warIconProvider.setVisibility(View.GONE);

           if (RootManager.isDeviceRooted(getActivity())) {
               CommandOutput commandOutput = RootManager.execSuCommands("su -v");
               if (commandOutput.getExitCode() == 0) {
                   rootStatus = true;
                   String[] outputParts = commandOutput.getLog().trim().split("\n");
                   viewStatusDesc.setText("✔ " + outputParts[0].trim());
               } else {
                   rootStatus = false;
                   viewStatusDesc.setText(STRING_UNAUTHORIZED);
                   warIconProvider.setVisibility(View.VISIBLE);
               }
           } else {
               rootStatus = false;
               viewStatusDesc.setText(STRING_NOT_FOUND);
               warIconProvider.setVisibility(View.VISIBLE);
               Utils.showDialog(getActivity(), getString(R.string.root_access_ensure_issue), getString(R.string.root_access_ensure_issue_desc));
           }
       } else {
           viewStatusTitle.setText(this.getString(R.string.shizuku_status));
           iconProvider.setImageDrawable(getActivity().getDrawable(R.drawable.shizuku));
           warIconProvider.setVisibility(View.GONE);
           if (Utils.isShizukuInstalled(getActivity())) {
               if (Shizuku.pingBinder()) {
                   if (Utils.hasShizukuPermission()) {
                       viewStatusDesc.setText(STRING_AUTHORIZED);
                   } else {
                       viewStatusDesc.setText(STRING_UNAUTHORIZED);
                       warIconProvider.setVisibility(View.VISIBLE);
                       view.findViewById(R.id.root_status).setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               if (Utils.hasShizukuPermission()) {
                                   viewStatusDesc.setText(STRING_AUTHORIZED);
                                   warIconProvider.setVisibility(View.GONE);
                               } else {
                                   Shizuku.requestPermission(100);
                               }
                           }
                       });
                   }
               } else {
                   Toast.makeText(ctx, ctx.getString(R.string.please_start_shizuku), Toast.LENGTH_SHORT).show();
                   warIconProvider.setVisibility(View.VISIBLE);
                   viewStatusDesc.setText(STRING_START_SERVICE);
               }
           } else {
               viewStatusDesc.setText(STRING_NOT_INSTALLED);
               warIconProvider.setVisibility(View.VISIBLE);
               view.findViewById(R.id.root_status).setOnClickListener(view2 -> Utils.openPlayStore(getActivity()));
           }
       }

       updateUI();

       viewStartBtn.setOnClickListener(view3 -> {
           if (!Utils.getBatteryRestrictionStatus(getActivity())) {
               if (Utils.getMethod(getContext()) == 1) {
                   if (rootStatus) {
                       logUtils.w(getContext().getString(R.string.upd_started));
                       UpdateWorkHelper.scheduleWork(getActivity());
                       updateUI();
                   } else {
                       Toast.makeText(ctx, getString(R.string.please_install_root), Toast.LENGTH_SHORT).show();
                   }
               } else {
                   if (!Utils.getBatteryRestrictionStatus(getActivity())) {
                       if (Utils.isShizukuInstalled(getActivity())) {
                           if (Shizuku.pingBinder()) {
                               if (Utils.hasShizukuPermission()) {
                                   logUtils.w(getContext().getString(R.string.upd_started));
                                   UpdateWorkHelper.scheduleWork(getActivity());
                                   updateUI();
                               } else {
                                   Toast.makeText(ctx, ctx.getString(R.string.please_give_permission), Toast.LENGTH_SHORT).show();
                                   Utils.openShizuku(ctx);
                               }
                           } else {
                               Toast.makeText(ctx, ctx.getString(R.string.please_start_shizuku), Toast.LENGTH_SHORT).show();
                               Utils.openShizuku(ctx);
                           }
                       } else {
                           Toast.makeText(ctx, ctx.getString(R.string.please_install_shizuku), Toast.LENGTH_SHORT).show();
                           Utils.openPlayStore(ctx);
                       }
                   } else {
                       Toast.makeText(ctx, ctx.getString(R.string.please_allow_unrestiracted), Toast.LENGTH_SHORT).show();
                       Utils.showBatteryDialog(ctx);
                   }
               }
           } else {
               Toast.makeText(ctx, ctx.getString(R.string.please_allow_unrestiracted), Toast.LENGTH_SHORT).show();
               Utils.showBatteryDialog(ctx);
           }
       });

       viewStopBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               logUtils.w(getContext().getString(R.string.upd_stopped));
               UpdateWorkHelper.cancelWork(getActivity());
               updateUI();
           }
       });
       return view;
    }

    private void updateUI() {
        UpdateWorkHelper.isWorkManagerActive(getActivity(), new UpdateWorkHelper.WorkManagerActiveCallback() {
            @Override
            public void onResult(boolean isActive) {
                if (isActive) {
                    if (sharedPreferences.getBoolean("15m_rule", false)) {
                        viewStatus.setText(getContext().getString(R.string.running, "15 " + getContext().getString(R.string.minute)));
                    } else {
                        viewStatus.setText(getContext().getString(R.string.running, sharedPreferences.getString("checker_interval", "NULL")));
                    }
                    viewStartBtn.setEnabled(false);
                    viewStopBtn.setEnabled(true);
                } else {
                    viewStatus.setText(STRING_STOPPED);
                    viewStartBtn.setEnabled(true);
                    viewStopBtn.setEnabled(false);
                }
            }
        });
    }
}