package my.application.cobaaws2;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
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
        i.putExtra("bucket",intent.getStringExtra("bucket"));
        i.putExtra("keypath",intent.getStringExtra("keypath"));
        i.putExtra("folder",intent.getStringExtra("folder"));
        i.putExtra("accesskey",intent.getStringExtra("accesskey"));
        i.putExtra("secretkey",intent.getStringExtra("secretkey"));
        context.startService(i);
    }
}
