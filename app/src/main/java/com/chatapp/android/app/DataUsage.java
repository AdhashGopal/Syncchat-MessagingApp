package com.chatapp.android.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.ActivityLauncher;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;

/**
 * created by  Adhash Team on 11/18/2016.
 */
public class DataUsage extends CoreActivity {
    ImageView backimg;
    LinearLayout l1, l2, l3;
    AvnNextLTProRegTextView text_media, textm_data, text_wifi, text_roam, content, call_setting, text_call_setting, text_data;
    AvnNextLTProRegTextView mobiledata, wifi, roaming;
    private AvnNextLTProDemiTextView text_actionbar_1,textnetwork;
    final Context context = this;
    Session session;
    String sIncludedOn = "";
    String sIncludedOn1 = "";
    String sIncludedOn2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_usage);
        session = new Session(DataUsage.this);
        textnetwork = (AvnNextLTProDemiTextView) findViewById(R.id.textnetwork);





        call_setting = (AvnNextLTProRegTextView) findViewById(R.id.call_setting);
        text_call_setting = (AvnNextLTProRegTextView) findViewById(R.id.text_call_setting);

        text_data = (AvnNextLTProRegTextView) findViewById(R.id.text_data);
        text_actionbar_1 = (AvnNextLTProDemiTextView) findViewById(R.id.text_actionbar_1);

        l1 = (LinearLayout) findViewById(R.id.Linear1);
        l2 = (LinearLayout) findViewById(R.id.Linear2);
        l3 = (LinearLayout) findViewById(R.id.Linear3);
        mobiledata = (AvnNextLTProRegTextView) findViewById(R.id.mobiledata);

        wifi = (AvnNextLTProRegTextView) findViewById(R.id.wifi);
        roaming = (AvnNextLTProRegTextView) findViewById(R.id.roaming);
        if (!session.getmobiledataPrefsName().equals("")) {
            mobiledata.setText(session.getmobiledataPrefsName());
        } else {
            mobiledata.setText("No media");
        }
        if (!session.getwifiPrefsName().equals("")) {
            wifi.setText(session.getwifiPrefsName());
        } else {
            wifi.setText("No media");
        }
        if (!session.getromingPrefsName().equals("")) {
            roaming.setText(session.getromingPrefsName());
        } else {
            roaming.setText("No media");
        }
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        backimg = (ImageView) findViewById(R.id.backarrow_datausage);
        getSupportActionBar().hide();
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchHomeScreen(DataUsage.this);

            }
        });
        textnetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DataUsage.this, Networkusage.class);
                startActivity(i);

            }
        });
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);

                final CheckBox c1, c2, c3, c4;
                AvnNextLTProDemiTextView cancel, ok;
                AvnNextLTProRegTextView text;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_mobile_data);
                text = (AvnNextLTProRegTextView) dialog.findViewById(R.id.texthead);
                c1 = (CheckBox) dialog.findViewById(R.id.check1);
                c2 = (CheckBox) dialog.findViewById(R.id.check2);
                c3 = (CheckBox) dialog.findViewById(R.id.check3);
                c4 = (CheckBox) dialog.findViewById(R.id.check4);
                cancel = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.cancel);
                ok = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.ok);
                Typeface typeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
                c1.setTypeface(typeface);
                c2.setTypeface(typeface);
                c3.setTypeface(typeface);
                c4.setTypeface(typeface);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                text.setText("When using mobile data");
                String[] values = session.getmobiledataPrefsName().split(",");
                System.out.println("==================================values" + values);
                for (int i = 0; i < values.length; i++) {
                    if (values[i].equalsIgnoreCase("photo")) {
                        c1.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Audio")) {
                        c2.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Videos")) {
                        c3.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Documents")) {
                        c4.setChecked(true);
                    }
                }


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (c1.isChecked()) {
                            sIncludedOn = sIncludedOn + "Photo" + ",";
                        }


                        if (c2.isChecked()) {
                            sIncludedOn = sIncludedOn + "Audio" + ",";
                        }

                        if (c3.isChecked()) {
                            sIncludedOn = sIncludedOn + "Videos" + ",";
                        }

                        if (c4.isChecked()) {
                            sIncludedOn = sIncludedOn + "Documents" + ",";
                        }


                        if (sIncludedOn.equalsIgnoreCase("")) {
                            sIncludedOn="No media";
                            session.putmobiledataPrefs(sIncludedOn);
                            mobiledata.setText(session.getmobiledataPrefsName());
                            sIncludedOn = "";
                            dialog.dismiss();
                          //  Toast.makeText(context, "Please select any one option?", Toast.LENGTH_LONG).show();
                        } else {
                            sIncludedOn = sIncludedOn.substring(0, sIncludedOn.length() - 1);
                            dialog.dismiss();
                            session.putmobiledataPrefs(sIncludedOn);
                            mobiledata.setText(session.getmobiledataPrefsName());
                            sIncludedOn = "";

                            System.out.println("-----------sIncludedOn----------" + sIncludedOn);
                            System.out.println("-----------session.getmobiledataPrefsName()----------" + session.getmobiledataPrefsName());
                        }

                    }

                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);

                final CheckBox c1, c2, c3, c4;
                AvnNextLTProDemiTextView cancel, ok;
                AvnNextLTProRegTextView text;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_mobile_data);
                text = (AvnNextLTProRegTextView) dialog.findViewById(R.id.texthead);
                c1 = (CheckBox) dialog.findViewById(R.id.check1);
                c2 = (CheckBox) dialog.findViewById(R.id.check2);
                c3 = (CheckBox) dialog.findViewById(R.id.check3);
                c4 = (CheckBox) dialog.findViewById(R.id.check4);
                cancel = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.cancel);
                ok = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.ok);

                Typeface typeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
                c1.setTypeface(typeface);
                c2.setTypeface(typeface);
                c3.setTypeface(typeface);
                c4.setTypeface(typeface);

                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                text.setText("When Connected On Wifi");
                String[] values = session.getwifiPrefsName().split(",");
                System.out.println("==================================values" + values);
                for (int i = 0; i < values.length; i++) {
                    if (values[i].equalsIgnoreCase("photo")) {
                        c1.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Audio")) {
                        c2.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Videos")) {
                        c3.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Documents")) {
                        c4.setChecked(true);
                    }
                }


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (c1.isChecked()) {
                            sIncludedOn1 = sIncludedOn1 + "Photo" + ",";
                        }

                        if (c2.isChecked()) {
                            sIncludedOn1 = sIncludedOn1 + "Audio" + ",";
                        }

                        if (c3.isChecked()) {
                            sIncludedOn1 = sIncludedOn1 + "Videos" + ",";
                        }

                        if (c4.isChecked()) {
                            sIncludedOn1 = sIncludedOn1 + "Documents" + ",";
                        }


                        if (sIncludedOn1.equalsIgnoreCase("")) {
                            sIncludedOn1="No media";
                            session.putwifiPrefs(sIncludedOn1);
                            wifi.setText(session.getwifiPrefsName());
                            sIncludedOn1= "";
                            dialog.dismiss();
                        } else {
                            sIncludedOn1 = sIncludedOn1.substring(0, sIncludedOn1.length() - 1);
                            dialog.dismiss();
                            session.putwifiPrefs(sIncludedOn1);
                            wifi.setText(session.getwifiPrefsName());
                            sIncludedOn1 = "";
                            System.out.println("-----------sIncludedOn----------" + sIncludedOn1);
                            System.out.println("-----------session.getmobiledataPrefsName()----------" + session.getmobiledataPrefsName());
                        }

                    }

                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);

                final CheckBox c1, c2, c3, c4;
                AvnNextLTProDemiTextView cancel, ok;
                AvnNextLTProRegTextView text;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_mobile_data);
                text = (AvnNextLTProRegTextView) dialog.findViewById(R.id.texthead);
                c1 = (CheckBox) dialog.findViewById(R.id.check1);
                c2 = (CheckBox) dialog.findViewById(R.id.check2);
                c3 = (CheckBox) dialog.findViewById(R.id.check3);
                c4 = (CheckBox) dialog.findViewById(R.id.check4);
                cancel = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.cancel);
                ok = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.ok);

                Typeface typeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
                c1.setTypeface(typeface);
                c2.setTypeface(typeface);
                c3.setTypeface(typeface);
                c4.setTypeface(typeface);

                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                text.setText("When Roaming");
                String[] values = session.getromingPrefsName().split(",");
                System.out.println("==================================values" + values);
                for (int i = 0; i < values.length; i++) {
                    if (values[i].equalsIgnoreCase("photo")) {
                        c1.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Audio")) {
                        c2.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Videos")) {
                        c3.setChecked(true);
                    }
                    if (values[i].equalsIgnoreCase("Documents")) {
                        c4.setChecked(true);
                    }
                }


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (c1.isChecked()) {
                            sIncludedOn2 = sIncludedOn2 + "Photo" + ",";
                        }

                        if (c2.isChecked()) {
                            sIncludedOn2 = sIncludedOn2 + "Audio" + ",";
                        }

                        if (c3.isChecked()) {
                            sIncludedOn2 = sIncludedOn2 + "Videos" + ",";
                        }

                        if (c4.isChecked()) {
                            sIncludedOn2 = sIncludedOn2 + "Documents" + ",";
                        }


                        if (sIncludedOn2.equalsIgnoreCase("")) {
                            sIncludedOn2="No media";
                            session.putromingPrefs(sIncludedOn2);
                            roaming.setText(session.getromingPrefsName());
                            sIncludedOn2 = "";
                            dialog.dismiss();

                        } else {
                            sIncludedOn2 = sIncludedOn2.substring(0, sIncludedOn2.length() - 1);
                            dialog.dismiss();

                            session.putromingPrefs(sIncludedOn2);
                            roaming.setText(session.getromingPrefsName());
                            sIncludedOn2 = "";

                            System.out.println("-----------sIncludedOn----------" + sIncludedOn);
                            System.out.println("-----------session.getmobiledataPrefsName()----------" + session.getmobiledataPrefsName());
                        }

                    }

                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });
    }
}



