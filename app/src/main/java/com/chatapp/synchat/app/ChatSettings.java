package com.chatapp.synchat.app;

/**
 * created by  Adhash Team on 11/22/2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomMultiTextItemsDialog;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.model.MultiTextDialogPojo;

import java.util.ArrayList;
import java.util.List;

public class ChatSettings extends CoreActivity implements CompoundButton.OnCheckedChangeListener {

    private static final int RESULT_LOAD_IMAGE = 1;

    ImageView backimg_chat;
    RelativeLayout Language, chathistory, Textsize, wallpaper, chatbackup;
    AvnNextLTProRegTextView tsize, tsize_head, wallpaper_text, chatbackup_text, chathistory_text;
    Session session;
    final Context context = this;
    String imgDecodableString;
    Drawable d;
    RelativeLayout imageLayout;
    AvnNextLTProRegTextView applanguage, lang_sub, enter, enter_sub, chatsetting;
    AvnNextLTProDemiTextView text_security;
    private static final int RESULT_WALLPAPER = 8;

    private CheckBox chkEnterSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_settings);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflatedView;
        inflatedView = layoutInflater.inflate(R.layout.chat_message_screen, null, false);
        imageLayout = (RelativeLayout) inflatedView.findViewById(R.id.Relative_recycler);

        applanguage = (AvnNextLTProRegTextView) findViewById(R.id.r2_txt1);
        lang_sub = (AvnNextLTProRegTextView) findViewById(R.id.language);

        enter_sub = (AvnNextLTProRegTextView) findViewById(R.id.r2_txt5);

        tsize = (AvnNextLTProRegTextView) findViewById(R.id.tsize);

        text_security = (AvnNextLTProDemiTextView) findViewById(R.id.text_security);

        backimg_chat = (ImageView) findViewById(R.id.image_chat_arrow);
        Language = (RelativeLayout) findViewById(R.id.R2_chat);
        wallpaper = (RelativeLayout) findViewById(R.id.R5_chat);
        chatbackup = (RelativeLayout) findViewById(R.id.R6_chat);
//        chatbackup.setVisibility(View.GONE);
        chathistory = (RelativeLayout) findViewById(R.id.R7_chat);
        session = new Session(ChatSettings.this);
        Textsize = (RelativeLayout) findViewById(R.id.R4_chat);

        chkEnterSend = (CheckBox) findViewById(R.id.chkEnterSend);
        chkEnterSend.setChecked(session.isEnterKeyPressToSend());
        chkEnterSend.setOnCheckedChangeListener(ChatSettings.this);

        tsize.setText(session.gettextsize());

        updateEnterKeyText();

        getSupportActionBar().hide();
        backimg_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "work in progress", Toast.LENGTH_LONG).show();
            }
        });
        chathistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchChatHistory(ChatSettings.this);

            }
        });
        chatbackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchChatBackup(ChatSettings.this);

            }
        });

        Textsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                AvnNextLTProDemiTextView textcancel;
                final RadioButton r1, r2, r3;

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_textsize);
                textcancel = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.cancelsize);

                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.rsize1);
                r2 = (RadioButton) dialog.findViewById(R.id.rsize2);
                r3 = (RadioButton) dialog.findViewById(R.id.rsize3);

                Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
                r1.setTypeface(face);
                r2.setTypeface(face);
                r3.setTypeface(face);

                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.gettextsize().equalsIgnoreCase("Small")) {
                    r1.setChecked(true);
                } else if (session.gettextsize().equalsIgnoreCase("Medium")) {
                    r2.setChecked(true);
                } else if (session.gettextsize().equalsIgnoreCase("Large")) {
                    r3.setChecked(true);
                }


                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (r1.isChecked()) {
                            String value = "Small";
                            session.puttextsize(value);
                            tsize.setText(session.gettextsize());

                            dialog.dismiss();

                        } else if (r2.isChecked()) {
                            String value = "Medium";
                            session.puttextsize(value);
                            tsize.setText(session.gettextsize());
                            dialog.dismiss();
                        } else if (r3.isChecked()) {
                            String value = "Large";
                            session.puttextsize(value);

                            tsize.setText(session.gettextsize());
                            dialog.dismiss();

                        }
                    }
                });
                textcancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*

                RelativeLayout gallery, nowallpaper, defwallpaper, solidcolor;
//                AvnNextLTProRegTextView text1, text2, text5, text4;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_wallpaper);
                gallery = (RelativeLayout) dialog.findViewById(R.id.R1);
                solidcolor = (RelativeLayout) dialog.findViewById(R.id.R2);
                defwallpaper = (RelativeLayout) dialog.findViewById(R.id.R4);
                nowallpaper = (RelativeLayout) dialog.findViewById(R.id.R5);

                //gallery=(ImageView)dialog.findViewById(R.id.galleryicon);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                      */
/*  Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);*//*

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image*/
/*");
                            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(photoPickerIntent, RESULT_WALLPAPER);
                        } else {
                            Intent intent = new Intent();
                            intent.setType("image*/
/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, RESULT_WALLPAPER);
                        }
                    }
                });
                defwallpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String setbgdef = "def";

                        imageLayout.setBackgroundResource(R.drawable.chat_background);
                        session.putgalleryPrefs(setbgdef);
                        Toast.makeText(ChatSettings.this,"Wallpaper Set",Toast.LENGTH_SHORT).show();
                    }
                });

                nowallpaper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String setbg = "no";
                        imageLayout.setBackgroundColor(Color.parseColor("#F0FFFF"));
                        session.putgalleryPrefs(setbg);
                    }
                });

                solidcolor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(context, WallpaperColor.class);
                        startActivity(intent);
                    }
                });

*/
                List<MultiTextDialogPojo> labelsList = new ArrayList<>();
                MultiTextDialogPojo label = new MultiTextDialogPojo();
                label.setImageResource(R.drawable.gallery_ic);
                label.setLabelText("Gallery");
                labelsList.add(label);

                label = new MultiTextDialogPojo();
                label.setImageResource(R.drawable.solidcolor_ic);
                label.setLabelText("Solid color");
                labelsList.add(label);

                label = new MultiTextDialogPojo();
                label.setImageResource(R.drawable.default_ic);
                label.setLabelText("Default");
                labelsList.add(label);

                label = new MultiTextDialogPojo();
                label.setImageResource(R.drawable.nowallpaper_ic);
                label.setLabelText("No wallpaper");
                labelsList.add(label);

                CustomMultiTextItemsDialog dialog = new CustomMultiTextItemsDialog();
                dialog.setTitleText("Wallpaper");
                dialog.setLabelsList(labelsList);
                dialog.setDialogItemClickListener(new CustomMultiTextItemsDialog.DialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(int position) {
                        switch (position) {

                            case 0:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(photoPickerIntent, RESULT_WALLPAPER);

                                } else {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, RESULT_WALLPAPER);

                                }
                                break;

                            case 1:
                                Intent intent = new Intent(context, WallpaperColor.class);
                                startActivity(intent);
                                break;
                            case 2:
                                String setbgdef = "def";
                                imageLayout.setBackgroundResource(R.drawable.chat_background);
                                session.putgalleryPrefs(setbgdef);
                                Toast.makeText(ChatSettings.this,"Wallpaper Set",Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                String setbg = "no";
                                imageLayout.setBackgroundColor(Color.parseColor("#F0FFFF"));
                                session.putgalleryPrefs(setbg);
                                Toast.makeText(ChatSettings.this,"Wallpaper Set",Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "Profile Pic");

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                System.out.println("============================>img" + imgDecodableString);
                cursor.close();

                session.putgalleryPrefs(imgDecodableString);


                //ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                d = new BitmapDrawable(getResources(), BitmapFactory
                        .decodeFile(imgDecodableString));
                imageLayout.setBackground(d);
                Toast.makeText(ChatSettings.this,"Wallpaper Set",Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == RESULT_WALLPAPER && resultCode == RESULT_OK && null != data) {
            try {
                Uri selectedImageUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                if (imgDecodableString == null) {
                    imgDecodableString = getRealFilePath(data);
                }
                cursor.close();
                session.putgalleryPrefs(imgDecodableString);
                d = new BitmapDrawable(getResources(), BitmapFactory
                        .decodeFile(imgDecodableString));
                imageLayout.setBackground(d);
                Toast.makeText(ChatSettings.this,"Wallpaper Set",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getRealFilePath(Intent data) {
        Uri selectedImage = data.getData();
        String wholeID = DocumentsContract.getDocumentId(selectedImage);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
        if (compoundButton.getId() == R.id.chkEnterSend) {
            session.setEnterKeyPressToSend(state);
            updateEnterKeyText();
        }
    }

    private void updateEnterKeyText() {
        if(session.isEnterKeyPressToSend()) {
            enter_sub.setText(getString(R.string.enter_to_send));
        } else {
            enter_sub.setText(getString(R.string.enter_to_newline));
        }
    }
}

