package com.nd.android.common.widget.recorder.library.player;

import android.media.MediaPlayer;

/**
 * 播放操作回调
 *
 * @author Young
 */
public interface AudioRecordPlayerCallback {
    /**
     * 初始化操作
     *
     * @param pMediaPlayer the p media player
     * @author Young
     */
    void onInitPlayer(MediaPlayer pMediaPlayer);

    /**
     * 开始播放操作
     *
     * @param pMediaPlayer
     */
    void onStartPlayer(MediaPlayer pMediaPlayer);

    /**
     * 停止操作
     *
     * @param pMediaPlayer the media player
     * @author Young
     */
    void onStopPlayer(MediaPlayer pMediaPlayer);
}