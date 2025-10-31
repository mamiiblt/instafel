package me.mamiiblt.instafel.updater.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.os.LocaleList;
import android.util.Log;
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
import me.mamiiblt.instafel.updater.R;
import me.mamiiblt.instafel.updater.update.UpdateWorkHelper;
import me.mamiiblt.instafel.updater.utils.LocalizationUtils;
import me.mamiiblt.instafel.updater.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.*;

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

        MaterialListPreference installType = findPreference("checker_type");
        if (installType != null) {
            List<CharSequence> entriesList = new ArrayList<>();
            List<CharSequence> entryValuesList = new ArrayList<>();

            entriesList.add(getString(R.string.unclone));
            entryValuesList.add("unclone");
            entriesList.add(getString(R.string.clone));
            entryValuesList.add("clone");

            installType.setEntries(entriesList.toArray(new CharSequence[0]));
            installType.setEntryValues(entryValuesList.toArray(new CharSequence[0]));

            installType.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.equals("unclone")) {
                    prefEditor.putInt("install_type_i", 11);
                } else {
                    prefEditor.putInt("install_type_i", 22);
                }

                prefEditor.apply();

                Toast.makeText(getContext(), getActivity().getString(R.string.work_restarted), Toast.LENGTH_SHORT).show();
                UpdateWorkHelper.restartWork(getActivity());
                return true;
            });
        }

        MaterialListPreference checkerInterval = findPreference("checker_interval");
        if (checkerInterval != null) {
            Integer[] hourEntries = {2,3,4,6,8,12,14,24};

            List<CharSequence> entriesList = new ArrayList<>();
            List<CharSequence> entryValuesList = new ArrayList<>();

            for (int hourEntry : hourEntries) {
                entriesList.add(getString(R.string.hour_imz, hourEntry));
                entryValuesList.add(String.valueOf(hourEntry));
            }

            checkerInterval.setDefaultValue(entryValuesList.get(0));
            checkerInterval.setEntries(entriesList.toArray(new CharSequence[0]));
            checkerInterval.setEntryValues(entryValuesList.toArray(new CharSequence[0]));

            checkerInterval.setOnPreferenceChangeListener((preference, newValue) -> {
                int intValue;
                try {
                    intValue = Integer.parseInt(String.valueOf(newValue));
                } catch (NumberFormatException e) {
                    return false;
                }

                prefEditor.putInt("checker_interval_i", intValue);
                prefEditor.apply();

                Toast.makeText(getContext(), getActivity().getString(R.string.work_restarted), Toast.LENGTH_SHORT).show();
                UpdateWorkHelper.restartWork(getActivity());
                return true;
            });
        }

        MaterialListPreference language = findPreference("language");
        if (language != null) {
            LocaleList languages = LocalizationUtils.getSupportedLocaleList(requireActivity());

            List<String> entriesList = new ArrayList<>();
            List<String> entryValuesList = new ArrayList<>();

            for (int i = 0; i < languages.size(); i++) {
                Locale locale = languages.get(i);
                String displayName = LocalizationUtils.getLanguageDisplayName(locale);
                entriesList.add(displayName);
                entryValuesList.add(LocalizationUtils.convertToLangCode(locale));
            }

            language.setEntries(entriesList.toArray(new CharSequence[0]));
            language.setEntryValues(entryValuesList.toArray(new CharSequence[0]));

            language.setOnPreferenceChangeListener((preference, newValue) -> {
                Log.i("IFLU", "Application language changed to " + newValue);
                LocalizationUtils.setAppLocale(requireActivity(), (String) newValue);
                requireActivity().recreate();
                return true;
            });
        }

        MaterialPreference sourceCode = findPreference("source_code");
        sourceCode.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/mamiiblt/instafel"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            return false;
        });

        MaterialPreference helpTranslation = findPreference("help_translation");
        helpTranslation.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://crowdin.com/project/instafel"));
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
        appVersion.setSummary(getActivity().getString(R.string.app_version_s, BuildConfig.IFLU_VERSION, BuildConfig.BUILD_TYPE));
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