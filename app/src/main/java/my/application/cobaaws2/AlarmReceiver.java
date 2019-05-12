package my.application.cobaaws2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, FolderCheckService.class);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.e(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
        i.putExtra("bucket_name",intent.getStringExtra("bucket_name"));
        i.putExtra("profile_name",intent.getStringExtra("profile_name"));
        i.putExtra("keypath",intent.getStringExtra("keypath"));
        i.putExtra("localfolder_path",intent.getStringExtra("localfolder_path"));
        i.putExtra("accesskey",intent.getStringExtra("accesskey"));
        i.putExtra("secretkey",intent.getStringExtra("secretkey"));
        context.startService(i);

        final PendingIntent pIntent = PendingIntent.getBroadcast(context, intent.getIntExtra("profile_id",0),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis;
        if(intent.getIntExtra("setting",0)==1) {
             firstMillis = System.currentTimeMillis()+intent.getIntExtra("interval",15000);
        }
        else{
             firstMillis = System.currentTimeMillis()+ PreferenceManager.getDefaultSharedPreferences(context).getInt("interval",15000);

        }
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setExact(AlarmManager.RTC_WAKEUP,
                firstMillis,pIntent);
    }
}
