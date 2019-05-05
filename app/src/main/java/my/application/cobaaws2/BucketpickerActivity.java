package my.application.cobaaws2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;

//import static my.application.cobaaws2.ProfileFragment.SHARED_PREFERENCE_NAME;

public class BucketpickerActivity extends MenuActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private GridLayoutManager layoutManager;
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucketpicker_activity);
        init();
        recyclerView = (RecyclerView) findViewById(R.id.bucketListView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        new ListBuckets().execute();


        // specify an adapter (see also next example)
        //recyclerView.setAdapter(mAdapter);
    }
    public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.ViewHolder> {

        private List<Bucket> mBuckets;
        private Context context;

        public BucketAdapter(List<Bucket> buckets) {
            mBuckets = buckets;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView nameTextView;
            public ImageView imageView;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                nameTextView = (TextView) itemView.findViewById(R.id.bucket_name);
                imageView = (ImageView) itemView.findViewById(R.id.bucket_thumb);
            }
        }

        @NonNull
        @Override
        public BucketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final Context context = parent.getContext();

            // Inflate the custom layout
            View layout = LayoutInflater.from(context).inflate(R.layout.bucketpicker_item, parent, false);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String bucketName = ((TextView)view.findViewById(R.id.bucket_name)).getText().toString();
                    new ListKeys().execute(bucketName);
//                    Intent data= new Intent();
//                    data.setData(Uri.parse(bucketName));
//                    setResult(RESULT_OK, data);
//                    finish();
                }
            });
            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(layout);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Bucket bucket = mBuckets.get(position);

            // Set item views based on your views and data model
            TextView textView = holder.nameTextView;
            textView.setText(bucket.getName());
        }

        @Override
        public int getItemCount() {
            return mBuckets.size();
        }


}
    public class ListBuckets extends AsyncTask<String, Void, List<Bucket>> {
        private final ProgressDialog dialog = new ProgressDialog(BucketpickerActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Retrieving bucket list...");
            dialog.show();
        }


        @Override
        protected void onPostExecute(List<Bucket> bucketList) {
            Log.e("Status","Done fetching buckets..");
            mAdapter = new BucketAdapter(bucketList);
            mAdapter.notifyDataSetChanged();
            ((RecyclerView)findViewById(R.id.bucketListView)).setAdapter(mAdapter);
            dialog.dismiss();
        }

        @Override
        protected List<Bucket> doInBackground(String... params) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            try {
                String accessKey =  prefs.getString("accesskey","");
                String secretKey =  prefs.getString("secretkey","");

//             Initialize the Amazon Cognito credentials provider
                Log.e("AccessKey:",accessKey);
                Log.e("SecretKey:",secretKey);
                BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,
                        secretKey);


                AmazonS3Client s3Client = new AmazonS3Client(awsCreds);
                for (Bucket b: s3Client.listBuckets()
                     ) {
                    Log.e("Bucket:",b.getName());
                }
                return s3Client.listBuckets();


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
    public class ListKeys extends AsyncTask<String, Void, ObjectListing> {
        private final ProgressDialog dialog = new ProgressDialog(BucketpickerActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Retrieving key list...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(final ObjectListing keys) {
            Log.e("Status","Done fetching keys..");
            final String bucketName = keys.getBucketName();
            dialog.dismiss();

            if(keys.getObjectSummaries().size() == 0 ){

                AlertDialog.Builder builder = new AlertDialog.Builder(BucketpickerActivity.this)
                        .setTitle("No folders found. Do you want to use root of bucket as file destination?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent data= new Intent();
                                data.putExtra("bucketName",bucketName);
                                data.putExtra("keyPath","root");
                                setResult(RESULT_OK, data);
                                finish();
                            }
                        })
                        .setNegativeButton("No",null);

//                    .setPositiveButton("Confirm", null)
//                    .setNegativeButton("Cancel", null);
                builder.show();
            }
            else {
                final ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<String>
                                (BucketpickerActivity.this, android.R.layout.select_dialog_item);
                List<S3ObjectSummary> objectSummary = keys.getObjectSummaries();
                for (S3ObjectSummary s: objectSummary) {
//                Log.e("Key:",s.getKey());
                    arrayAdapter.add(s.getKey());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(BucketpickerActivity.this)
                        .setTitle("Pick a folder")
                        .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Toast.makeText(BucketpickerActivity.this, arrayAdapter.getItem(i), Toast.LENGTH_SHORT).show();
                                Intent data = new Intent();
                                data.putExtra("bucketName", bucketName);
                                data.putExtra("keyPath", arrayAdapter.getItem(i));
                                setResult(RESULT_OK, data);
                                finish();
                            }
                        });

//                    .setPositiveButton("Confirm", null)
//                    .setNegativeButton("Cancel", null);
                builder.show();
            }
        }

        @Override
        protected ObjectListing doInBackground(String... params) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String bucketName = params[0];
            try {
                String accessKey =  prefs.getString("accesskey","");
                String secretKey =  prefs.getString("secretkey","");
//             Initialize the Amazon Cognito credentials provider
                Log.e("AccessKey:",accessKey);
                Log.e("SecretKey:",secretKey);
                Log.e("Bucketname",bucketName);
                BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey,
                        secretKey);


                AmazonS3Client s3Client = new AmazonS3Client(awsCreds);
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                        .withBucketName(bucketName);
                ObjectListing objectList = s3Client.listObjects(listObjectsRequest);

                Log.e("Total directory:",Integer.toString(objectList.getCommonPrefixes().size()));
                return objectList;

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
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // hide the menu item
        menu.findItem(R.id.settingButton).setVisible(false);
        return true;
    }

}
