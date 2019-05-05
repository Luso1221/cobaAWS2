package my.application.cobaaws2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

//import net.rdrei.android.dirchooser.DirectoryChooserActivity;
//import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import static my.application.cobaaws2.ProfileFragment.SHARED_PREFERENCE_NAME;

public class HistoryActivity extends Fragment {
    public static boolean SYNCING = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View myFragmentView = inflater.inflate(R.layout.history_fragment, container, false);
        final SharedPreferences prefs = myFragmentView.getContext().getSharedPreferences("MyPref",0);

        ((Button)myFragmentView.findViewById(R.id.listFile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              listFiles(prefs.getString("folder",""));
            }
        });
        ((Button)myFragmentView.findViewById(R.id.toBucket)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (File file: getFiles(prefs.getString("folder","")))
                {
                    Log.e("File:",file.getName());
                    new CheckFileExist(getContext()).execute(file.getName());
                }
            }
        });
        ((Button)myFragmentView.findViewById(R.id.toLocal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return myFragmentView;
    }

    public void listFiles(String p) {
        StringBuilder text = new StringBuilder();

        String path = p;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);

        try {
            for (int i = 0; i < files.length; i++) {
                text.append("FileName:" + files[i].getName() + ", Last Modified: " + new Date(files[i].lastModified()) + "\n");

            }
        } catch (Exception e) {
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    public List<File> getFiles(String path){

        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        List<File> listOfFiles = new ArrayList<File>(Arrays.asList(files));
        return listOfFiles;
    }

    public class CheckFileExist extends AsyncTask<String, Void, String> {
        private Context context;
        private ProgressDialog dialog;
        public CheckFileExist(Context ctx) {
            context = ctx;
//            dialog = new ProgressDialog(context);

        }

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog.setMessage("Synchronizing...");
//            dialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Log.e("Status","Done fetching buckets..");
//            dialog.dismiss();
//        }

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            try {
                String accessKey =  prefs.getString("accesskey","");
                String secretKey =  prefs.getString("secretkey","");
                String bucket =  prefs.getString("bucket","");
                String path =  params[0];
//             Initialize the Amazon Cognito credentials provider
                BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,
                        secretKey);

                AmazonS3Client s3Client = new AmazonS3Client(awsCreds);
                //s3Client.listBuckets();

                NotificationManager notificationManager = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "Check file";
                    String description = "checks if file has already existed or not";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("1000", name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                        0);
                final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "1000");
                Log.e("TAG", "doInBackground: ");
                if(s3Client.doesObjectExist(bucket, params[0])) {
//                    notificationBuilder.setOngoing(true)
//                            .setSmallIcon(android.R.drawable.btn_star)
//                            .setContentTitle(params[0])
//                            .setContentText(params[0] + " already exists inside " + bucket)
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                            // Set the intent that will fire when the user taps the notification
//                            .setContentIntent(pendingIntent)
//                            .setAutoCancel(true)
//                            .setNumber(0)
//                            .setOngoing(false);
                }
                else {
//
//                    notificationBuilder.setOngoing(true)
//                            .setSmallIcon(android.R.drawable.btn_star)
//                            .setContentTitle(params[0])
//                            .setContentText("This file hasn't existed yet inside" + bucket +" ,tap here to begin upload")
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                            // Set the intent that will fire when the user taps the notification
//                            .setContentIntent(pendingIntent)
//                            .setAutoCancel(true)
//                            .setNumber(0)
//                            .setOngoing(false);
                }

                    Date now = new Date();
                    final int notificationID = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.getDefault()).format(now));

                    notificationManager.notify(notificationID, notificationBuilder.build());



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
}
