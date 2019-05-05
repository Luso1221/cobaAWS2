package my.application.cobaaws2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class DebugActivity extends AppCompatActivity {
    private final String TAG =  "DebugActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        TransferNetworkLossHandler.getInstance(getApplicationContext());
        // Network service
        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
        final Button upload = (Button)findViewById(R.id.uploadButton);
        final Button pilihBucket = (Button)findViewById(R.id.cobabucket);
        final Button pilihFile = (Button)findViewById(R.id.cobapilihFile);
        final EditText pilihFileEdit = (EditText) findViewById(R.id.cobafile);
        Button getETag = (Button)findViewById(R.id.getETag);
        Button getChecksum = (Button)findViewById(R.id.getChecksum);
        Button listObjectByKey = (Button)findViewById(R.id.listObjectByKey);
        getChecksum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DebugActivity.this, Classes.calculateMD5(new File(pilihFileEdit.getText().toString())), Toast.LENGTH_SHORT).show();
            }
        });
        getETag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(),BucketpickerActivity.class);


                startActivityForResult(intent, 46);
            }
        });
        final Button checksumdir = (Button)findViewById(R.id.checksumdir);
        checksumdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 44);
            }
        });
        pilihFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                intent.addCategory(Intent.CATEGORY_OPENABLE);

                intent.setType("*/*");
                startActivityForResult(intent, 43);
            }
        });
        pilihBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),BucketpickerActivity.class);
                startActivityForResult(intent, 45);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessKey = prefs.getString("accesskey", "");
                String secretKey = prefs.getString("secretkey", "");

                String bucketName = ((EditText)findViewById(R.id.cobabucketedit)).getText().toString();
                String keypath  = ((EditText)findViewById(R.id.cobakeypathedit)).getText().toString();
                String filePath = ((EditText)findViewById(R.id.cobafile)).getText().toString();

                new UploadTask().execute(accessKey,secretKey,bucketName,keypath,filePath);
            }
        });
        listObjectByKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        switch(requestCode){
            case 43:
                uri = data.getData();

                ((EditText) findViewById(R.id.cobafile)).setText(Classes.getPath(this,uri));
                break;
            case 44:
                uri = data.getData();
                String path = Classes.getPath(this,uri);
                Log.e(TAG, path);
                try {
                    List<FileData> fileDataList = Classes.listSortedFiles(new File(path));
                    for (FileData fileData : fileDataList
                    ) {
                        Log.e(TAG, "onActivityResult: " + fileData.toString());

                    }
                }
                catch (Exception e){
                    Log.e(TAG, "onActivityResult: ",e );
                }
                break;
            case 45:
                if (resultCode == RESULT_OK) {
                    ((EditText)findViewById(R.id.cobabucketedit)).setText(data.getStringExtra("bucketName"));
                    ((EditText)findViewById(R.id.cobakeypathedit)).setText(data.getStringExtra("keyPath"));
                }
                break;
            case 46:
                if (resultCode == RESULT_OK) {

                    String bucketName = data.getStringExtra("bucketName");
                    String keyPath = data.getStringExtra("keyPath");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String accesskey = prefs.getString("accesskey","");
                    String secretkey = prefs.getString("secretkey","");
                    new GetETag().execute(accesskey,secretkey,bucketName,keyPath);
                }
                break;
        }


    }


    public class GetETag extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(DebugActivity.this);
        private static final String TAG = "GetETAG";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Retrieving etag...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG,"Etag retrieved");
            dialog.dismiss();
            Toast.makeText(DebugActivity.this, result, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            String accessKey =  params[0];
            String secretKey =  params[1];
            try {
//             Initialize the Amazon Cognito credentials provider



                BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,
                        secretKey);
                AmazonS3Client amazonS3Client = new AmazonS3Client(awsCreds);
                ObjectMetadata metadata = amazonS3Client.getObjectMetadata(params[2],params[3]);

                return metadata.getETag();
            } catch (AmazonServiceException e) {
                // The call was transmitted successfully, but Amazon S3 couldn't process
                // it, so it returned an error response.

                Log.e("Exception:", e.getMessage());
            } catch (Exception e) {

                // Amazon S3 couldn't be contacted for a response, or the client
                // couldn't parse the response from Amazon S3.
                Log.e("Exception:", e.getMessage());

            }

            return null;
        }

    }
    public class UploadTask extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(DebugActivity.this);
        private static final String TAG = "Upload";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Uploading...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Upload finished");
            dialog.dismiss();
            Toast.makeText(DebugActivity.this, result, Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... params) {
            String accessKey = params[0];
            String secretKey = params[1];
            String bucketName = params[2];
            String keyName = params[3];
            String filePath = params[4];
            try {

//             Initialize the Amazon Cognito credentials provider


                BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,
                        secretKey);
                AmazonS3Client amazonS3Client = new AmazonS3Client(awsCreds);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (keyName.equals("root"))
                    keyName = "";
                final File file = new File(filePath);
                //keyName += file.getName();
                Log.e("AccessKey:", accessKey);
                Log.e("SecretKey:", secretKey);
                Log.e("File:", filePath);
//                    ClientConfiguration clientConfiguration = new ClientConfiguration();
//                    clientConfiguration.setSocketTimeout(180000);
//                    amazonS3Client.setConfiguration(clientConfiguration);
                final TransferUtility transferUtility =
                        TransferUtility.builder()
                                .context(getApplicationContext())
                                .s3Client(amazonS3Client)
                                .build();
//                    Intent tsIntent = new Intent(getApplicationContext(), TransferService.class);
//                    tsIntent.putExtra(TransferService.INTENT_KEY_NOTIFICATION, new Notification());
//                    tsIntent.putExtra(TransferService.INTENT_KEY_NOTIFICATION_ID, 10);
//                    tsIntent.putExtra(TransferService.INTENT_KEY_REMOVE_NOTIFICATION, 10);
//                    getApplicationContext().startForegroundService(tsIntent);
                final TransferObserver uploadObserver =
                        transferUtility.upload(
                                bucketName, keyName + file.getName(), file);

                // Attach a listener to the observer to get state update and progress notifications
                final String finalKeyName = keyName;
                uploadObserver.setTransferListener(new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (TransferState.COMPLETED == state) {
                            // Handle a completed upload.
                            Log.e("Finish", "");
                        }
                        Log.e("State", state.toString());
                        if (TransferState.FAILED == state) {
                            Log.e("Failed", "");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                        int percentDone = (int) percentDonef;

                        Log.e("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                                + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        // Handle errors
                        Log.e("Error", "" + ex.getMessage());

//                            transferUtility.upload(bucketName, finalKeyName,file);
                    }

                });
                return "Finished";
                // If you prefer to poll for the data, instead of attaching a
                // listener, check for the state and progress in the observer.
//                    if (TransferState.COMPLETED == uploadObserver.getState()) {
//                        // Handle a completed upload.
//                        Log.e("Finish","");
//                    }if (TransferState.PENDING_NETWORK_DISCONNECT == uploadObserver.getState()) {
//                        // Handle a completed upload.
//
//                        transferUtility.resume(uploadObserver.getId());
//                    }

//                    Log.d("YourActivity", "Bytes Transferred: " + uploadObserver.getBytesTransferred());
//                    Log.d("YourActivity", "Bytes Total: " + uploadObserver.getBytesTotal());

            } catch (AmazonS3Exception e) {
                e.printStackTrace();
            } catch (AmazonServiceException e) {
                e.printStackTrace();
            } catch (AmazonClientException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return  null;
        }
    }

}
