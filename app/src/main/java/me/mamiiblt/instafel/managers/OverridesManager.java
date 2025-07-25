package me.mamiiblt.instafel.managers;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;

import me.mamiiblt.instafel.managers.modals.ParseResult;
import me.mamiiblt.instafel.utils.types.PreferenceKeys;

public class OverridesManager {
    private Activity act;
    private PreferenceManager preferenceManager;
    private File overrideFile;
    private File mappingFile;

    public OverridesManager(Activity act) {
        this.act = act;
        this.preferenceManager = new PreferenceManager(act);

        boolean debugModeState = preferenceManager.getPreferenceBoolean(PreferenceKeys.ifl_enable_debug_mode, false);
        if (debugModeState) {
            this.overrideFile = new File(act.getExternalFilesDir(null), "ifl_update_files/mc_overrides.json");
            this.mappingFile = new File(act.getExternalFilesDir(null), "ifl_update_files/example_mapping.json");
        } else {
            this.overrideFile = new File(act.getFilesDir() + "/mobileconfig", "mc_overrides.json");
            this.mappingFile = new File(act.getFilesDir() + "/mobileconfig", "id_name_mapping.json");
        }
    }

    public boolean existsMappingFile() {
        return mappingFile.exists();
    }

    public boolean createMappingFileDebug() {
        try {
            File folder = new File(act.getExternalFilesDir(null), "ifl_update_files");
            File iflCrashlogFile = new File(folder.getPath(), "mc_overrides.json");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            boolean fileCreated = false;
            if (!iflCrashlogFile.exists()) {
                fileCreated = iflCrashlogFile.createNewFile();
            }
            if (fileCreated) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(act, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean existsOverrideFile() {
        return overrideFile.exists();
    }

    public void deleteOverrideFile() {
        overrideFile.delete();
    }

    public JSONArray readMappingFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(mappingFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder content = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            return new JSONArray(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject readBackupFile(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     act.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            return jsonObject;
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray readMappingFileFromUri(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     act.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return new JSONArray(stringBuilder.toString());
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String writeContentIntoOverridesFile(JSONObject content) {
        try {
            FileOutputStream fos = new FileOutputStream(overrideFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            if (content.has("backup")) {
                osw.write(content.getJSONObject("backup").toString());
            } else {
                osw.write(content.toString());

            }
            osw.close();
            fos.close();
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public boolean writeContentIntoBackupFile(Uri backup_file_uri, JSONObject content) {
        try {
            OutputStream outputStream = act.getContentResolver().openOutputStream(backup_file_uri);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(content.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeContentIntoMappingFile(Uri mapping_file_url, JSONArray content) {
        try {
            OutputStream outputStream = act.getContentResolver().openOutputStream(mapping_file_url);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(content.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public JSONObject readOverrideFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(overrideFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder content = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            return new JSONObject(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject parseMappingFile(JSONArray mappingContent) {
        try {

            if (mappingContent != null) {
                JSONObject mappingObjects = new JSONObject();
                for (int i = 0; i < mappingContent.length(); i++) {
                    String mappingRaw = mappingContent.get(i).toString();
                    String flagName = mappingRaw.substring(0, 5);
                    ParseResult map = convertMappingIntoObject(mappingRaw);
                    mappingObjects.put(map.getFlagId(), map.getMappingObject());
                }
                Log.v("Instafel", "Parsed " + mappingContent.length() + " item");
                return mappingObjects;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ParseResult convertMappingIntoObject(String mappingRaw) throws JSONException {
        String[] flagSubs = mappingRaw.split(":");
        JSONObject map = new JSONObject();
        JSONObject subs = new JSONObject();
        for (int i = 2; i < flagSubs.length; i += 2) {
            subs.put(
                    flagSubs[i],
                    flagSubs[i + 1]
            );
        }
        map.put("name", flagSubs[1]);
        map.put("subs", subs);
        return new ParseResult(flagSubs[0], map);
    }

    public JSONArray getOverrideKeyValues(String string) {
        try {
            JSONArray array = new JSONArray(string);
            JSONArray parsed = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                String[] parts = array.get(i).toString().split(": : ");
                JSONArray parsedItem = new JSONArray();
                parsedItem.put(parts[0]);
                parsedItem.put(parts[1]);
                parsed.put(parsedItem);
            }
            return parsed;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
