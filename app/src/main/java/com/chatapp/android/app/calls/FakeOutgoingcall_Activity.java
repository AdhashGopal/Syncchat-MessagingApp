package com.chatapp.android.app.calls;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;

import java.io.IOException;

public class FakeOutgoingcall_Activity extends AppCompatActivity implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

    private ImageView img_btn_disconnect, img_btn_cameraswitch;
    MediaPlayer player;
    private int currentCameraId = 1;
    private AvnNextLTProRegTextView tv_name;
    String call = "",mCurrentUserId = "";
    AvnNextLTProRegTextView tv_tvCallLbl;
    ImageView img_ibToggleSpeaker_off,img_ibToggleSpeaker,img_user_preview_voicecall;
    private static final String TAG = FakeOutgoingcall_Activity.class.getSimpleName()+"$$$$";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_outgoingcall_layotu);

        Log.d(TAG, "onCreate: ");
        getWindow().setFormat(PixelFormat.UNKNOWN);

        WindowManager.LayoutParams layoutParamsControl
                = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT);
        tv_name = (AvnNextLTProRegTextView) findViewById(R.id.tvName);

        if (getIntent() != null) {
            tv_name.setText(getIntent().getStringExtra("Username"));
            call = getIntent().getStringExtra("call");
            mCurrentUserId = getIntent().getStringExtra("mCurrentUserId");
        }
        img_btn_disconnect = (ImageView) findViewById(R.id.button_call_disconnect);
        img_btn_cameraswitch = (ImageView) findViewById(R.id.button_call_switch_camera);
        img_ibToggleSpeaker = (ImageView) findViewById(R.id.ibToggleSpeaker);
        img_ibToggleSpeaker_off = (ImageView) findViewById(R.id.ibToggleSpeaker_off);
        img_user_preview_voicecall = (ImageView) findViewById(R.id.img_user);
        tv_tvCallLbl = (AvnNextLTProRegTextView) findViewById(R.id.tvCallLbl);
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);

        if(call.equalsIgnoreCase("1")){
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }else{
            img_ibToggleSpeaker.setVisibility(View.GONE);
            img_btn_cameraswitch.setVisibility(View.GONE);
            surfaceView.setVisibility(View.GONE);
            img_ibToggleSpeaker_off.setVisibility(View.VISIBLE);
            img_user_preview_voicecall.setVisibility(View.VISIBLE);
            tv_tvCallLbl.setText("NowLetsChat Voice Call");
            Getcontactname getcontactname = new Getcontactname(this);
            getcontactname.configProfilepic(img_user_preview_voicecall, mCurrentUserId, false, false, R.mipmap.chat_attachment_profile_default_image_frame);

        }


        img_btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                finish();
           //     Toast.makeText(FakeOutgoingcall_Activity.this,"call disconnected",Toast.LENGTH_SHORT).show();
            }
        });
        img_btn_cameraswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previewing) {
                    camera.stopPreview();
                }
//NB: if you don't release the current camera before switching, you app will crash
                camera.release();

//swap the id of the camera to be used
                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                camera = Camera.open(currentCameraId);

                setCameraDisplayOrientation(FakeOutgoingcall_Activity.this, currentCameraId, camera);
                try {

                    camera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }
        });

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        player = MediaPlayer.create(this, R.raw.call_tone);
        player.start();

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if(call.equalsIgnoreCase("1")) {
            if (previewing) {
                camera.stopPreview();
                previewing = false;
            }

            if (camera != null) {
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.setDisplayOrientation(90);
                    camera.startPreview();
                    previewing = true;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if(call.equalsIgnoreCase("1")) {
            camera = Camera.open(1);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if(call.equalsIgnoreCase("1")) {
            camera.stopPreview();
            camera.release();
            camera = null;
            previewing = false;
            player.stop();
        }
    }


    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
