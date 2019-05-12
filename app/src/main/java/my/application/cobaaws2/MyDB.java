package my.application.cobaaws2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class MyDB {

    private static final String TAG = "MyDB";
    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    public final static String PROFILE_TABLE= "Profiles";
    public final static String SYNC_TABLE = "Synchronizations";
    public final static String DETAIL_TABLE= "SynchronizationDetail";

    public final static String PROFILE_ID=  "profile_id";
    public final static String PROFILE_NAME="profile_name";
    public final static String BUCKET_NAME="bucket_name";
    public final static String KEY_PATH="key_path";
    public final static String LOCALFOLDER_PATH="localfolder_path";
    public final static String INCLUDE_SUBFOLDER="include_subfolder";
    public final static String SETTING="setting";
    public final static String INTERVAL="interval";

    public final static String SYNC_ID = "sync_id";
    public final static String STATUS = "status";
    public final static String TIMESTAMP_START= "timestamp_start";
    public final static String TIMESTAMP_END= "timestamp_end";

    public final static String FILESIZE = "filesize";
    public final static String FILEPATH = "filepath";
    public final static String FILENAME = "filename";
    public final static String CHECKSUM = "checksum";
    public final static String TIMESTAMP = "timestamp";

    /**
     *
     * @param context
     */
    public MyDB(Context context){
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public int getMax(){

        String sql = "SELECT MAX("+PROFILE_ID+") as MAX from Profiles";
        Cursor mCursor = database.rawQuery(sql,null);
        mCursor.moveToFirst();
        Log.e(TAG, "getMax: " + mCursor.getColumnName(0) );
        int max = mCursor.getInt(mCursor.getColumnIndex("MAX"));
        mCursor.close();
        return max;
    }

    public long insertProfile(String profile_name,
                              String bucket, String folder, String keypath,
                              Boolean include_subfolder, Boolean setting, Integer interval){
        ContentValues values = new ContentValues();
        values.put(PROFILE_ID, getMax()+1);
        values.put(PROFILE_NAME, profile_name);
        values.put(BUCKET_NAME, bucket);
        values.put(KEY_PATH, keypath);
        values.put(LOCALFOLDER_PATH, folder);
        values.put(INCLUDE_SUBFOLDER, include_subfolder ? 1 : 0);
        values.put(SETTING, setting ? 1 : 0);
        values.put(INTERVAL, interval );
        return database.insert(PROFILE_TABLE, null, values);
    }
    public long insertSync(Integer profile_id, Timestamp timestamp_start, Double timestamp_end, Integer status){
        ContentValues values = new ContentValues();
        values.put(SYNC_ID, getMax()+1);
        values.put(PROFILE_ID, profile_id);
        values.put(TIMESTAMP_START, timestamp_start.toString());
        values.put(TIMESTAMP_END, timestamp_end.toString());
        values.put(STATUS, status);

        return database.insert(SYNC_TABLE, null, values);
    }
    public long insertDetail(Integer sync_id, Timestamp t, FileData fileData){
        ContentValues values = new ContentValues();
        values.put(SYNC_ID, getMax()+1);
        values.put(TIMESTAMP, t.toString());
        values.put(FILENAME, fileData.getFileName());
        values.put(FILEPATH, fileData.getFilePath());
        values.put(FILESIZE, fileData.getFileSize());
        values.put(CHECKSUM, fileData.getChecksum_hash());

        return database.insert(DETAIL_TABLE, null, values);
    }

    public long updateProfile(Integer profile_id, String profile_name,
                              String bucket, String folder, String keypath,
                              Boolean setting, Boolean include_subfolder, Integer interval){
        ContentValues values = new ContentValues();
        values.put(PROFILE_ID, profile_id);
        values.put(PROFILE_NAME, profile_name);
        values.put(BUCKET_NAME, bucket);
        values.put(KEY_PATH, keypath);
        values.put(LOCALFOLDER_PATH, folder);
        values.put(SETTING, setting ? 1 : 0);
        values.put(INCLUDE_SUBFOLDER, include_subfolder ? 1 : 0);
        values.put(INTERVAL, interval);
        return database.update(PROFILE_TABLE,  values, "profile_id="+profile_id,null);
    }

    public long deleteProfile(Integer profile_id){

        return database.delete(PROFILE_TABLE,  "profile_id="+profile_id,null);
    }
    public long deleteSync(Integer sync_id){

        return database.delete(SYNC_TABLE,  "sync_id="+sync_id,null);
    }
    public Profile getProfile(Integer profile_id){

        String[] cols = new String[] {PROFILE_ID, PROFILE_NAME,
                BUCKET_NAME, KEY_PATH, LOCALFOLDER_PATH, INCLUDE_SUBFOLDER, SETTING,INTERVAL};
        Cursor mCursor = database.query(PROFILE_TABLE,cols,
                PROFILE_ID + "=?",
                new String[]{String.valueOf(profile_id)}, null, null, null, null);

        if (mCursor != null)
            mCursor.moveToFirst();

        // prepare note object
        Profile profile = new Profile();
        profile.setProfile_id(mCursor.getInt(mCursor.getColumnIndex(PROFILE_ID)));
        profile.setProfile_name(mCursor.getString(mCursor.getColumnIndex(PROFILE_NAME)));
        profile.setBucket_name(mCursor.getString(mCursor.getColumnIndex(BUCKET_NAME)));
        profile.setKeyPath(mCursor.getString(mCursor.getColumnIndex(KEY_PATH)));
        profile.setFolderPath(mCursor.getString(mCursor.getColumnIndex(LOCALFOLDER_PATH)));
        profile.setCustomSettings(mCursor.getInt(mCursor.getColumnIndex(SETTING)));
        profile.setIncludeSubfolder(mCursor.getInt(mCursor.getColumnIndex(INCLUDE_SUBFOLDER)));
        profile.setInterval(mCursor.getInt(mCursor.getColumnIndex(INTERVAL)));

        // close the db connection
        mCursor.close();

        return profile;
    }
    public List<Synchronization> getAllSync(){
        String[] cols = new String[] {SYNC_ID, STATUS, TIMESTAMP_START, TIMESTAMP_END};
        Cursor mCursor = database.query(true, SYNC_TABLE,cols,null
                , null, null, null, SYNC_ID, null);

        List<Synchronization> synchronizations = new ArrayList<Synchronization>();
        if (mCursor.moveToFirst()) {
            do {
                Synchronization sync = new Synchronization();
                sync.setSync_id(mCursor.getInt(mCursor.getColumnIndex(SYNC_ID)));
                sync.setStatus(mCursor.getInt(mCursor.getColumnIndex(STATUS)));

                String UTC_TIMEZONE_NAME = "UTC";
                DateFormat dateFormat = SimpleDateFormat.getDateInstance();
                dateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE_NAME));
                String tStart = mCursor.getString(mCursor.getColumnIndex(TIMESTAMP_START));
                String tEnd = mCursor.getString(mCursor.getColumnIndex(TIMESTAMP_END));
                try {
                    sync.setTimestamp_start(new Timestamp(dateFormat.parse(tStart).getTime()));
                    sync.setTimestamp_end(new Timestamp(dateFormat.parse(tEnd).getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                };

                synchronizations.add(sync);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return synchronizations;
    }

    public SynchronizationDetail getLastSynced(Integer sync_id){
        String[] cols = new String[] {SYNC_ID, FILESIZE, FILENAME, FILEPATH, TIMESTAMP, CHECKSUM, STATUS};
        Cursor mCursor = database.query(true, DETAIL_TABLE,cols,
                SYNC_ID + "=?"
                , new String[]{String.valueOf(sync_id)}, null, null, SYNC_ID + " DESC", "10");

        List<SynchronizationDetail> syncDetails = new ArrayList<SynchronizationDetail>();
        if (mCursor.moveToFirst()) {
            do {
                SynchronizationDetail syncDetail = new SynchronizationDetail();
                syncDetail.setFileData(new FileData(
                                mCursor.getString(mCursor.getColumnIndex(FILENAME)),
                                mCursor.getInt(mCursor.getColumnIndex(FILESIZE)),
                                mCursor.getString(mCursor.getColumnIndex(FILEPATH)),
                                mCursor.getString(mCursor.getColumnIndex(CHECKSUM))
                        )
                );
                syncDetail.setStatus(mCursor.getInt(mCursor.getColumnIndex(STATUS)));

                String UTC_TIMEZONE_NAME = "UTC";
                DateFormat dateFormat = SimpleDateFormat.getDateInstance();
                dateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE_NAME));
                String t= mCursor.getString(mCursor.getColumnIndex(TIMESTAMP));
                try {
                    syncDetail.setTimestamp(new Timestamp(dateFormat.parse(t).getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                };

                syncDetails.add(syncDetail);
            } while (mCursor.moveToNext());
        }
        return syncDetails.get(0);
    }
    public List<SynchronizationDetail> getAllDetails(Integer sync_id){
        String[] cols = new String[] {SYNC_ID, FILESIZE, FILENAME, FILEPATH, TIMESTAMP, CHECKSUM, STATUS};
        Cursor mCursor = database.query(true, DETAIL_TABLE,cols,
                SYNC_ID + "=?"
                , new String[]{String.valueOf(sync_id)}, null, null, SYNC_ID + " DESC", "10");

        List<SynchronizationDetail> syncDetails = new ArrayList<SynchronizationDetail>();
        if (mCursor.moveToFirst()) {
            do {
                SynchronizationDetail syncDetail = new SynchronizationDetail();
                syncDetail.setFileData(new FileData(
                        mCursor.getString(mCursor.getColumnIndex(FILENAME)),
                mCursor.getInt(mCursor.getColumnIndex(FILESIZE)),
                        mCursor.getString(mCursor.getColumnIndex(FILEPATH)),
                        mCursor.getString(mCursor.getColumnIndex(CHECKSUM))
                )
                );
                syncDetail.setStatus(mCursor.getInt(mCursor.getColumnIndex(STATUS)));

                String UTC_TIMEZONE_NAME = "UTC";
                DateFormat dateFormat = SimpleDateFormat.getDateInstance();
                dateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE_NAME));
                String t= mCursor.getString(mCursor.getColumnIndex(TIMESTAMP));
                try {
                    syncDetail.setTimestamp(new Timestamp(dateFormat.parse(t).getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                };

                syncDetails.add(syncDetail);
            } while (mCursor.moveToNext());
        }
        return syncDetails;
    }
    public List<Profile> getAllProfiles() {
        String[] cols = new String[] {PROFILE_ID, PROFILE_NAME,
                BUCKET_NAME, KEY_PATH, LOCALFOLDER_PATH, INCLUDE_SUBFOLDER, SETTING,INTERVAL};
        Cursor mCursor = database.query(true, PROFILE_TABLE,cols,null
                , null, null, null, PROFILE_ID, null);

        List<Profile> profiles = new ArrayList<Profile>();
        if (mCursor.moveToFirst()) {
            do {
                Profile profile = new Profile();
                profile.setProfile_id(mCursor.getInt(mCursor.getColumnIndex(PROFILE_ID)));
                profile.setProfile_name(mCursor.getString(mCursor.getColumnIndex(PROFILE_NAME)));
                profile.setBucket_name(mCursor.getString(mCursor.getColumnIndex(BUCKET_NAME)));
                profile.setKeyPath(mCursor.getString(mCursor.getColumnIndex(KEY_PATH)));
                profile.setFolderPath(mCursor.getString(mCursor.getColumnIndex(LOCALFOLDER_PATH)));
                profile.setCustomSettings(mCursor.getInt(mCursor.getColumnIndex(SETTING)));
                profile.setIncludeSubfolder(mCursor.getInt(mCursor.getColumnIndex(INCLUDE_SUBFOLDER)));
                profile.setInterval(mCursor.getInt(mCursor.getColumnIndex(INTERVAL)));

                profiles.add(profile);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return profiles;
    }
}
