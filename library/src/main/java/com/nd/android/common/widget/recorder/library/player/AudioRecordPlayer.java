package com.nd.android.common.widget.recorder.library.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.nd.android.common.widget.recorder.library.R;

import java.io.File;

import rx.Observable;
import rx.Subscriber;

/**
 * 录音文件播放器
 *
 * @author Young
 * @date 2015年4月21日 15:03:11
 */
public class AudioRecordPlayer {

    private static final String TAG = "AudioRecordPlayer";

    private static ExtendMediaPlayer mMediaPlayer;

    /**
     * 播放声音
     *
     * @return 是否播放成功 boolean
     * @throws IllegalStateException the illegal state exception
     * @author Young
     */
    public static boolean play(Context pContext, AudioRecordPlayerConfig pConfig) throws IllegalStateException {
        if (pConfig.getFilePath() == null) {
            Log.e(TAG, "Play Error:Null Record File");
            throw new IllegalStateException(pContext.getString(R.string.audio_record_file_null));
        }
        final File file = new File(pConfig.getFilePath());
        if (!file.exists()) {
            throw new IllegalStateException(pContext.getString(R.string.audio_record_file_not_exist));
        }
        final AudioRecordPlayerCallback playerCallback = pConfig.getAudioRecordPlayerCallback();
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }

            Uri uri = Uri.fromFile(file);
            mMediaPlayer = new ExtendMediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            mMediaPlayer.setDataSource(pContext, uri);
            mMediaPlayer.setRecordPlayerCallback(playerCallback);
            if (playerCallback != null) {
                playerCallback.onInitPlayer(mMediaPlayer);
            }
        } catch (Exception e) {
            playerCallback.onStopPlayer(mMediaPlayer);
        }

        Observable<Pair<Integer, Integer>> observable = RxMediaPlayer.play(mMediaPlayer);
        playerCallback.onStartPlayer(mMediaPlayer);
        observable.subscribe(new Subscriber<Pair<Integer, Integer>>() {
            @Override
            public void onCompleted() {
                mMediaPlayer = null;
                playerCallback.onStopPlayer(mMediaPlayer);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
                playerCallback.onStopPlayer(mMediaPlayer);
            }

            @Override
            public void onNext(Pair<Integer, Integer> t) {
            }
        });
        return true;
    }

    /**
     * 停止播放
     *
     * @return the boolean
     * @author Young
     */
    public static boolean stop() {
        RxMediaPlayer.stop(mMediaPlayer);
        return true;
    }

}
