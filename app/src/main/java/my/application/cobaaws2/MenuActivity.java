package my.application.cobaaws2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Map;

//import static my.application.cobaaws2.ProfileFragment.SHARED_PREFERENCE_NAME;

public class MenuActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void init(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
//            case R.id.mybutton:
//
//                printAllSharedPreferences();
//                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.settingButton:
//                Toast.makeText(this, Environment.getExternalStorageDirectory().toString(),Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this,SettingActivity.class);
                startActivity(i);
                break;
            case R.id.uploader:
//                Toast.makeText(this, Environment.getExternalStorageDirectory().toString(),Toast.LENGTH_SHORT).show();
                Intent ii = new Intent(this, DebugActivity.class);
                startActivity(ii);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    MenuItem mDynamicMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menus, menu);
        // Get dynamic menu item
        mDynamicMenuItem = menu.findItem(R.id.settingButton);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
}
