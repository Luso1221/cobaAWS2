package my.application.cobaaws2;

public class Profile {
    private String profileName, bucketName, keyPath, folderPath;
    private int interval;
    private boolean customSettings, includeSubfolder;

    public Profile(String profileName, String bucketName, String keyPath, String folderPath,
                   boolean customSettings, boolean includeSubfolder, int milliseconds) {
        this.profileName = profileName;
        this.bucketName = bucketName;
        this.keyPath = keyPath;
        this.folderPath = folderPath;
        this.customSettings = customSettings;
        this.includeSubfolder = includeSubfolder;
        this.interval = milliseconds;
    }

    public String getProfileName() {
        return profileName;
    }

    public int getInterval() {
        return interval;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public boolean isCustomSettings() {
        return customSettings;
    }

    public void setCustomSettings(boolean customSettings) {
        this.customSettings = customSettings;
    }

    public boolean isIncludeSubfolder() {
        return includeSubfolder;
    }

    public void setIncludeSubfolder(boolean includeSubfolder) {
        this.includeSubfolder = includeSubfolder;
    }
}
