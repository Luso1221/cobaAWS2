package my.application.cobaaws2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import net.rdrei.android.dirchooser.DirectoryChooserActivity;
//import net.rdrei.android.dirchooser.DirectoryChooserConfig;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    //    public static String SHARED_PREFERENCE_NAME = "MyPref";
    private static boolean SYNCING = false;
    Context context;
    Switch syncButton;
    ImageButton settingButton;
    //ProgressBar progressBar;
    TextView folder,bucket,keypath;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View myFragmentView = inflater.inflate(R.layout.profile_fragment, container, false);


        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        folder = (TextView)myFragmentView.findViewById(R.id.txt_folder);
        bucket= (TextView)myFragmentView.findViewById(R.id.txt_bucket);
        keypath = (TextView)myFragmentView.findViewById(R.id.txt_key_path);
        folder.setText(prefs.getString("folder", ""));
        bucket.setText(prefs.getString("bucket", ""));
        keypath.setText(prefs.getString("keypath",""));
        //progressBar = (ProgressBar)myFragmentView.findViewById(R.id.progressBar);
        //labelProgress = (TextView) myFragmentView.findViewById(R.id.labelProgress);
//        ((Button) myFragmentView.findViewById(R.id.saveButtonAccessKey)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleSave((Button)view);
//            }
//        });
        //syncSwitch = (Switch)myFragmentView.findViewById(R.id.syncSwitch);
        settingButton = (ImageButton) myFragmentView.findViewById(R.id.settingButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(myFragmentView.getContext(), ConfigActivity.class);
                startActivityForResult(intent, 44);
            }
        });
        syncButton = (Switch) myFragmentView.findViewById(R.id.syncButton);
        syncButton.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    startAlarm(new Profile("Test",
                            bucket.getText().toString(),
                            keypath.getText().toString(),
                            folder.getText().toString(),
                            false,false, 1*1000));
                } else {
                    cancelAlarm();
                }
            }
        });
        return myFragmentView;
    }
    private void startAlarm(Profile profile){
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("folder", profile.getFolderPath());
        intent.putExtra("bucket", profile.getBucketName());
        intent.putExtra("name", profile.getProfileName());
        intent.putExtra("keypath", profile.getKeyPath());
        intent.putExtra("accesskey", PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("accesskey",""));
        intent.putExtra("secretkey", PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("secretkey",""));
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(), AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis()+5*1000; // alarm is set right away
        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setExact(AlarmManager.RTC_WAKEUP,
                firstMillis,pIntent);
    }
    public void cancelAlarm() {
        Log.e(TAG, "Alarm canceled ");
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(), AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 44:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                if (resultCode == getActivity().RESULT_OK) {
                    ((TextView) getActivity().findViewById(R.id.txt_folder)).setText(prefs.getString("folder", ""));
                    ((TextView) getActivity().findViewById(R.id.txt_bucket)).setText(prefs.getString("bucket", ""));
                    ((TextView) getActivity().findViewById(R.id.txt_key_path)).setText(prefs.getString("keypath", ""));

                    Toast.makeText(getContext(), "Configuration successful", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
