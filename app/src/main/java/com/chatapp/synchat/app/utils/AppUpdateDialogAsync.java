package com.chatapp.synchat.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.chatapp.synchat.R;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 * created by  Adhash Team on 3/12/2018.
 */

public class AppUpdateDialogAsync extends AsyncTask<String, String, JSONObject> {

    private String latestVersion="0";
    private String currentVersion="0";
    private Context context;
    private static final String TAG = AppUpdateDialogAsync.class.getSimpleName();

    public AppUpdateDialogAsync(Context context){
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            Log.d(TAG, "AppUpdateDialogAsync: "+currentVersion);
        } catch (Exception e) {
            Log.e(TAG, "AppUpdateDialogAsync: ",e );
        }

        Log.d(TAG, "AppUpdateDialogAsync: "+currentVersion);
        this.context = context;
    }

    /**
     *
     * @param params
     * @return
     */
    @Override
    protected JSONObject doInBackground(String... params) {

        try {
            Element element=Jsoup.connect("https://play.google.com/store/apps/details?id="+context.getPackageName()+"&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first();
            if(element!=null)
                latestVersion = element.ownText();

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ",e );
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        if(latestVersion!=null && !latestVersion.equals("0")){
            Log.d(TAG, "onPostExecute: latestversion: "+latestVersion);
            if(!currentVersion.equalsIgnoreCase(latestVersion)){

                    if(!((Activity)context).isFinishing()){
                        showUpdateDialog();
                    }

            }
        }
        super.onPostExecute(jsonObject);
    }

    public void showUpdateDialog(){
        try {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyleNew);
            builder.setTitle("Update");

            builder.setMessage("There is newer version of this application available, click OK to upgrade now");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    } catch (Exception e) {
                        Log.e(TAG, "onClick: ", e);
                    }
                }
            });//second parameter used for onclicklistener
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
        catch (Exception e){
            Log.e(TAG, "showUpdateDialog: ",e );
        }
    }
}
