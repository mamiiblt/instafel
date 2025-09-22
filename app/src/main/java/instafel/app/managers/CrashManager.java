package instafel.app.managers;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Locale;

import instafel.app.ota.IflEnvironment;

public class CrashManager {

    private Context ctx;

    public CrashManager(Context ctx) {
        this.ctx = ctx;
    }

    public void saveLog(Throwable throwable) {
        try {
            JSONObject logObject = new JSONObject();
            JSONObject appData = new JSONObject();
            appData.put("ifl_ver", IflEnvironment.getIflVersion(ctx));
            appData.put("ig_ver", IflEnvironment.getIgVersion(ctx));
            appData.put("ig_ver_code", IflEnvironment.getIgVerCode(ctx));
            appData.put("ig_itype", IflEnvironment.getType(ctx));
            logObject.put("appData", appData);

            JSONObject deviceData = new JSONObject();
            deviceData.put("aver", Build.VERSION.RELEASE != null ? Build.VERSION.RELEASE : JSONObject.NULL);
            deviceData.put("sdk", Build.VERSION.SDK_INT != 0 ? Build.VERSION.SDK_INT : JSONObject.NULL);
            deviceData.put("model", Build.MODEL != null ? Build.MODEL : JSONObject.NULL);
            deviceData.put("brand", Build.BRAND != null ? Build.BRAND : JSONObject.NULL);
            deviceData.put("product", Build.PRODUCT != null ? Build.PRODUCT : JSONObject.NULL);
            logObject.put("deviceData", deviceData);

            String sStackTrace = Log.getStackTraceString(throwable);
            JSONObject crashData = new JSONObject();
            String message = throwable.getMessage();
            crashData.put("msg", message != null ? message : JSONObject.NULL);
            crashData.put("trace", sStackTrace);
            throwable.getClass();
            String throwableClass = throwable.getClass().getName();
            crashData.put("class", throwableClass);
            logObject.put("crashData", crashData);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            logObject.put("date", sdf.format(new Date()));

            addLogIntoCrashFile(logObject);
        } catch (Exception e) {
            Toast.makeText(ctx, "Error while creating crashlog object", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static JSONArray getLogsFromCrashFile(Context ctx) {
        try {
            File folder = new File(ctx.getExternalFilesDir(null), "ifl_update_files");
            File iflCrashlogFile = new File(folder.getPath(), "ifl_crashlogs.json");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (!iflCrashlogFile.exists()) {
                iflCrashlogFile.createNewFile();
            }

            FileInputStream fis = new FileInputStream(iflCrashlogFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            fis.close();

            if (builder.toString().isEmpty()) {
                return new JSONArray("[]");
            } else {
                return new JSONArray(builder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void removeReports(Context ctx) {
        try {
            File folder = new File(ctx.getExternalFilesDir(null), "ifl_update_files");
            File iflCrashlogFile = new File(folder.getPath(), "ifl_crashlogs.json");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (iflCrashlogFile.exists()) {
                iflCrashlogFile.delete();
                iflCrashlogFile.createNewFile();
            }
        } catch (Exception e) {
            Toast.makeText(ctx, "Error while deleting crashlogs", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void addLogIntoCrashFile(JSONObject logObject) {
        try {
            File folder = new File(ctx.getExternalFilesDir(null), "ifl_update_files");
            File iflCrashlogFile = new File(folder.getPath(), "ifl_crashlogs.json");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            if (!iflCrashlogFile.exists()) {
                iflCrashlogFile.createNewFile();
            }

            FileInputStream fis = new FileInputStream(iflCrashlogFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            fis.close();

            String fileContent = builder.toString();
            JSONArray jsonArray;
            if (fileContent.isEmpty()) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(fileContent);
            }
            
            JSONArray newArray = new JSONArray();
            newArray.put(logObject);

            if (jsonArray.length() >= 11) {
                jsonArray.remove(7);
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                newArray.put(jsonArray.get(i));
            }

            FileOutputStream fos = new FileOutputStream(iflCrashlogFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(newArray.toString());
            osw.close();
            fos.close();
        } catch (Exception e) {
            Toast.makeText(ctx, "Error while saving crashlog", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
