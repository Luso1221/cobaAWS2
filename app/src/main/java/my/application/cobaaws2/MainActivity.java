package my.application.cobaaws2;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends MenuActivity {

    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        init();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setup();
    }
    private void setup(){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        if(
                SP.getString("accesskey","").equals("")
        ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View credentialsDialog = inflater.inflate(R.layout.credentials_dialog, null);

            builder.setView(credentialsDialog);

            EditText editText1 = (EditText)credentialsDialog.findViewById(R.id.txt_access_key);
            EditText editText2 = (EditText)credentialsDialog.findViewById(R.id.txt_secret_key);

            builder.setTitle("Enter your credentials")
                    .setCancelable(false)
                    .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Authenticate(getApplicationContext()).execute(editText1.getText().toString(),
                            editText2.getText().toString());
                }
            });
            AlertDialog credentialsInput = builder.create();
            credentialsInput.show();

            View newProfile = inflater.inflate(R.layout.config_activity,null);
            builder.setView(newProfile);
            builder.setTitle("Create your first profile")
                    .setCancelable(false)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String alert = "";
                            if(
                                    ((TextView)newProfile.findViewById(R.id.name_edit)).getText().toString().equals("")
                            )
                                alert += "Please enter profile name.";
                            if(
                                    ((TextView)newProfile.findViewById(R.id.bucket_edit)).getText().toString().equals("")
                            )
                                alert += "Please enter bucket name.";
                            if(
                                    ((TextView)newProfile.findViewById(R.id.folderLocation)).getText().toString().equals("")
                            )
                                alert +="Please enter folder path.";
                            if(
                                    ((TextView)newProfile.findViewById(R.id.key_path)).getText().toString().equals("")
                            )
                            {
                                alert += "Please enter key_path";
                            }
                            if(
                                    ((TextView)newProfile.findViewById(R.id.name_edit)).getText().toString().equals("")
                                            ||      ((TextView)newProfile.findViewById(R.id.bucket_edit)).getText().toString().equals("")
                                            ||      ((TextView)newProfile.findViewById(R.id.folderLocation)).getText().toString().equals("")
                                            ||      ((TextView)newProfile.findViewById(R.id.key_path)).getText().toString().equals("")
                            ){
                                Toast.makeText(MainActivity.this, alert, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                MyDB db = new MyDB(getApplicationContext());
                                db.insertProfile(
                                        ((TextView) newProfile.findViewById(R.id.name_edit)).getText().toString(),
                                        ((TextView) newProfile.findViewById(R.id.bucket_edit)).getText().toString(),
                                        ((TextView) newProfile.findViewById(R.id.folderLocation)).getText().toString(),
                                        ((TextView) newProfile.findViewById(R.id.key_path)).getText().toString(),
                                        ((CheckBox) newProfile.findViewById(R.id.check_settings)).isChecked(),
                                        ((CheckBox) newProfile.findViewById(R.id.check_subfolder)).isChecked(),
                                        Integer.valueOf(((EditText) findViewById(R.id.interval_edit)).getText().toString())
                                );
                            }
                        }
                    })
                    .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment(), "Profiles");
        adapter.addFragment(new HistoryFragment(), "History");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public class Authenticate extends AsyncTask<String, Void, Boolean> {
        private Context context;
        private ProgressDialog dialog;
        String accesskey,secretkey;
        public Authenticate(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Validating credentials...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean check) {
            Log.e("Status","Done validating..");
            dialog.dismiss();
            if(check){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Credentials are correct")
                        .setTitle("Please enter your credentials");
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                        editor.putString("accesskey", accesskey);
                        editor.putString("secretkey", secretkey);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
            else {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Credentials are wrong")
                        .setPositiveButton("Done", null);
                builder.show();
            }

        }

        @Override
        protected Boolean doInBackground(String... params) {
            accesskey =  params[0];
            secretkey =  params[1];
            try {
//             Initialize the Amazon Cognito credentials provider
                BasicAWSCredentials awsCreds = new BasicAWSCredentials(accesskey,
                        secretkey);


                AmazonS3Client s3Client = new AmazonS3Client(awsCreds);

                Log.e("Region",Integer.toString(s3Client.listBuckets().size()));
                return true;
            } catch (AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.

                Log.e("Exception:", e.getMessage());
                return false;
            } catch (Exception e) {

                // Amazon S3 couldn't be contacted for a response, or the client
                // couldn't parse the response from Amazon S3.
                Log.e("Exception:", e.getMessage());

            }

            return null;
        }

    }
}