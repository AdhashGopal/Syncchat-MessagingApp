/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.webrtc.RendererCommon.ScalingType;

/**
 * Fragment for call control.
 */
public class CallFragment extends Fragment {
  private View controlView;
  private TextView contactView;
  private ImageButton disconnectButton;
  private ImageView cameraSwitchButton;
  private ImageView videoScalingButton;
  private ImageView toggleMuteButton;
  private TextView captureFormatText;
  private SeekBar captureFormatSlider;
  private OnCallEvents callEvents;
  private ScalingType scalingType;
  private boolean videoCallEnabled = true;
  private ImageView audio_speaker;
  private ImageView ivToggleSpeaker;
  private ImageView button_audio_call_toggle_mic;
  private AudioManager audioManager;
  private static final String TAG = "CallFragment";
  /**
   * Call control interface for container activity.
   */
  public interface OnCallEvents {
    void onCallHangUp();
    void onCameraSwitch();
    void onVideoScalingSwitch(ScalingType scalingType);
    void onCaptureFormatChange(int width, int height, int framerate);
    boolean onToggleMic();
    boolean onToggleSpeaker();
    void onPageClick();

    void gotomsg();
  }

  @Override
  public View onCreateView(
          LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    controlView = inflater.inflate(R.layout.fragment_call, container, false);

    audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
    // Create UI controls.
    contactView = (TextView) controlView.findViewById(R.id.contact_name_call);
    disconnectButton = (ImageButton) controlView.findViewById(R.id.button_call_disconnect);
    cameraSwitchButton = (ImageView) controlView.findViewById(R.id.button_call_switch_camera);
    videoScalingButton = (ImageView) controlView.findViewById(R.id.button_call_scaling_mode);
    ivToggleSpeaker = (ImageView) controlView.findViewById(R.id.ibToggleSpeaker);
    toggleMuteButton = (ImageView) controlView.findViewById(R.id.button_call_toggle_mic);
    captureFormatText = (TextView) controlView.findViewById(R.id.capture_format_text_call);
    captureFormatSlider = (SeekBar) controlView.findViewById(R.id.capture_format_slider_call);
    button_audio_call_toggle_mic=(ImageView)controlView.findViewById(R.id.button_audio_call_toggle_mic);
    audio_speaker=(ImageView)controlView.findViewById(R.id.audio_speaker);
    // Add buttons click events.
    disconnectButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        callEvents.onCallHangUp();
      }
    });

    cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        callEvents.onCameraSwitch();
      }
    });

    button_audio_call_toggle_mic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        boolean enabled = callEvents.onToggleMic();
        button_audio_call_toggle_mic.setAlpha(enabled ? 1.0f : 0.3f);
      }
    });



    ivToggleSpeaker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "onClick: speaker click");
        boolean enabled = callEvents.onToggleSpeaker();
        if (enabled) {
          ivToggleSpeaker.setImageResource(R.drawable.ic_specker_on);
        } else {
          ivToggleSpeaker.setImageResource(R.drawable.ic_specker_off);
        }

      }
    });
    Log.d(TAG, "onCreateView: video enabled: "+videoCallEnabled);
    videoCallEnabled = getArguments().getBoolean(CallActivity.EXTRA_VIDEO_CALL, true);
    //voice call - disable loud speaker
    if(!videoCallEnabled){
      ivToggleSpeaker.setImageResource(R.drawable.ic_specker_off);
      boolean enabled = callEvents.onToggleMic();
      if(!enabled)
        callEvents.onToggleMic();
    }
    //video call --> enable loud speaker
    else {

      if(audioManager.isWiredHeadsetOn()){
        ivToggleSpeaker.setImageResource(R.drawable.ic_specker_off);
        boolean enableds = callEvents.onToggleMic();
        if(!enableds)
          callEvents.onToggleMic();
      }else {
        ivToggleSpeaker.setImageResource(R.drawable.ic_specker_on);
        boolean enabled = callEvents.onToggleSpeaker();
        if(!enabled)
          callEvents.onToggleSpeaker();
      }

    }
    audio_speaker.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Thread thread = new Thread() {
          @Override
          public void run() {
            try {
              while(true) {
                sleep(1000);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                if (!audioManager.isSpeakerphoneOn()) {
                  audioManager.setSpeakerphoneOn(true);
                }else{
                  audioManager.setSpeakerphoneOn(false);
                }
              }
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        };

        thread.start();
      }
    });

    videoScalingButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
          videoScalingButton.setBackgroundResource(R.drawable.ic_action_full_screen);
          scalingType = ScalingType.SCALE_ASPECT_FIT;
        } else {
          videoScalingButton.setBackgroundResource(R.drawable.ic_action_return_from_full_screen);
          scalingType = ScalingType.SCALE_ASPECT_FILL;
        }
        callEvents.onVideoScalingSwitch(scalingType);
      }
    });
    scalingType = ScalingType.SCALE_ASPECT_FILL;

    toggleMuteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        boolean enabled = callEvents.onToggleMic();
        toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
      }
    });

    return controlView;
  }

  @Override
  public void onStart() {
    super.onStart();

    boolean captureSliderEnabled = false;
    Bundle args = getArguments();
    if (args != null) {
      String contactName = args.getString(CallActivity.EXTRA_ROOMID);
      contactView.setText(contactName);
      videoCallEnabled = args.getBoolean(CallActivity.EXTRA_VIDEO_CALL, true);
      captureSliderEnabled = videoCallEnabled
              && args.getBoolean(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
    }
    if (!videoCallEnabled) {
      cameraSwitchButton.setVisibility(View.INVISIBLE);
      button_audio_call_toggle_mic.setVisibility(View.VISIBLE);
      toggleMuteButton.setVisibility(View.GONE);
    }else{
      button_audio_call_toggle_mic.performClick();
      button_audio_call_toggle_mic.setVisibility(View.GONE);
      toggleMuteButton.setVisibility(View.VISIBLE);
      boolean enableds = callEvents.onToggleMic();

    }
    if (captureSliderEnabled) {
      captureFormatSlider.setOnSeekBarChangeListener(
              new CaptureQualityController(captureFormatText, callEvents));
    } else {
      captureFormatText.setVisibility(View.GONE);
      captureFormatSlider.setVisibility(View.GONE);
    }
  }

  // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    callEvents = (OnCallEvents) activity;
  }
}
