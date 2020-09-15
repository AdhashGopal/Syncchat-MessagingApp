package com.chatapp.android.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.chatapp.android.BuildConfig;
import com.chatapp.android.R;
import com.chatapp.android.core.connectivity.NetworkChangeReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DownloadManagerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    String updateUrl = "", updateMsg = "";
    private Button downloadAPK;
    private NetworkChangeReceiver networkChangeReceiver = null;
    private SessionManager sessionManager;

    /**
     * Delete directory from path
     *
     * @param dir path for directory
     * @return response value
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));

                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static void InstallAPK(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            try {
                String command;
                filename = insertEscape(filename);
                command = "adb install -r " + filename;
                Runtime.getRuntime().exec("reboot");
                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String insertEscape(String text) {
        return text.replace(" ", "\\ ");
    }

    /**
     * Check the internet connection
     *
     * @param context current activity
     * @return response value
     */
    public static Boolean isInternetConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    private void showAlert(final String message) {

        DownloadManagerActivity.this.runOnUiThread(new Runnable() {
            public void run() {

                try {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DownloadManagerActivity.this, R.style.MyDialogTheme);
                    alertDialog.setCancelable(false);
                    // Setting Dialog Title
                    alertDialog.setTitle("aChat");
                    // Setting Dialog Message
                    alertDialog.setMessage(message);
                    // Setting Icon to Dialog
                    //  alertDialog.setIcon(R.drawable.tick);
                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
//                System.exit(0);
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();

                }


            }
        });

    }

    /**
     * Downloading url based apk file
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_man);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        downloadAPK = findViewById(R.id.btn_download);
        sessionManager = SessionManager.getInstance(DownloadManagerActivity.this);
        networkChangeReceiver = new NetworkChangeReceiver();
        final Bundle bundle = getIntent().getExtras();
        downloadAPK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bundle != null) {

                    if (bundle.containsKey("update_url")) {
                        updateUrl = bundle.getString("update_url", "");
                        updateMsg = bundle.getString("update_msg", "");
                        if (!checkPermission()) {
                            requestPermission();
                        } else {
                            if (!TextUtils.isEmpty(updateUrl)) {
//                        new DownloadMethodTwo().execute("https://achat.adani.com/content/achat.apk");
                                new DownloadMethodTwo().execute(updateUrl);
                            }

                        }
                    }
                }
                /*if (!checkPermission()) {
                    requestPermission();
                } else {

                    new DownloadMethodTwo().execute("https://drive.google.com/uc?id=1PnJrKekflWykVunvRCfIYIMyg7LNb_yo&export=download");
                }*/
            }
        });


    }

    /**
     * Check the runtime permission
     *
     * @return
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime permission
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    /***
     * Response of runtime permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE:
                    if (grantResults.length > 0) {

                        boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                        if (locationAccepted && cameraAccepted) {
                            if (!TextUtils.isEmpty(updateUrl)) {
                                new DownloadMethodTwo().execute(updateUrl);
                            }

                        } else {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                    Toast.makeText(DownloadManagerActivity.this, "Need access for both", Toast.LENGTH_LONG).show();
                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                            PERMISSION_REQUEST_CODE);
                                }
                            }

                        }
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OpenNewVersion(String location) {
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(getUriFromFile(location), "application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(new File(location)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getApplicationContext().startActivity(intent);*/

//        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {

            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Uri uri = FileProvider.getUriForFile(DownloadManagerActivity.this, BuildConfig.APPLICATION_ID, new File(location));
//            Uri apkUri = FileProvider.getUriForFile(DownloadManagerActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(location));

            // intent.setDataAndType(Uri.fromFile(file), mimeType);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//            intent.setData(apkUri);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            System.out.println("Testing123--> OpenNewVersion");
        }

    }

    private Uri getUriFromFile(String location) {

        if (Build.VERSION.SDK_INT < 24) {
            return Uri.fromFile(new File(location + "achat.apk"));
        } else {
            return FileProvider.getUriForFile(DownloadManagerActivity.this,
                    getApplicationContext().getPackageName() + ".provider",
                    new File(location + "achat.apk"));
        }
    }


    /**
     * Activity kill mode to disconnect network receiver
     */
    @Override
    protected void onDestroy() {

//        deleteDir(new File(Environment.getExternalStorageDirectory() + "/aChat"));
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }

    }

    /**
     * Activity start mode to inlizied  network receiver
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (networkChangeReceiver != null) {
            registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    /**
     * Download apk from AsyncTask
     */
    class DownloadMethodTwo extends AsyncTask<String, String, String> {
        ProgressDialog pd;
        String pathFolder = "";
        String pathFile = "";
        HttpsURLConnection connection;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                deleteDir(new File(Environment.getExternalStorageDirectory() + "/aChat"));
                /*File file = new File(Environment.getExternalStorageDirectory() + "/aChatUpdater/aChatUpdater.apk");
                if (file.exists()) file.delete();*/
            } catch (Exception e) {
                e.printStackTrace();
            }

            pd = new ProgressDialog(DownloadManagerActivity.this);
            pd.setTitle("Update in Progress..!");
            pd.setMessage(updateMsg);
            pd.setMax(100);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;

            try {
                pathFolder = Environment.getExternalStorageDirectory() + "/aChat";
                pathFile = pathFolder + "/aChat.apk";
                File futureStudioIconFile = new File(pathFolder);
                if (!futureStudioIconFile.exists()) {
                    futureStudioIconFile.mkdirs();
                }


                URL url = new URL(f_url[0]);
                connection = (HttpsURLConnection) url.openConnection();
//                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lengthOfFile = connection.getContentLength();
                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                FileOutputStream output = new FileOutputStream(pathFile);

                byte data[] = new byte[1024]; //anybody know what 1024 means ?
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();


            } catch (Exception e) {
                if (connection != null) {
                    connection.disconnect();
                }
                Log.e("Error: ", e.getMessage());


            }

            return pathFile;
        }

        protected void onProgressUpdate(String... progress) {

            try {
                // setting progress percentage
                if (isInternetConnected(DownloadManagerActivity.this)) {
                    pd.setProgress(Integer.parseInt(progress[0]));
                }/* else {
                    Toast.makeText(DownloadManagerActivity.this, "Internet disconnected please try again", Toast.LENGTH_LONG).show();
                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pd != null) {
                pd.dismiss();
            }
            if (connection != null) {
                connection.disconnect();
            }
            if (isInternetConnected(DownloadManagerActivity.this)) {
                sessionManager.setChatUpdaterDownload("1");
                OpenNewVersion(file_url);
            } /*else {
                Toast.makeText(DownloadManagerActivity.this, "Internet Disconnected", Toast.LENGTH_SHORT).show();
            }*/


        }
    }

    private String getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong.toString();
    }
}
