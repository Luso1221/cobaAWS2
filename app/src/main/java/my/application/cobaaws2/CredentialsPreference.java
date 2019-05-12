package my.application.cobaaws2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

//import static my.application.cobaaws2.ProfileFragment.SHARED_PREFERENCE_NAME;

public class CredentialsPreference extends DialogPreference implements DialogInterface.OnClickListener{

    private View myView;
    public CredentialsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        setDialogLayoutResource(R.layout.credentials_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        myView = view;

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getContext());
        EditText editText1 = (EditText)view.findViewById(R.id.txt_access_key);
        editText1.setText(SP.getString("accesskey",""));
        EditText editText2 = (EditText)view.findViewById(R.id.txt_secret_key);
        editText2.setText(SP.getString("secretkey",""));
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE) {
            // do your stuff to handle positive button
            new Authenticate().execute(((EditText)myView.findViewById(R.id.txt_access_key)).getText().toString(),
                    ((EditText)myView.findViewById(R.id.txt_secret_key)).getText().toString());
        }else if(which == DialogInterface.BUTTON_NEGATIVE){
            // do your stuff to handle negative button
        }
    }

    public class Authenticate extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(getContext());
        String accesskey,secretkey;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Validating credentials...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean check) {
            Log.e("Status","Done validating..");
            dialog.dismiss();
            if(check){


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle("Credentials are correct");
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                        editor.putString("accesskey", accesskey);
                        editor.putString("secretkey", secretkey);
                        editor.apply();
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
            else {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
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
