package com.chatapp.synchat.app.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.IllegalSeekPositionException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * created by  Adhash Team on 25/09/17.
 */

public class MyExoPlayer implements Player.EventListener, VideoRendererEventListener, AdaptiveMediaSourceEventListener, ExtractorMediaSource.EventListener {
    private static final String TAG = "MyExoPlayer";
    private static DefaultBandwidthMeter mBandwidthMeter;
    private SimpleExoPlayer mExoPlayer;


    private boolean isPaused = false;
    private MyExoPlayerListener myExoPlayerListener;
    private int mPlayerCode;
    private int mTrack = 0;
    private float mCurrentVolume = 1f;
    private Handler mainHandler;
    private List<Integer> mImageAdsIndex = new ArrayList<>();
    private List<Integer> mVideoAdsIndex = new ArrayList<>();

    public MediaSource getMediaSourceFromUrl(Context context, String url) {
        MediaSource videoSource = null;

        try {
            //CHOOSE CONTENT: LiveStream / SdCard
            mainHandler = new Handler(Looper.getMainLooper());

//LIVE STREAM SOURCE: * Livestream links may be out of date so find any m3u8 files online and replace:


//        Uri mp4VideoUri =Uri.parse("FIND A WORKING LINK ABD PLUg INTO HERE"); //PLUG INTO HERE<------------------------------------------


//VIDEO FROM SD CARD: (2 steps. set up file and path, then change videoSource to get the file)
//        String urimp4 = "path/FileName.mp4"; //upload file to device and add path/name.mp4
//        Uri mp4VideoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+urimp4);

            //Measures bandwidth during playback. Can be null if not required.
            mBandwidthMeter = new DefaultBandwidthMeter();
//Produces DataSource instances through which media data is loaded.
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "MOD"), mBandwidthMeter);
//Produces Extractor instances for parsing the media data.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            if (url != null && !url.isEmpty()) {
                MyLog.d(TAG, "getMediaSourceFromUrl: 11");
                if (url.contains(".m3u8")) {
//FOR LIVESTREAM LINK:
                    MyLog.d(TAG, "getMediaSourceFromUrl: 2");
                    Uri mp4VideoUri = Uri.parse(url);
                    MyLog.d(TAG, "getMediaSourceFromUrl: mp4VideoUri " + mp4VideoUri);
                    try {
                        videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, mainHandler, this);
                    } catch (Exception e) {
                        MyLog.e(TAG, "getMediaSourceFromUrl: ", e);
                    }
                    /*videoSource = new HlsMediaSource(Uri.parse(url),
                        dataSourceFactory,mainHandler, null);*/
                } else  {
                    Uri mp4VideoUri = Uri.parse(url);
                    videoSource = new ExtractorMediaSource(mp4VideoUri,
                            new CacheDataSourceFactory(context, 1000 * 1024 * 1024, 50 * 1024 * 1024), new DefaultExtractorsFactory(), null, null);
                    //videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, this);
                }
                MyLog.d(TAG, "getMediaSourceFromUrl: 4" + videoSource);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "getMediaSourceFromUrl: ", e);
        }

        return videoSource;
    }
    public MediaSource getMediaSourceFromLocalFile(Context context,String path){
        DataSpec dataSpec = new DataSpec(Uri.fromFile(new File(path)));
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (Exception e) {
            MyLog.e(TAG, "getMediaSourceFromLocalFile: ",e );
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        return new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);
    }

    public void loadPlayer(Context context, int playerCode, SimpleExoPlayerView simpleExoPlayerView, MyExoPlayerListener myExoPlayerListener) {

        this.myExoPlayerListener = myExoPlayerListener;
        this.mPlayerCode = playerCode;

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
// 1. Create a default TrackSelector
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector mTrackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // mTrackSelector.setParameters(new DefaultTrackSelector.Parameters().withMaxVideoBitrate(2));

        //setMaxBitRate(160);

// 2. Create a default LoadControl
        // LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player
        //  player = ExoPlayerFactory.newSimpleInstance(this, mTrackSelector, loadControl); //deprecated..
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, mTrackSelector);


//Set media controller
        // simpleExoPlayerView.setUseController(false);
        simpleExoPlayerView.requestFocus();

// Bind the player to the view.
        simpleExoPlayerView.setPlayer(mExoPlayer);

        mExoPlayer.addListener(this);


        mExoPlayer.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution
    }

    public void setMediaSource(MediaSource mediaSource) {
        mExoPlayer.prepare(mediaSource);
    }

    public void setImageAdsIndex(List<Integer> imageAdsIndex) {
        mImageAdsIndex = imageAdsIndex;
    }

    public void setVideoAdsIndex(List<Integer> videoAdsIndex) {
        mVideoAdsIndex = videoAdsIndex;
    }

/*
    public void setMaxBitRate(int maxBitrate) {
        if (mTrackSelector != null)
            mTrackSelector.setParameters(
                    mTrackSelector.getParameters().withMaxVideoBitrate(maxBitrate));
    }
*/

    public MediaSource getMediaSourceFromUri2(Context context, Uri final_uri,String Local_url) {
        Uri newUri =  Uri.fromFile(new File(Local_url));
        DataSpec dataSpec = new DataSpec(newUri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        mExoPlayer.prepare(audioSource);

        return audioSource;
    }
    public ExoPlayer getExoPlayer() {
        return mExoPlayer;
    }

    public void play() {
        MyLog.d(TAG, "play: ");
        myExoPlayerListener.onStartPlaying();
        mExoPlayer.setPlayWhenReady(true); //run file/link when ready to play.
    }

    /*@Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        MyLog.d(TAG, "onTimelineChanged: ");
    }*/

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        MyLog.d(TAG, "onTracksChanged: 1 " + trackGroups);

        MyLog.d(TAG, "onTracksChanged: mTrack " + mTrack);

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        MyLog.d(TAG, "onLoadingChanged: " + isLoading);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        MyLog.d(TAG, "onPlayerStateChanged: " + playbackState);
        //if (mPlayerCode == 0)
            myExoPlayerListener.onPlayerStateChanged(playWhenReady, playbackState);

        if (playWhenReady && (playbackState == PlaybackStateCompat.STATE_PAUSED)) {
            try {
                int currentWindowIndex = mExoPlayer.getCurrentWindowIndex();

                long resumePosition = Math.max(0, mExoPlayer.getContentPosition());

                MyLog.d(TAG, "onPlayerStateChanged:>> currentWindowIndex: " + currentWindowIndex + " track: " + mTrack);
                MyLog.d(TAG, "onPlayerStateChanged: " + resumePosition);

                //mExoPlayer.seekToDefaultPosition(mTrack);
                mTrack++;
            } catch (IllegalSeekPositionException e) {
                myExoPlayerListener.onCompleted();
                MyLog.e(TAG, "onPlayerStateChanged: IllegalSeekPositionException: ", e);
            } catch (Exception e) {
                MyLog.e(TAG, "onPlayerStateChanged: ", e);
            }
        }

        if (playbackState == PlaybackStateCompat.STATE_FAST_FORWARDING) {
            if (myExoPlayerListener != null)
                myExoPlayerListener.onCompleted();
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        MyLog.d(TAG, "onRepeatModeChanged: ");
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        MyLog.d(TAG, "onPlayerError: " + error.getMessage());
        if (myExoPlayerListener != null)
            myExoPlayerListener.onError();

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    /*@Override
    public void onPositionDiscontinuity() {
        try {
            boolean isVideoAds = false;
            if (mExoPlayer != null) {
                MyLog.d(TAG, "onPositionDiscontinuity: " + mExoPlayer.getCurrentWindowIndex());
                MyLog.d(TAG, "onPositionDiscontinuity: " + mExoPlayer.getContentPosition() / 1000);

                if (mVideoAdsIndex != null && mVideoAdsIndex.size() > 0) {
                    if (mVideoAdsIndex.contains(mExoPlayer.getCurrentWindowIndex())) {
                        isVideoAds = true;
                    }
                }
                Log.d(TAG, "onPositionDiscontinuity isVideoAds: " + isVideoAds);
                Log.d(TAG, "onPositionDiscontinuity mVideoAdsIndex: " + mVideoAdsIndex);
                if (myExoPlayerListener != null) {
                    if ((mImageAdsIndex != null && mImageAdsIndex.contains(mExoPlayer.getCurrentWindowIndex()))) {
                        MyLog.d(TAG, "onPositionDiscontinuity: is image position");
                        myExoPlayerListener.onVideoChanged(true, true);
                    } else {
                        MyLog.d(TAG, "onPositionDiscontinuity: is video position");
                        myExoPlayerListener.onVideoChanged(false, isVideoAds);
                    }
                }
            }

        } catch (Exception e) {
            MyLog.e(TAG, "onPositionDiscontinuity: ", e);
        }
    }*/

    public boolean isAdUrl() {
        try {
            if (mImageAdsIndex.contains(mExoPlayer.getCurrentWindowIndex()) || mVideoAdsIndex.contains(mExoPlayer.getCurrentWindowIndex())) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        MyLog.d(TAG, "onPlaybackParametersChanged: ");
    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {
        MyLog.d(TAG, "onVideoEnabled: ");
    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        MyLog.d(TAG, "onVideoDecoderInitialized: ");
    }

    @Override
    public void onVideoInputFormatChanged(Format format) {
        MyLog.d(TAG, "onVideoInputFormatChanged: ");
    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {
        MyLog.d(TAG, "onDroppedFrames: Count: " + count + " Frames (Ms): " + elapsedMs);

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        MyLog.d(TAG, "onVideoSizeChanged: ");
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

        MyLog.d(TAG, "onRenderedFirstFrame: ");
    }


    public void mutePlayer() {
        mCurrentVolume = mExoPlayer.getVolume();
        mExoPlayer.setVolume(0f);
    }

    public void unMutePlayer() {
        mExoPlayer.setVolume(mCurrentVolume);
    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {
        MyLog.d(TAG, "onVideoDisabled: ");
    }

    public void pausePlayer() {
        MyLog.d(TAG, "pausePlayer: ");
        try {
            if (mExoPlayer != null) {
                MyLog.d(TAG, "pausePlayer: state: " + mExoPlayer.getPlaybackState());
                isPaused = true;
                mExoPlayer.setPlayWhenReady(false);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "pausePlayer: ", e);
        }
    }

    public void resumePlayer() {
        MyLog.d(TAG, "resumePlayer: isPaused: " + isPaused);
        try {
            if (isPaused && mExoPlayer != null) {
                isPaused = false;
                MyLog.d(TAG, "resumePlayer: state: " + mExoPlayer.getPlaybackState());
                mExoPlayer.setPlayWhenReady(true);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "resumePlayer: ", e);
        }
    }

    public void resumePlayerPrepare(MediaSource source) {
        MyLog.d(TAG, "resumePlayer: isPaused: " + isPaused);
        try {
            if (source != null) {
                mExoPlayer.prepare(source, false, false);
            }
            mExoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            MyLog.e(TAG, "resumePlayer: ", e);
        }
    }


    public void releasePlayer() {
        Log.i(TAG, "releasePlayer: ");
        if (mExoPlayer != null)
            mExoPlayer.release();

    }

    public int getCurrentWindowIndex() {
        if (mExoPlayer != null) {
            return mExoPlayer.getCurrentWindowIndex();
        }
        return 0;
    }

    public long getCurrentDurationUpdated() {
        long currentPosition = 0;
        try {
            Timeline timeline = mExoPlayer.getCurrentTimeline();
            int currentWindowIndex = mExoPlayer.getCurrentWindowIndex();
            currentPosition = mExoPlayer.getCurrentPosition();
            Timeline.Window tmpWindow = new Timeline.Window();
            if (timeline != null) {
                for (int i = 0; i < timeline.getWindowCount(); i++) {
                    MyLog.d(TAG, "getCurrentDurationUpdated: window: " + i);
                    long windowDuration = timeline.getWindow(i, tmpWindow).getDurationMs();
                    //  totalTime += mExoPlayer.getCurrentPosition()+windowDuration;
                    if (i < currentWindowIndex) {
                        currentPosition += windowDuration;
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "getCurrentDurationUpdated: ", e);
        }
        return currentPosition;
    }

    @Override
    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
        MyLog.d(TAG, "HLS onLoadStarted: ");
    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        MyLog.d(TAG, "HLS onLoadCompleted: ");
    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
        MyLog.d(TAG, "HLS onLoadCanceled: ");
    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {
        MyLog.d(TAG, "HLS onLoadError: ");
    }

    @Override
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
        MyLog.d(TAG, "HLS onUpstreamDiscarded: ");
    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {
        MyLog.d(TAG, "HLS onDownstreamFormatChanged: ");
    }

    @Override
    public void onLoadError(IOException error) {
        MyLog.d(TAG, "HLS onLoadError: ");
    }
}
