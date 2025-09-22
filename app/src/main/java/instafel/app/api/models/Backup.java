package instafel.app.api.models;

import org.json.JSONObject;


public class Backup extends BackupListItem {

    private int manifestVersion, backupVersion;
    private String versionName;
    private String description, lastEdit, changelog;
    private boolean optionalShowAuthorSocials;
    private JSONObject authorSocials = null;


    public Backup(String repo, String name, String author) {
        super(repo, name, author);
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    public int getManifestVersion() {

        return manifestVersion;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getBackupVersion() {
        return backupVersion;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOptionalShowAuthorSocials() {
        return optionalShowAuthorSocials;
    }

    public JSONObject getAuthorSocials() {
        return authorSocials;
    }

    public void setManifestVersion(int manifestVersion) {
        this.manifestVersion = manifestVersion;
    }

    public void setBackupVersion(int backupVersion) {
        this.backupVersion = backupVersion;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOptionalShowAuthorSocials(boolean optionalShowAuthorSocials) {
        this.optionalShowAuthorSocials = optionalShowAuthorSocials;
    }

    public void setAuthorSocials(JSONObject optionalValue_authorSocials) {
        this.authorSocials = optionalValue_authorSocials;
    }
}
