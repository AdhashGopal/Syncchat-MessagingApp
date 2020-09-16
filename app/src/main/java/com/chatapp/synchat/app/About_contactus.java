package com.chatapp.synchat.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.DeviceInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiButton;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegEditText;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.uploadtoserver.FileUploadDownloadManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class About_contactus extends CoreActivity {
    ImageView backarrow_About_contactus, imagescreen1, imagescreen2, imagescreen3;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    Bitmap bitmap;
    private AvnNextLTProDemiTextView header, Next;
    private AvnNextLTProDemiTextView subheader;
    private AvnNextLTProRegEditText edittext1;
    private AvnNextLTProDemiButton About_contactus_button;
    int value;
    Bitmap photo;
    String selectedImagePath, selectedImagePathG;
    private Uri myFirstImageURI, mySecondURI, myThirdURI;
    ArrayList<Uri> uris = new ArrayList<Uri>();
    ArrayList<String> fileList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_contactus);
        imagescreen1 = (ImageView) findViewById(R.id.imagescreen1);
        imagescreen2 = (ImageView) findViewById(R.id.imagescreen2);
        imagescreen3 = (ImageView) findViewById(R.id.imagescreen3);
        edittext1 = (AvnNextLTProRegEditText) findViewById(R.id.About_contactus_editText);
        header = (AvnNextLTProDemiTextView) findViewById(R.id.About_contactus_actionbar_1);
        Next = (AvnNextLTProDemiTextView) findViewById(R.id.next_About_contactus);
        subheader = (AvnNextLTProDemiTextView) findViewById(R.id.About_contactus_screenshot_text);
        About_contactus_button = (AvnNextLTProDemiButton) findViewById(R.id.About_contactus_button);

        getDeviceInfo();

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    Intent intent;
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, edittext1.getText().toString());
                intent.putExtra(Intent.EXTRA_STREAM, uris);
                intent.setType("**///*");
                // startActivity(intent);*/

                if (!edittext1.getText().toString().equalsIgnoreCase("")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    emailIntent.setType("plain/text");

                 //   String emailId = SessionManager.getInstance(About_contactus.this).getContactUsEMailId();

                    String emailId = "contact@synctag.com";

                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
                    //ei.putExtra(Intent.EXTRA_EMAIL, new String[] {"me@somewhere.nodomain"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Question about " + getString(R.string.app_name));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, edittext1.getText().toString());
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    PackageManager pm = getPackageManager();
                    List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                    ResolveInfo best = null;
                    for (final ResolveInfo info : matches)
                        if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")
                                || info.activityInfo.name.toLowerCase().contains("com.android.email"))
                            best = info;
                    if (best != null)
                        emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                    startActivityForResult(Intent.createChooser(emailIntent, "Sending multiple attachment"), 12345);
                    edittext1.setText("");
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_plus12);
                    imagescreen1.setImageBitmap(icon);
                    imagescreen2.setImageBitmap(icon);
                    imagescreen3.setImageBitmap(icon);
                    uris.clear();
                } else {
                    Toast.makeText(About_contactus.this, "Please Describe Your Problem", Toast.LENGTH_SHORT).show();
                }


            }


        });
        imagescreen1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
                value = 0;
            }
        });
        imagescreen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
                value = 1;
            }
        });
        imagescreen3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
                value = 2;
            }
        });
        About_contactus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri = Uri.parse(Constants.FAQ); // missing 'http://' will cause crashed
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
            }
        });
        backarrow_About_contactus = (ImageView) findViewById(R.id.backarrow_About_contactus);
        backarrow_About_contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityLauncher.launchAbouthelp(About_contactus.this);
                finish();
            }
        });

    }

    private void startDialog() {
        CustomAlertDialog myAlertDialog = new CustomAlertDialog();
        myAlertDialog.setTitle("Upload Pictures Option:");
        myAlertDialog.setMessage("How do you want to set your picture?");
        myAlertDialog.setPositiveButtonText("Gallery");
        myAlertDialog.setNegativeButtonText("Camera");
        myAlertDialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                Intent pictureActionIntent = null;

                pictureActionIntent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(
                        pictureActionIntent,
                        GALLERY_PICTURE);
            }

            @Override
            public void onNegativeButtonClick() {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        myAlertDialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            try {
                photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getApplicationContext(), photo);
                if (value == 0) {
                    myFirstImageURI = tempUri;
                    uris.add(myFirstImageURI);
                    imagescreen1.setImageBitmap(photo);
                } else if (value == 1) {
                    mySecondURI = tempUri;
                    uris.add(mySecondURI);
                    //  Uri imageUri = data.getData();
                    imagescreen2.setImageBitmap(photo);
                } else if (value == 2) {
                    myThirdURI = tempUri;
                    uris.add(myThirdURI);
                    //  Uri imageUri = data.getData();
                    imagescreen3.setImageBitmap(photo);
                }
                //storeImageTosdCard(bitmap);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                fileList.add(selectedImagePath);
                c.close();

               /* if (selectedImagePath != null) {
                    txt_image_path.setText(selectedImagePath);
                }*/

                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                if (value == 0) {
                    myFirstImageURI = data.getData();
                    uris.add(myFirstImageURI);
                    imagescreen1.setImageBitmap(bitmap);
                } else if (value == 1) {
                    mySecondURI = data.getData();
                    uris.add(mySecondURI);
                    //  Uri imageUri = data.getData();
                    imagescreen2.setImageBitmap(bitmap);
                } else if (value == 2) {
                    myThirdURI = data.getData();
                    uris.add(myThirdURI);
                    //  Uri imageUri = data.getData();
                    imagescreen3.setImageBitmap(bitmap);
                }


            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        try {
            bytes.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.parse(path);
    }

    private String getDeviceInfo() {
        SessionManager sessionManager = SessionManager.getInstance(About_contactus.this);

        Locale current = getResources().getConfiguration().locale;

        String debugInfo = "Debug info: " + sessionManager.getPhoneNumberOfCurrentUser();
        String description = "Description: " + Getcontactname.getAppVersion(this);
        String appVersion = "Version: " + Getcontactname.getAppVersion(this);
        String lc = "LC: " + current.getCountry();
        String lg = "LG: " + current.getLanguage();
        String context = "Context: settings/about";
        String carrier = "Carrier: " + getOperatorName();
        String manufacturer = "Manufacturer: " + Build.MANUFACTURER;
        String model = "Model: " + Build.MODEL;
        String osVersion = "OS: " + Build.VERSION.RELEASE;
        String radioMcc = "Radio MCC-MNC: " + DeviceInfo.getDeviceInfo(this, DeviceInfo.Device.DEVICE_NETWORK_MCC_MNC);
        String simMcc = "SIM MCC-MNC: " + DeviceInfo.getDeviceInfo(this, DeviceInfo.Device.DEVICE_SIM_MCC_MNC);
        String freeSpaceBuildIn = "Free Space Built-In: " + FileUploadDownloadManager.getAvailableInternalMemorySize();
        String freeSpaceSdCard = "Free Space Removable: " + FileUploadDownloadManager.getAvailableExternalMemorySize();
        String ccode = "CCode: " + sessionManager.getCountryCodeOfCurrentUser() + " " + sessionManager.getUserMobileNoWithoutCountryCode();
        String target = "Target: release";
        String distribut = "Distribution: play";
        String product = "Product: " + Build.PRODUCT;
        String device = "Device: " + Build.DEVICE;
        String build = "Build: " + Build.DISPLAY;
        String board = "Board: " + Build.BOARD;
        String kernel = "Kernel: " + System.getProperty("os.version");
        String connection = "Connection: " + DeviceInfo.getConnectedNetwork(this);
        String phoneType = "Phone Type: G.S.M.";
        String networkType = "Network Type: " + DeviceInfo.getNetworkType(this);
        String missPermissions = "Missing Permissions: " +"";
        String architecture = "Architecture: " +System.getProperty("os.arch");
        String dataRoam = "Data roaming: " +"";
        String telRoam = "Tel roaming: " +"";


        return "";
    }

    private String getOperatorName() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    private boolean isDataRoamActivated() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return manager.isNetworkRoaming();
    }

    public static boolean isCallRoamActivated(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.isNetworkRoaming();
    }
}
