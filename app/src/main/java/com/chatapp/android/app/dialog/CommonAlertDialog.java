package com.chatapp.android.app.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.chatapp.android.R;

/**
 * created by  Adhash Team on 4/16/2018.
 */

public class CommonAlertDialog {
    private static final String TAG = CommonAlertDialog.class.getSimpleName();

    public interface DialogListener{
        void onPositiveBtnClick(Object result);
        void onNegativeBtnClick();
    }

    public static void showDialog(Context context, final DialogListener dialogListener, String title, String msg,
                                  String okBtnText, String cancelBtnText, final Object input){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogColor);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(okBtnText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(dialogListener!=null){
                        dialogListener.onPositiveBtnClick(input);
                    }
                }
            });
            builder.setNegativeButton(cancelBtnText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                 if(dialogListener!=null){
                     dialogListener.onNegativeBtnClick();
                 }
                }
            });
            builder.show();
        }
        catch (Exception e){
            Log.e(TAG, "showDialog: ",e );
        }
    }

}
