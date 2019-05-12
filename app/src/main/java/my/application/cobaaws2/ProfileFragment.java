package my.application.cobaaws2;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

//import net.rdrei.android.dirchooser.DirectoryChooserActivity;
//import net.rdrei.android.dirchooser.DirectoryChooserConfig;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    //    public static String SHARED_PREFERENCE_NAME = "MyPref";
    Context context;
    private RecyclerView recyclerView;
    private ProfileAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final int RESULT_UPDATE = 44;
    private static final int RESULT_INSERT = 43;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View myFragmentView = inflater.inflate(R.layout.profile_fragment, container, false);

        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //recyclerView.setHasFixedSize(true);
        mAdapter = new ProfileAdapter(getActivity());
        for (Profile p: mAdapter.getAll()
             ) {
            Log.e(TAG, "onCreateView: "+p.toString() );
        }
        fab = (FloatingActionButton)myFragmentView.findViewById(R.id.createNewProfile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ConfigActivity.class);
                i.putExtra("mode","INSERT");
                startActivityForResult(i,RESULT_INSERT);
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        return myFragmentView;
    }

    public class ProfileAdapter extends RecyclerView.Adapter<ProfileFragment.ProfileAdapter.ViewHolder> {

        private MyDB db;
        private List<Profile> mProfiles;
        private Context mContext;

        public ProfileAdapter(Context context) {
            mContext = context;
            db = new MyDB(context);
            mProfiles = db.getAllProfiles();
        }

        public void refresh(){
            mProfiles = db.getAllProfiles();
            notifyDataSetChanged();
        }
        public List<Profile> getAll(){
            return mProfiles;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView profileName, bucketName, keyPath, folderPath;
            public Toolbar toolbar;
            public ImageButton editButton;
            public Button restoreButton;
            public Switch syncButton;

            public ViewHolder(View itemView) {
                super(itemView);

                profileName = (TextView) itemView.findViewById(R.id.toolbarText);
                bucketName= (TextView) itemView.findViewById(R.id.txt_bucket);
                keyPath= (TextView) itemView.findViewById(R.id.txt_key_path);
                folderPath= (TextView) itemView.findViewById(R.id.txt_folder);
                editButton= (ImageButton) itemView.findViewById(R.id.settingButton);
                restoreButton= (Button) itemView.findViewById(R.id.restoreButton);
                syncButton= (Switch) itemView.findViewById(R.id.syncButton);
                toolbar = (Toolbar)itemView.findViewById(R.id.toolbar);
                toolbar.inflateMenu(R.menu.menu2);
            }
        }

        @NonNull
        @Override
        public ProfileFragment.ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final Context context = parent.getContext();

            // Inflate the custom layout
            View layout = LayoutInflater.from(context).inflate(R.layout.profile_list_item, parent, false);

            // Return a new holder instance
            ProfileAdapter.ViewHolder viewHolder = new ProfileAdapter.ViewHolder(layout);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProfileFragment.ProfileAdapter.ViewHolder holder, int position) {

            Profile profile = mProfiles.get(position);

            // Set item views based on your views and data model
            TextView profile_name = holder.profileName;
            profile_name.setText(profile.getProfile_name());
            TextView bucketName = holder.bucketName;
            bucketName.setText(profile.getBucket_name());
            TextView folderPath = holder.folderPath;
            folderPath.setText(profile.getFolderPath());
            TextView keyPath = holder.keyPath;
            keyPath.setText(profile.getKeyPath());
            holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.edit:

                            Intent intent = new Intent(getActivity(),ConfigActivity.class);
                            profile.toIntent(intent);
                            intent.putExtra("mode","UPDATE");
                            startActivityForResult(intent, RESULT_UPDATE);
                            break;
                        case R.id.delete:
                            MyDB myDB = new MyDB(getActivity());
                            myDB.deleteProfile(profile.getProfile_id());
                            mAdapter.refresh();
                            break;
                    }
                    return true;
                }
            });
            holder.syncButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);
                            buttonView.setChecked(false);
                        }
                        else {
                            startAlarm(profile);
                        }
                    } else {
                        cancelAlarm(profile.getProfile_id());
                    }
                }
            });
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),ConfigActivity.class);
                    profile.toIntent(intent);
                    intent.putExtra("mode","UPDATE");
                    startActivityForResult(intent, RESULT_UPDATE);
                }
            });
            holder.restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mProfiles.size();
        }


    }
    private void editProfile(Profile profile) {
        Intent intent = new Intent(getActivity(), ConfigActivity.class);
        intent = profile.toIntent(intent);
        startActivityForResult(intent, 44);
    }
    private void startAlarm(Profile profile){
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent = profile.toIntent(intent);
        intent.putExtra("accesskey", PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("accesskey",""));
        intent.putExtra("secretkey", PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("secretkey",""));
       // intent.putExtra("interval", PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("interval",15000));

        final PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(), profile.getProfile_id(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis()+(profile.getInterval());
        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.setExact(AlarmManager.RTC_WAKEUP,
                firstMillis,pIntent);

    }

    public void cancelAlarm(Integer profile_id) {
        Log.e(TAG, "Alarm canceled ");
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(),profile_id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case RESULT_UPDATE:
                if (resultCode == Activity.RESULT_OK) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (Build.VERSION.SDK_INT >= 26) {
                        ft.setReorderingAllowed(false);
                    }
                    ft.detach(this).attach(this).commit();
                    Toast.makeText(getContext(), "Configuration successful", Toast.LENGTH_SHORT).show();
                }
                break;
            case RESULT_INSERT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getContext(), "Configuration successful", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        mAdapter.refresh();
    }
}
