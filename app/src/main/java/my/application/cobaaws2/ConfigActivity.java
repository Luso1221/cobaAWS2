package my.application.cobaaws2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import static my.application.cobaaws2.Classes.getPath;
//import static my.application.cobaaws2.ProfileFragment.SHARED_PREFERENCE_NAME;

public class ConfigActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);
        init();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ((EditText) findViewById(R.id.folderLocation)).setText(prefs.getString("folder", ""));
        ((EditText) findViewById(R.id.bucket_edit)).setText(prefs.getString("bucket", ""));
        ((EditText) findViewById(R.id.key_path)).setText(prefs.getString("keypath", ""));

        ((Button) findViewById(R.id.selectFolder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                printAllPermissions();

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 43);
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("file/*");
//                startActivityForResult(intent, 44);
            }
        });
        ((Button)findViewById(R.id.selectBucket)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                printAllPermissions();

                Intent intent = new Intent(getApplicationContext(), BucketpickerActivity.class);
                startActivityForResult(intent, 45);
            }
        });
        ((Button)findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = prefs.edit();
                   editor.putString("folder", ((EditText) findViewById(R.id.folderLocation)).getText().toString());
                    editor.putString("bucket", ((EditText) findViewById(R.id.bucket_edit)).getText().toString());
                    editor.putString("keypath", ((EditText) findViewById(R.id.key_path)).getText().toString());
                    editor.apply();
                    setResult(RESULT_OK);
                    finish();
            }
        });

        // specify an adapter (see also next example)
        //recyclerView.setAdapter(mAdapter);
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

                    File file = new File(path);
                    if (file.exists()) {
                        //Toast.makeText(context,"Path doesnt exist",Toast.LENGTH_SHORT).show();
                        //Do something

                        ((EditText) findViewById(R.id.folderLocation)).setText(path);
//
//                    Intent serviceIntent = new Intent(context, FileSystemObserverService.class);
//                    serviceIntent.putExtra("accessKey",accessKey);
//                    serviceIntent.putExtra("secretKey",secretKey);
//                    context.startService(serviceIntent);
                    } else
                        Toast.makeText(getApplicationContext(), "Path doesnt exist", Toast.LENGTH_SHORT).show();
                }
                break;

            case 45:
                if (resultCode == RESULT_OK) {
                    ((EditText)findViewById(R.id.bucket_edit)).setText(data.getStringExtra("bucketName"));
                    ((EditText)findViewById(R.id.key_path)).setText(data.getStringExtra("keyPath"));
                }
                break;
        }
    }
}
