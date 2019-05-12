package my.application.cobaaws2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static my.application.cobaaws2.Classes.getPath;
//import static my.application.cobaaws2.ProfileFragment.SHARED_PREFERENCE_NAME;

public class ConfigActivity extends MenuActivity {
    private static final String TAG = "ConfigActivity";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);
        init();
        intent = getIntent();

        String mode = intent.getStringExtra("mode");

        ((Button) findViewById(R.id.selectFolder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 43);
            }
        });
        ((Button)findViewById(R.id.selectBucket)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(), BucketpickerActivity.class);
                startActivityForResult(intent, 45);
            }
        });
        setup(mode);


        // specify an adapter (see also next example)
        //recyclerView.setAdapter(mAdapter);
    }
    private void setup(String mode){

        ((CheckBox) findViewById(R.id.check_settings)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    ((CheckBox) findViewById(R.id.check_subfolder)).setEnabled(true);
                    ((EditText) findViewById(R.id.interval_edit)).setEnabled(true);
                }
                else{
                    ((CheckBox) findViewById(R.id.check_subfolder)).setEnabled(false);
                    ((EditText) findViewById(R.id.interval_edit)).setEnabled(false);

                }
            }
        });
        switch(mode){
            case "INSERT":
                if(
                        !((CheckBox) findViewById(R.id.check_settings)).isChecked()
                ) {
                    ((CheckBox) findViewById(R.id.check_subfolder)).setEnabled(false);
                    ((EditText) findViewById(R.id.interval_edit)).setEnabled(false);
                }
                ((Button)findViewById(R.id.saveButton)).setText("Create");

                ((Button)findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String alert = "";
                        if(
                                ((TextView)findViewById(R.id.name_edit)).getText().toString().equals("")
                        )
                            alert += "Please enter profile name.";
                        if(
                                    ((TextView)findViewById(R.id.bucket_edit)).getText().toString().equals("")
                            )
                                alert += "Please enter bucket name.";
                        if(
                                ((TextView)findViewById(R.id.folderLocation)).getText().toString().equals("")
                        )
                            alert +="Please enter folder path.";
                            if(
                                    ((TextView)findViewById(R.id.key_path)).getText().toString().equals("")
                        )
                            {
                                alert += "Please enter key_path";
                            }
                        if(
                                ((TextView)findViewById(R.id.name_edit)).getText().toString().equals("")
                                        ||      ((TextView)findViewById(R.id.bucket_edit)).getText().toString().equals("")
                                        ||      ((TextView)findViewById(R.id.folderLocation)).getText().toString().equals("")
                                        ||      ((TextView)findViewById(R.id.key_path)).getText().toString().equals("")
                        ){
                            Toast.makeText(ConfigActivity.this, alert, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            MyDB db = new MyDB(getApplicationContext());
                            db.insertProfile(
                                    ((TextView) findViewById(R.id.name_edit)).getText().toString(),
                                    ((TextView) findViewById(R.id.bucket_edit)).getText().toString(),
                                    ((TextView) findViewById(R.id.folderLocation)).getText().toString(),
                                    ((TextView) findViewById(R.id.key_path)).getText().toString(),
                                    ((CheckBox) findViewById(R.id.check_settings)).isChecked(),
                                    ((CheckBox) findViewById(R.id.check_subfolder)).isChecked(),
                                    Integer.valueOf(((EditText) findViewById(R.id.interval_edit)).getText().toString())
                            );
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
                break;
            case "UPDATE":
                if(
                        !((CheckBox) findViewById(R.id.check_settings)).isChecked()
                ) {
                    ((CheckBox) findViewById(R.id.check_subfolder)).setEnabled(false);
                    ((EditText) findViewById(R.id.interval_edit)).setEnabled(false);
                }

                ((Button)findViewById(R.id.saveButton)).setText("Update");
                ((TextView)findViewById(R.id.name_edit)).setText(intent.getStringExtra("profile_name"));
                ((TextView)findViewById(R.id.bucket_edit)).setText(intent.getStringExtra("bucket_name"));
                ((TextView)findViewById(R.id.key_path)).setText(intent.getStringExtra("keypath"));
                ((TextView)findViewById(R.id.folderLocation)).setText(intent.getStringExtra("localfolder_path"));
                ((Button)findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String alert = "";
                        Log.e(TAG, "onClick: "+((TextView)findViewById(R.id.name_edit)).getText().toString() );
                        if(
                                ((TextView)findViewById(R.id.name_edit)).getText().toString().equals("")
                        )
                            alert += "Please enter profile name.";
                        if(
                                ((TextView)findViewById(R.id.bucket_edit)).getText().toString().equals("")
                        )
                            alert += "Please enter bucket name.";
                        if(
                                ((TextView)findViewById(R.id.folderLocation)).getText().toString().equals("")
                        )
                            alert +="Please enter folder path.";
                        if(
                                ((TextView)findViewById(R.id.key_path)).getText().toString().equals("")
                        )
                        {
                            alert += "Please enter key_path";
                        }
                        if(
                                ((TextView)findViewById(R.id.name_edit)).getText().toString().equals("")
                                        ||      ((TextView)findViewById(R.id.bucket_edit)).getText().toString().equals("")
                                        ||      ((TextView)findViewById(R.id.folderLocation)).getText().toString().equals("")
                                        ||      ((TextView)findViewById(R.id.key_path)).getText().toString().equals("")
                        ){
                            Toast.makeText(ConfigActivity.this, alert, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            MyDB db = new MyDB(getApplicationContext());
                            db.updateProfile(intent.getIntExtra("profile_id", 0),

                                    ((TextView) findViewById(R.id.name_edit)).getText().toString(),
                                    ((TextView) findViewById(R.id.bucket_edit)).getText().toString(),
                                    ((TextView) findViewById(R.id.folderLocation)).getText().toString(),
                                    ((TextView) findViewById(R.id.key_path)).getText().toString(),
                                    ((CheckBox) findViewById(R.id.check_settings)).isChecked(),
                                    ((CheckBox) findViewById(R.id.check_subfolder)).isChecked(),
                                    Integer.parseInt(((EditText) findViewById(R.id.interval_edit)).getText().toString())

                            );
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
                break;
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 43:
                if (resultCode == RESULT_OK) {
                    Uri treeUri = data.getData();
                    String path = getPath(getApplicationContext(), treeUri);
                    //DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                    Log.e(TAG, path );
                    File file = new File(path);
                    if (file.exists()) {
                        ((TextView) findViewById(R.id.folderLocation)).setText(path);
                    } else
                        Toast.makeText(getApplicationContext(), "Path doesnt exist", Toast.LENGTH_SHORT).show();
                }
                break;

            case 45:
                if (resultCode == RESULT_OK) {
                    ((TextView)findViewById(R.id.bucket_edit)).setText(data.getStringExtra("bucketName"));
                    ((TextView)findViewById(R.id.key_path)).setText(data.getStringExtra("keyPath"));
                }
                break;
        }
    }
}
