package com.chatapp.synchat.app.utils;

/**
 * created by  Adhash Team  on 26/09/17.
 *
 */

public interface MyExoPlayerListener {

    void onError();

    void onCompleted();

    void onStartPlaying();

    void onVideoChanged(boolean isImageAds, boolean isAds);
    void onPlayerStateChanged(boolean playWhenReady, int playbackState);
}
