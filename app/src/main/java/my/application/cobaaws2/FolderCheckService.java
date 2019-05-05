package my.application.cobaaws2;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class FolderCheckService extends IntentService {
    private static final String TAG = "FolderCheckService";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FolderCheckService(String name) {
        super(name);
    }

    public FolderCheckService() {
        super(TAG);
        Log.e(TAG, "FolderCheckService: ");
    }

    @SuppressLint("NewApi")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.e(TAG, "Service running");
        TransferNetworkLossHandler.getInstance(getApplicationContext());
        //Classes.printIntentValues(intent);
        String folder = intent.getStringExtra("folder");
        String accesskey= intent.getStringExtra("accesskey");
        String secretkey = intent.getStringExtra("secretkey");
        String bucket = intent.getStringExtra("bucket");
        String keypath= intent.getStringExtra("keypath");

        File directory = new File(folder);

//        Log.e("Total directory:",Integer.toString(objectList.getCommonPrefixes().size()));
        List<FileData> files = Classes.listSortedFiles(directory);
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accesskey,secretkey);
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucket).withPrefix(keypath);
        ObjectListing objectList = s3Client.listObjects(listObjectsRequest);
        List<S3ObjectSummary> objectSummary = objectList.getObjectSummaries();

        for (FileData file: files) {
//                Log.e("Key:",s.getKey());
            String uploadDestination = keypath + file.getFileName();
            if(keypath.equals("root"))
                uploadDestination = file.getFileName();
            if(!Classes.containsChecksum(objectSummary,file.getChecksum_hash()) ){
                Log.e(TAG, "Object doesnt exist");
                final TransferUtility transferUtility =
                        TransferUtility.builder()
                                .context(getApplicationContext())
                                .s3Client(s3Client)
                                .build();
                final TransferObserver uploadObserver =
                        transferUtility.upload(
                                bucket, uploadDestination, new File(file.getFilePath()));

                // Attach a listener to the observer to get state update and progress notifications
                //final String finalKeyName = keyName;
                NotificationChannel notificationChannel = new NotificationChannel(Long.toString(System.currentTimeMillis()%1000),"monitor",NotificationManager.IMPORTANCE_DEFAULT);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, notificationChannel.getId())
                        .setSmallIcon(R.drawable.s3icon)
                        .setContentTitle(bucket)
                        .setContentText(file.getFileName())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setProgress(100,0,false)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);
                notificationManager.notify(Integer.valueOf(notificationChannel.getId()),notificationBuilder.build());
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
                        notificationBuilder.setProgress(100,percentDone,false);
                        notificationManager.notify(Integer.valueOf(notificationChannel.getId()),notificationBuilder.build());
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
            }
            else{

                Log.e(TAG, "ETAG: "+s3Client.getObjectMetadata(bucket,keypath).getETag());
                Log.e(TAG, file.getChecksum_hash());
                Log.e(TAG, "Checksum matches");
            }
        }
        //getETAG(accesskey,secretkey,bucket,keypath);


    }



}
