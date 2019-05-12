package my.application.cobaaws2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "S3sync";

    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE_TABLE_PROFILE =
            "create table Profiles( profile_id integer primary key," +
                    "profile_name text unique, " +
                    "bucket_name text," +
                    "key_path text," +
                    "localfolder_path text," +
                    "include_subfolder integer," +
                    "setting integer," +
                    "interval integer);";
    private static final String DATABASE_CREATE_TABLE_SYNC =
            "create table Synchronizations( sync_id integer primary key," +
                    "status integer," +
                    "time_start text," +
                    "time_end text);";
    private static final String DATABASE_CREATE_TABLE_DETAILS =
            "create table SynchronizationDetails( sync_id integer," +
                    "size integer," +
                    "timestamp text," +
                    "status text," +
                    "file_name text," +
                    "checksum text primary key," +
                    "foreign key(sync_id) references synchronizations(sync_id))";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TABLE_SYNC);
        database.execSQL(DATABASE_CREATE_TABLE_PROFILE);
        database.execSQL(DATABASE_CREATE_TABLE_DETAILS);
    }

    // Method is called during an upgrade of the database,
    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion){
        Log.w(MyDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS Profiles");
        onCreate(database);
    }
}
