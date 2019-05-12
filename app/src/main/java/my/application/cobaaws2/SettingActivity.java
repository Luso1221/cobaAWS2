package my.application.cobaaws2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;

//import static my.application.cobaaws2.ProfileFragment.SHARED_PREFERENCE_NAME;

public class SettingActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
//        String accesskey = SP.getString("accesskey", "BLANK");
//        String secretkey = SP.getString("secretkey","BLANK");
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // hide the menu item
        menu.findItem(R.id.settingButton).setVisible(false);
        return true;
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                    Preference pref = findPreference(key);
                    if (pref instanceof EditTextPreference) {
                        EditTextPreference etp = (EditTextPreference) pref;
                        etp.setSummary(sharedPreferences.getString(key,""));
                    }
                    if (pref instanceof ListPreference) {
                        ListPreference listPreference = (ListPreference) pref;
                        listPreference.setSummary(listPreference.getEntry());
                    }
                }
            });
        }
    }

}