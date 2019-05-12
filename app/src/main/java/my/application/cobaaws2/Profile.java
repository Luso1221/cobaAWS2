package my.application.cobaaws2;

import android.content.Intent;
import android.preference.PreferenceManager;

public class Profile {
    private Integer profile_id, interval, includeSubfolder, customSettings;
    private String profile_name, bucket_name, keyPath, folderPath;



    public Profile() {
    }

    public Intent toIntent(Intent intent){

        intent.putExtra("profile_id", profile_id);
        intent.putExtra("bucket_name", bucket_name);
        intent.putExtra("localfolder_path", folderPath);
        intent.putExtra("keypath", keyPath);
        intent.putExtra("profile_name", profile_name);
        intent.putExtra("include_subfolder", includeSubfolder);
        intent.putExtra("setting",customSettings );
        intent.putExtra("interval",interval);
        return intent;
    }

    public Integer getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(Integer profile_id) {
        this.profile_id = profile_id;
    }

    public Profile(Integer profile_id, String profileName, String bucketName, String keyPath, String folderPath,
                   Integer customSettings, int includeSubfolder, int milliseconds) {
        this.profile_id = profile_id;
        this.profile_name = profileName;
        this.bucket_name = bucketName;
        this.keyPath = keyPath;
        this.folderPath = folderPath;
        this.customSettings = customSettings;
        this.includeSubfolder = includeSubfolder;
        this.interval = milliseconds;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getBucket_name() {
        return bucket_name;
    }

    public void setBucket_name(String bucket_name) {
        this.bucket_name = bucket_name;
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

    public Boolean useCustomSettings() {

        return customSettings == 1;

    }

    @Override
    public String toString() {
        return "Profile{" +
                "profile_id=" + profile_id +
                ", interval=" + interval +
                ", includeSubfolder=" + includeSubfolder +
                ", customSettings=" + customSettings +
                ", profile_name='" + profile_name + '\'' +
                ", bucket_name='" + bucket_name + '\'' +
                ", keyPath='" + keyPath + '\'' +
                ", folderPath='" + folderPath + '\'' +
                '}';
    }

    public void setCustomSettings(Integer customSettings) {
        this.customSettings = customSettings;
    }

    public Boolean isIncludeSubfolder() {
        return includeSubfolder == 1;

    }

    public void setIncludeSubfolder(Integer includeSubfolder) {
        this.includeSubfolder = includeSubfolder;
    }
}
