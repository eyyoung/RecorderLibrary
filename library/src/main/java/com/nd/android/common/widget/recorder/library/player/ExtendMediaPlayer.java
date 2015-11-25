package com.nd.android.common.widget.recorder.library.player;

import android.media.MediaPlayer;

/**
 * MediaPlayer扩展
 */
public class ExtendMediaPlayer extends MediaPlayer {

    public AudioRecordPlayerCallback getRecordPlayerCallback() {
        return mRecordPlayerCallback;
    }

    public void setRecordPlayerCallback(AudioRecordPlayerCallback pRecordPlayerCallback) {
        mRecordPlayerCallback = pRecordPlayerCallback;
    }

    public AudioRecordPlayerCallback mRecordPlayerCallback;

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mRecordPlayerCallback.onStopPlayer();
    }

    @Override
    public void release() {
        super.release();
        mRecordPlayerCallback = null;
    }
}
