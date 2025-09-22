package instafel.app.api.models;

import org.json.JSONObject;

public class AutoUpdateInfo {
    private String backup_id;
    private String backup_name;
    private int current_backup_version;

    public AutoUpdateInfo(String backup_id, String backup_name, int current_backup_version) {
        this.backup_id = backup_id;
        this.backup_name = backup_name;
        this.current_backup_version = current_backup_version;
    }

    public String getBackup_id() {
        return backup_id;
    }

    public int getCurrent_backup_version() {
        return current_backup_version;
    }

    public String getBackup_name() {
        return backup_name;
    }

    public void setCurrent_backup_version(int current_backup_version) {
        this.current_backup_version = current_backup_version;
    }

    public String exportAsJsonString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", backup_id);
            jsonObject.put("name", backup_name);
            jsonObject.put("current_version", current_backup_version);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "CANNOT_BE_CONVERTED";
        }
    }
}
