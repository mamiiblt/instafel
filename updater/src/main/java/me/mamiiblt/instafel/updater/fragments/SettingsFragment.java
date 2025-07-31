package me.mamiiblt.instafel.updater.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.widget.Toast;

import com.github.tttt55.materialyoupreferences.preferences.MaterialListPreference;
import com.github.tttt55.materialyoupreferences.preferences.MaterialPreference;
import com.github.tttt55.materialyoupreferences.preferences.MaterialSwitchGooglePreference;

import me.mamiiblt.instafel.updater.BuildConfig;
import me.mamiiblt.instafel.updater.MainActivity;
import me.mamiiblt.instafel.updater.R;
import me.mamiiblt.instafel.updater.update.UpdateWorkHelper;
import me.mamiiblt.instafel.updater.utils.LocalizationUtils;
import me.mamiiblt.instafel.updater.utils.Utils;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.app_options, rootKey);
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        MaterialSwitchGooglePreference dynamicColorPreference = findPreference("material_you");
        if (dynamicColorPreference != null) {
            dynamicColorPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isDynamicColorEnabled = (Boolean) newValue;

                sharedPreferences.edit().putBoolean("material_you", isDynamicColorEnabled).apply();

                getActivity().recreate();
                return true;
            });
        }

        MaterialListPreference installationMet = findPreference("installation_method");

        String cachedOption = sharedPreferences.getString("checker_method", "NONE");
        switch (cachedOption) {
            case "root":
                installationMet.setValue("Root");
                break;
            case "shi":
                installationMet.setValue("Shizuku");
                break;
            default:
                installationMet.setValue("Shizuku");
        }

        installationMet.setOnPreferenceChangeListener((preference, newValue) -> {
            switch (String.valueOf(newValue)) {
                case "Root":
                    prefEditor.putString("checker_method", "root");
                    break;
                case "Shizuku":
                    prefEditor.putString("checker_method", "shi");
                    break;
            }
            prefEditor.apply();
            Toast.makeText(getContext(), getActivity().getString(R.string.work_restarted), Toast.LENGTH_SHORT).show();
            UpdateWorkHelper.restartWork(getActivity());
            return true;
        });

        installationMet.setValue(sharedPreferences.getString("checker_method", "shi").equals("root") ? "Root" : "Shizuku");

        MaterialListPreference checkerInterval = findPreference("checker_interval");
        checkerInterval.setOnPreferenceChangeListener((preference, newValue) -> {
            prefEditor.putString("checker_interval", String.valueOf(newValue)).apply();
            prefEditor.apply();
            Toast.makeText(getContext(), getActivity().getString(R.string.work_restarted), Toast.LENGTH_SHORT).show();
            UpdateWorkHelper.restartWork(getActivity());
            return true;
        });

        MaterialListPreference language = findPreference("language");
        language.setOnPreferenceChangeListener((preference, newValue) -> {
            prefEditor.putString("language", String.valueOf(newValue)).apply();
            prefEditor.apply();
            LocalizationUtils localizationUtils = new LocalizationUtils(getActivity().getApplicationContext());
            localizationUtils.updateAppLanguage();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            // getActivity().recreate();
            return true;
        });

        MaterialPreference sourceCode = findPreference("source_code");
        sourceCode.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/mamiiblt/instafel"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            return false;
        });

        MaterialPreference createIssue = findPreference("create_issue");
        createIssue.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/mamiiblt/instafel/issues"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            return false;
        });

        MaterialPreference updaterGuide = findPreference("updater_guide");
        updaterGuide.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://instafel.app/about_updater"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            return false;
        });
        MaterialPreference appVersion = findPreference("app_version");
        appVersion.setSummary(getActivity().getString(R.string.app_version_s, BuildConfig.VERSION_NAME, BuildConfig.COMMIT, BuildConfig.BRANCH));
        appVersion.setOnPreferenceClickListener(preference -> {
            Utils.showAppInfoDialog(getActivity());
            return false;
        });
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof ListPreference) {
            showPreferenceDialog((ListPreference) preference);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    private void showPreferenceDialog(ListPreference preference) {
        DialogFragment dialogFragment = new me.mamiiblt.instafel.updater.utils.MaterialListPreference();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getParentFragmentManager(), "androidx.preference.PreferenceFragment.DIALOG");
    }
}