package com.chatapp.synchat.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;

/**
 * created by  Adhash Team on 11/18/2016.
 */
public class NotificationSettings extends CoreActivity implements View.OnClickListener {

    final Context context = this;
    Session session;
    RelativeLayout Relativenotify0, Relativenotify1, Relativenotify2, Relativenotify3, Relativenotify4, Relativenotify5, Relativenotify6, Relativenotify7;
    ImageButton ibBack;
    AvnNextLTProRegTextView tone, vibrate, popup, light, gtone, glight, gpopup, gvibrate;
    TextView conv_tones_sub;
    TextView message_head, group_head;
    AvnNextLTProDemiTextView notification_actionbar_1;

    CheckBox checkbox0;
    RelativeLayout notification_rl2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        session = new Session(NotificationSettings.this);
        final Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        conv_tones_sub = (TextView) findViewById(R.id.r12_txt2);
        conv_tones_sub.setTypeface(face);
        notification_rl2 = (RelativeLayout) findViewById(R.id.notification_rl2);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(this);

        Relativenotify0 = (RelativeLayout) findViewById(R.id.notify_tone);
        Relativenotify1 = (RelativeLayout) findViewById(R.id.notification_rl4);
        Relativenotify2 = (RelativeLayout) findViewById(R.id.notification_rl5);
        Relativenotify3 = (RelativeLayout) findViewById(R.id.notification_rl6);
        Relativenotify4 = (RelativeLayout) findViewById(R.id.notify_group);
        Relativenotify5 = (RelativeLayout) findViewById(R.id.notification_vibrate);
        Relativenotify6 = (RelativeLayout) findViewById(R.id.notification_gpopup);
        Relativenotify7 = (RelativeLayout) findViewById(R.id.grouplight);
        popup = (AvnNextLTProRegTextView) findViewById(R.id.r15_txt2_notification);
        vibrate = (AvnNextLTProRegTextView) findViewById(R.id.r14_txt2_notification);
        tone = (AvnNextLTProRegTextView) findViewById(R.id.r12_txt5);
        light = (AvnNextLTProRegTextView) findViewById(R.id.r16_txt2_notification);
        checkbox0 = (CheckBox) findViewById(R.id.checkbox0);

        gvibrate = (AvnNextLTProRegTextView) findViewById(R.id.vibrate2);
        gpopup = (AvnNextLTProRegTextView) findViewById(R.id.text_popup);
        glight = (AvnNextLTProRegTextView) findViewById(R.id.group_light2);
        gtone = (AvnNextLTProRegTextView) findViewById(R.id.r12_txt5group);


        notification_actionbar_1 = (AvnNextLTProDemiTextView) findViewById(R.id.notification_actionbar_1);


        if (!session.getvibratePrefsName().equals("")) {
            vibrate.setText(session.getvibratePrefsName());
        } else {
            vibrate.setText("Off");
            session.putvibratePrefs("Off");
        }
        if (!session.getpopupPrefsName().equals("")) {
            popup.setText(session.getpopupPrefsName());
        } else {
            popup.setText("No popup");
            session.putpopupPrefs("No popup");
        }
        if (!session.getlightPrefsName().equals("")) {
            light.setText(session.getlightPrefsName());
        } else {
            light.setText("None");
            session.putlightPrefs("None");
        }
        if (!session.getvibratePrefsNamegroup().equals("")) {
            gvibrate.setText(session.getvibratePrefsNamegroup());
        } else {
            gvibrate.setText("Off");
            session.putvibratePrefsgroup("Off");
        }
        if (!session.getpopupPrefsNamegroup().equals("")) {
            gpopup.setText(session.getpopupPrefsNamegroup());
        } else {
            gpopup.setText("No popup");
            session.putpopupPrefsgroup("No popup");
        }
        if (!session.getlightPrefsNamegroup().equals("")) {
            glight.setText(session.getlightPrefsNamegroup());
        } else {
            glight.setText("None");
            session.putlightPrefsgroup("None");
        }
        if (session.getPrefsNameintoneouttone()) {
            checkbox0.setChecked(true);
        } else {
            checkbox0.setChecked(false);
        }


        checkbox0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkbox0.isChecked()) {
                    session.putPrefsintoneouttone(true);

                } else {
                    session.putPrefsintoneouttone(false);
                }
            }
        });

        Relativenotify1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                AvnNextLTProDemiTextView text4, text;
                final RadioButton r1, r2, r3, r4;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.custom_dialog_notification_vibrate);
                text4 = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text4);
                text = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);
                r2 = (RadioButton) dialog.findViewById(R.id.r2);
                r4 = (RadioButton) dialog.findViewById(R.id.r4);
                r1.setTypeface(face);
                r2.setTypeface(face);
                r3.setTypeface(face);
                r4.setTypeface(face);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.getvibratePrefsName().equalsIgnoreCase("Off")) {
                    r1.setChecked(true);
                } else if (session.getvibratePrefsName().equalsIgnoreCase("Default")) {
                    r2.setChecked(true);
                } else if (session.getvibratePrefsName().equalsIgnoreCase("Short")) {
                    r3.setChecked(true);
                } else if (session.getvibratePrefsName().equalsIgnoreCase("Long")) {
                    r4.setChecked(true);
                }


                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (r1.isChecked()) {
                            String value = "Off";
                            session.putvibratePrefs(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            vibrate.setText(session.getvibratePrefsName());

                            dialog.dismiss();

                        } else if (r2.isChecked()) {
                            String value = "Default";
                            session.putvibratePrefs(value);
                            vibrate.setText(session.getvibratePrefsName());

                            dialog.dismiss();
                        } else if (r3.isChecked()) {
                            String value = "Short";
                            session.putvibratePrefs(value);

                            vibrate.setText(session.getvibratePrefsName());
                            dialog.dismiss();
                        } else if (r4.isChecked()) {
                            String value = "Long";
                            session.putvibratePrefs(value);

                            vibrate.setText(session.getvibratePrefsName());
                            dialog.dismiss();
                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        Relativenotify0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rgroup;
                AvnNextLTProDemiTextView done, cancel, head;
                final RadioButton rb_1, rb_2;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_notification_tone);
                rgroup = (RadioGroup) dialog.findViewById(R.id.radiogrouptone);
                rb_1 = (RadioButton) dialog.findViewById(R.id.radiotone1);
                rb_2 = (RadioButton) dialog.findViewById(R.id.radiotone2);

                rb_1.setTypeface(face);
                rb_2.setTypeface(face);

               /* rb_3=(RadioButton)dialog.findViewById(R.id.radiotone3) ;
                rb_4=(RadioButton)dialog.findViewById(R.id.radiotone4) ;*/
                cancel = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text4);


                head = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text);

               /* done=(TextView)dialog.findViewById(R.id.donetone) ;*/
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.getTone().equalsIgnoreCase("None")) {
                    rb_1.setChecked(true);
                } else {
                    rb_2.setChecked(true);
                }
                //rgroup.check(0);
                rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (rb_1.isChecked()) {
                            String value = "None";
                            session.putTone(value);
                            tone.setText(session.getTone());
                            dialog.dismiss();

                        } else if (rb_2.isChecked()) {
                            String value = getString(R.string.Default_ringtone);
                            session.putTone(value);

                            tone.setText(session.getTone());
                            dialog.dismiss();
                        }
                        /*else if(rb_3.isChecked()){
                            String value ="Opener";
                            session.putTone(value);
                            mp = MediaPlayer.create(Notification.this, R.raw.end_of_call);
                            if(mp.isPlaying())
                            {
                                mp.stop();
                            }
                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            tone.setText(session.getTone());


                        }
                        else if(rb_4.isChecked()){
                            String value ="Pure Bell";
                            session.putTone(value);
                            mp = MediaPlayer.create(Notification.this, R.raw.piano);

                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    if(mp.isPlaying())
                                    {
                                        mp.stop();
                                    }
                                    mp.start();
                                }
                            });
                            tone.setText(session.getTone());
*/

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
        Relativenotify2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                AvnNextLTProDemiTextView text4, head;
                final RadioButton r1, r2, r3, r4;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.popupdialog);
                text4 = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text4);
                head = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);
                r2 = (RadioButton) dialog.findViewById(R.id.r2);
                r4 = (RadioButton) dialog.findViewById(R.id.r4);
                r1.setTypeface(face);
                r2.setTypeface(face);
                r3.setTypeface(face);
                r4.setTypeface(face);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.getpopupPrefsName().equalsIgnoreCase("No popup")) {
                    r1.setChecked(true);
                } else if (session.getpopupPrefsName().equalsIgnoreCase("Only when screen on")) {
                    r2.setChecked(true);
                } else if (session.getpopupPrefsName().equalsIgnoreCase("Only when screen off")) {
                    r3.setChecked(true);
                } else if (session.getpopupPrefsName().equalsIgnoreCase("Always show popup")) {
                    r4.setChecked(true);
                }


                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (r1.isChecked()) {
                            String value = "No popup";
                            session.putpopupPrefs(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            popup.setText(session.getpopupPrefsName());
                            dialog.dismiss();

                        } else if (r2.isChecked()) {
                            String value = "Only when screen on";
                            session.putpopupPrefs(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            popup.setText(session.getpopupPrefsName());
                            dialog.dismiss();
                        } else if (r3.isChecked()) {
                            String value = "Only when screen off";
                            session.putpopupPrefs(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            popup.setText(session.getpopupPrefsName());
                            dialog.dismiss();
                        } else if (r4.isChecked()) {
                            String value = "Always show popup";
                            session.putpopupPrefs(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            popup.setText(session.getpopupPrefsName());
                            dialog.dismiss();
                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        Relativenotify3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                AvnNextLTProDemiTextView text4, text;
                final RadioButton r1, r2, r3, r4, r5, r6, r7, r8;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.lightdialog);
                text4 = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text4);
                text = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);
                r2 = (RadioButton) dialog.findViewById(R.id.r2);
                r4 = (RadioButton) dialog.findViewById(R.id.r4);
                r5 = (RadioButton) dialog.findViewById(R.id.r5);
                r6 = (RadioButton) dialog.findViewById(R.id.r6);
                r7 = (RadioButton) dialog.findViewById(R.id.r7);
                r8 = (RadioButton) dialog.findViewById(R.id.r8);
                r1.setTypeface(face);
                r2.setTypeface(face);
                r3.setTypeface(face);
                r4.setTypeface(face);
                r5.setTypeface(face);
                r6.setTypeface(face);
                r7.setTypeface(face);
                r8.setTypeface(face);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.getlightPrefsName().equalsIgnoreCase("None")) {
                    r1.setChecked(true);
                } else if (session.getlightPrefsName().equalsIgnoreCase("white")) {
                    r2.setChecked(true);
                }
                if (session.getlightPrefsName().equalsIgnoreCase("Red")) {
                    r3.setChecked(true);
                } else if (session.getlightPrefsName().equalsIgnoreCase("Yellow")) {
                    r4.setChecked(true);
                }
                if (session.getlightPrefsName().equalsIgnoreCase("Green")) {
                    r5.setChecked(true);
                } else if (session.getlightPrefsName().equalsIgnoreCase("Cyan")) {
                    r6.setChecked(true);
                }
                if (session.getlightPrefsName().equalsIgnoreCase("Blue")) {
                    r7.setChecked(true);
                } else if (session.getlightPrefsName().equalsIgnoreCase("Purple")) {
                    r8.setChecked(true);
                }

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (r1.isChecked()) {
                            String value = "None";
                            session.putlightPrefs(value);

                            light.setText(session.getlightPrefsName());
                            dialog.dismiss();

                        } else if (r2.isChecked()) {
                            String value = "White";
                            session.putlightPrefs(value);

                            light.setText(session.getlightPrefsName());
                            dialog.dismiss();
                        }

                        if (r3.isChecked()) {
                            String value = "Red";
                            session.putlightPrefs(value);
                            light.setText(session.getlightPrefsName());
                            dialog.dismiss();

                        } else if (r4.isChecked()) {
                            String value = "Yellow";
                            session.putlightPrefs(value);

                            light.setText(session.getlightPrefsName());
                            dialog.dismiss();
                        }

                        if (r5.isChecked()) {
                            String value = "Green";
                            session.putlightPrefs(value);

                            light.setText(session.getlightPrefsName());
                            dialog.dismiss();

                        } else if (r6.isChecked()) {
                            String value = "Cyan";
                            session.putlightPrefs(value);

                            light.setText(session.getlightPrefsName());
                            dialog.dismiss();
                        }

                        if (r7.isChecked()) {
                            String value = "Blue";
                            session.putlightPrefs(value);

                            light.setText(session.getlightPrefsName());
                            dialog.dismiss();

                        } else if (r8.isChecked()) {
                            String value = "Purple";
                            session.putlightPrefs(value);

                            light.setText(session.getlightPrefsName());
                            dialog.dismiss();
                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        Relativenotify5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                AvnNextLTProDemiTextView text4, text;
                final RadioButton r1, r2, r3, r4;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.custom_dialog_notification_vibrate);
                text4 = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text4);
                text = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);
                r2 = (RadioButton) dialog.findViewById(R.id.r2);
                r4 = (RadioButton) dialog.findViewById(R.id.r4);
                r1.setTypeface(face);
                r2.setTypeface(face);
                r3.setTypeface(face);
                r4.setTypeface(face);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.getvibratePrefsNamegroup().equalsIgnoreCase("Off")) {
                    r1.setChecked(true);
                } else if (session.getvibratePrefsNamegroup().equalsIgnoreCase("Default")) {
                    r2.setChecked(true);
                } else if (session.getvibratePrefsNamegroup().equalsIgnoreCase("Short")) {
                    r3.setChecked(true);
                } else if (session.getvibratePrefsNamegroup().equalsIgnoreCase("Long")) {
                    r4.setChecked(true);
                }


                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (r1.isChecked()) {
                            String value = "Off";
                            session.putvibratePrefsgroup(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            gvibrate.setText(session.getvibratePrefsNamegroup());

                            dialog.dismiss();

                        } else if (r2.isChecked()) {
                            String value = "Default";
                            session.putvibratePrefsgroup(value);
                            gvibrate.setText(session.getvibratePrefsNamegroup());

                            dialog.dismiss();
                        } else if (r3.isChecked()) {
                            String value = "Short";
                            session.putvibratePrefsgroup(value);

                            gvibrate.setText(session.getvibratePrefsNamegroup());
                            dialog.dismiss();
                        } else if (r4.isChecked()) {
                            String value = "Long";
                            session.putvibratePrefsgroup(value);
                            gvibrate.setText(session.getvibratePrefsNamegroup());
                            dialog.dismiss();
                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        Relativenotify7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                AvnNextLTProDemiTextView text4, text;
                final RadioButton r1, r2, r3, r4, r5, r6, r7, r8;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.lightdialog);
                text = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text);
                text4 = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text4);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);
                r2 = (RadioButton) dialog.findViewById(R.id.r2);
                r4 = (RadioButton) dialog.findViewById(R.id.r4);
                r5 = (RadioButton) dialog.findViewById(R.id.r5);
                r6 = (RadioButton) dialog.findViewById(R.id.r6);
                r7 = (RadioButton) dialog.findViewById(R.id.r7);
                r8 = (RadioButton) dialog.findViewById(R.id.r8);
                r1.setTypeface(face);
                r2.setTypeface(face);
                r3.setTypeface(face);
                r4.setTypeface(face);
                r5.setTypeface(face);
                r7.setTypeface(face);
                r6.setTypeface(face);
                r8.setTypeface(face);

                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.getlightPrefsNamegroup().equalsIgnoreCase("None")) {
                    r1.setChecked(true);
                } else if (session.getlightPrefsNamegroup().equalsIgnoreCase("white")) {
                    r2.setChecked(true);
                }
                if (session.getlightPrefsNamegroup().equalsIgnoreCase("Red")) {
                    r3.setChecked(true);
                } else if (session.getlightPrefsNamegroup().equalsIgnoreCase("Yellow")) {
                    r4.setChecked(true);
                }
                if (session.getlightPrefsNamegroup().equalsIgnoreCase("Green")) {
                    r5.setChecked(true);
                } else if (session.getlightPrefsNamegroup().equalsIgnoreCase("Cyan")) {
                    r6.setChecked(true);
                }
                if (session.getlightPrefsNamegroup().equalsIgnoreCase("Blue")) {
                    r7.setChecked(true);
                } else if (session.getlightPrefsNamegroup().equalsIgnoreCase("Purple")) {
                    r8.setChecked(true);
                }

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (r1.isChecked()) {
                            String value = "None";
                            session.putlightPrefsgroup(value);

                            glight.setText(session.getlightPrefsNamegroup());
                            dialog.dismiss();

                        } else if (r2.isChecked()) {
                            String value = "White";
                            session.putlightPrefsgroup(value);

                            glight.setText(session.getlightPrefsNamegroup());
                            dialog.dismiss();
                        }

                        if (r3.isChecked()) {
                            String value = "Red";
                            session.putlightPrefsgroup(value);
                            glight.setText(session.getlightPrefsNamegroup());
                            dialog.dismiss();

                        } else if (r4.isChecked()) {
                            String value = "Yellow";
                            session.putlightPrefsgroup(value);

                            glight.setText(session.getlightPrefsNamegroup());
                            dialog.dismiss();
                        }

                        if (r5.isChecked()) {
                            String value = "Green";
                            session.putlightPrefsgroup(value);

                            glight.setText(session.getlightPrefsNamegroup());
                            dialog.dismiss();

                        } else if (r6.isChecked()) {
                            String value = "Cyan";
                            session.putlightPrefsgroup(value);

                            glight.setText(session.getlightPrefsNamegroup());
                            dialog.dismiss();
                        }

                        if (r7.isChecked()) {
                            String value = "Blue";
                            session.putlightPrefsgroup(value);

                            glight.setText(session.getlightPrefsNamegroup());
                            dialog.dismiss();

                        } else if (r8.isChecked()) {
                            String value = "Purple";
                            session.putlightPrefsgroup(value);

                            glight.setText(session.getlightPrefsNamegroup());
                            dialog.dismiss();
                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        Relativenotify6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                AvnNextLTProDemiTextView text4, text;
                final RadioButton r1, r2, r3, r4;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.popupdialog);
                text4 = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text4);
                text = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);
                r2 = (RadioButton) dialog.findViewById(R.id.r2);
                r4 = (RadioButton) dialog.findViewById(R.id.r4);
                r1.setTypeface(face);
                r2.setTypeface(face);
                r3.setTypeface(face);
                r4.setTypeface(face);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.getpopupPrefsNamegroup().equalsIgnoreCase("No popup")) {
                    r1.setChecked(true);
                } else if (session.getpopupPrefsNamegroup().equalsIgnoreCase("Only when screen on")) {
                    r2.setChecked(true);
                } else if (session.getpopupPrefsNamegroup().equalsIgnoreCase("Only when screen off")) {
                    r3.setChecked(true);
                } else if (session.getpopupPrefsNamegroup().equalsIgnoreCase("Always show popup")) {
                    r4.setChecked(true);
                }


                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (r1.isChecked()) {
                            String value = "No popup";
                            session.putpopupPrefsgroup(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            gpopup.setText(session.getpopupPrefsNamegroup());
                            dialog.dismiss();

                        } else if (r2.isChecked()) {
                            String value = "Only when screen on";
                            session.putpopupPrefsgroup(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            gpopup.setText(session.getpopupPrefsNamegroup());
                            dialog.dismiss();
                        } else if (r3.isChecked()) {
                            String value = "Only when screen off";
                            session.putpopupPrefsgroup(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            gpopup.setText(session.getpopupPrefsNamegroup());
                            dialog.dismiss();
                        } else if (r4.isChecked()) {
                            String value = "Always show popup";
                            session.putpopupPrefsgroup(value);
                            System.out.println("+==========================================>" + session.getvibratePrefsName());
                            gpopup.setText(session.getpopupPrefsNamegroup());
                            dialog.dismiss();
                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        Relativenotify4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rgroup;
                AvnNextLTProDemiTextView done, cancel;
                final RadioButton rb_1, rb_2;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_notification_tone);
                rgroup = (RadioGroup) dialog.findViewById(R.id.radiogrouptone);
                rb_1 = (RadioButton) dialog.findViewById(R.id.radiotone1);
                rb_2 = (RadioButton) dialog.findViewById(R.id.radiotone2);
               /* rb_3=(RadioButton)dialog.findViewById(R.id.radiotone3) ;
                rb_4=(RadioButton)dialog.findViewById(R.id.radiotone4) ;*/
                cancel = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.text4);
                cancel.setTypeface(face);
                rb_1.setTypeface(face);
                rb_2.setTypeface(face);
               /* done=(TextView)dialog.findViewById(R.id.donetone) ;*/
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (session.getgroupTone().equalsIgnoreCase("None")) {
                    rb_1.setChecked(true);
                } else {
                    rb_2.setChecked(true);
                }
                //rgroup.check(0);
                rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (rb_1.isChecked()) {
                            String value = "None";
                            session.putgroupTone(value);
                            gtone.setText(session.getgroupTone());
                            dialog.dismiss();

                        } else if (rb_2.isChecked()) {
                            String value = getString(R.string.Default_ringtone);
                            session.putgroupTone(value);
                            gtone.setText(session.getgroupTone());
                            dialog.dismiss();
                        }
                        /*else if(rb_3.isChecked()){
                            String value ="Opener";
                            session.putTone(value);
                            mp = MediaPlayer.create(Notification.this, R.raw.end_of_call);
                            if(mp.isPlaying())
                            {
                                mp.stop();
                            }
                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            tone.setText(session.getTone());


                        }
                        else if(rb_4.isChecked()){
                            String value ="Pure Bell";
                            session.putTone(value);
                            mp = MediaPlayer.create(Notification.this, R.raw.piano);

                            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    if(mp.isPlaying())
                                    {
                                        mp.stop();
                                    }
                                    mp.start();
                                }
                            });
                            tone.setText(session.getTone());
*/

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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ibBack:
                finish();
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_notification, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuResetNotify) {
            CustomAlertDialog dialog = new CustomAlertDialog();
            dialog.setMessage("Reset all notification settings for your chats?");
            dialog.setPositiveButtonText("RESET");
            dialog.setNegativeButtonText("CANCEL");
            dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                @Override
                public void onPositiveButtonClick() {
                    String defTone = getString(R.string.Default_ringtone);
                    session.putTone(defTone);
                    tone.setText(defTone);
                    session.putvibratePrefs("Default");
                    vibrate.setText("Default");
                    session.putpopupPrefs("No popup");
                    popup.setText("No popup");
                    session.putlightPrefs("None");
                    light.setText("None");
                    session.putgroupTone("Default ringtone(beep once)");
                    gtone.setText(defTone);
                    session.putvibratePrefsgroup("Default");
                    gvibrate.setText("Default");
                    session.putpopupPrefsgroup("No popup");
                    gpopup.setText("No popup");
                    session.putlightPrefsgroup("None");
                    glight.setText("None");
                    checkbox0.setChecked(false);


                }

                @Override
                public void onNegativeButtonClick() {

                }
            });
            dialog.show(getSupportFragmentManager(), "CustomAlert");
        }

        return super.onOptionsItemSelected(item);
    }
}