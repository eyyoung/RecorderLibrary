package com.nd.android.common.widget.recorder.library.player;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.nd.android.common.widget.recorder.library.R;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * 录音文件播放器
 *
 * @author Young
 * @date 2015年4月21日 15:03:11
 */
public class AudioRecordPlayer {

    private static final String TAG = "AudioRecordPlayer";

    private static ExtendMediaPlayer mMediaPlayer;
    private static Subscription sSubscription;

    /**
     * 播放声音
     *
     * @return 是否播放成功 boolean
     * @throws IllegalStateException the illegal state exception
     * @author Young
     */
    public static boolean play(final Context pContext, AudioRecordPlayerConfig pConfig) throws IllegalStateException {
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
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
            }
            if (sSubscription != null) {
                sSubscription.unsubscribe();
            }

            Uri uri = Uri.fromFile(file);
            mMediaPlayer = new ExtendMediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(pContext, uri);
            mMediaPlayer.setRecordPlayerCallback(playerCallback);
            if (playerCallback != null) {
                playerCallback.onInitPlayer(mMediaPlayer);
            }
        } catch (Exception e) {
            playerCallback.onStopPlayer();
        }

        Observable<Pair<Integer, Integer>> observable = RxMediaPlayer.play(mMediaPlayer);
        playerCallback.onStartPlayer(mMediaPlayer);
        sSubscription = observable.subscribe(new Subscriber<Pair<Integer, Integer>>() {
            @Override
            public void onCompleted() {
                mMediaPlayer = null;
                playerCallback.onPlayComplete();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, e.getMessage());
                playerCallback.onStopPlayer();
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
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (sSubscription != null) {
            sSubscription.unsubscribe();
        }
        return true;
    }

}
