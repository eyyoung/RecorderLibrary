package com.nd.android.common.widget.recorder.library;

import android.content.Context;

import com.nd.android.common.widget.recorder.library.player.AudioRecordPlayer;
import com.nd.android.common.widget.recorder.library.player.AudioRecordPlayerConfig;

/**
 * Created by Young on 2015/4/17.
 */
public class AudioRecordManager {

    /**
     * 获取触摸事件
     *
     * @return the touch listener
     */
    public static AudioRecordTouchListener getTouchListener(AudioRecordConfig pAudioRecordConfig) {
        return new AudioRecordTouchListener(pAudioRecordConfig);
    }

    /**
     * 播放
     *
     * @param pContext
     * @param pAudioRecordPlayerConfig
     * @author Young
     */
    public static void play(Context pContext, AudioRecordPlayerConfig pAudioRecordPlayerConfig) {
        AudioRecordPlayer.play(pContext, pAudioRecordPlayerConfig);
    }

    /**
     * 停止所有正在播放的播放器
     *
     * @author Young
     */
    public static void stopPlayer() {
        AudioRecordPlayer.stop();
    }

}
