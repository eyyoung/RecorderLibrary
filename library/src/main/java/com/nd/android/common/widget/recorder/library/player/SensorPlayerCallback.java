package com.nd.android.common.widget.recorder.library.player;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

/**
 * 感应器播放回调
 */
public class SensorPlayerCallback implements AudioRecordPlayerCallback {

    public static final String TAG = "SensorPlayerCallback";

    private final Context mContext;
    private AudioManager mAudioManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean mOnlyHeadset;

    public SensorPlayerCallback(Context pContext) {
        mContext = pContext;
        mAudioManager = (AudioManager) pContext.getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) pContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onInitPlayer(MediaPlayer pMediaPlayer) {
        mAudioManager.setSpeakerphoneOn(true);
        if (!mOnlyHeadset) {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        } else {
            setHeadsetMode();
        }
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStartPlayer(MediaPlayer pMediaPlayer) {

    }

    @Override
    public void onStopPlayer() {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    public void onPlayComplete() {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "onSensorChanged");
            float range = event.values[0];
            Log.d(TAG, "range:" + range);
            if (range >= 5 && !mOnlyHeadset) {
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mAudioManager.setSpeakerphoneOn(true);
                if (mContext instanceof Activity) {
                    ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);
                }
            } else {
                setHeadsetMode();
                mAudioManager.setSpeakerphoneOn(false);
                if (mContext instanceof Activity) {
                    ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void setHeadsetMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            mAudioManager.setMode(AudioManager.MODE_IN_CALL);
        }
    }

    public void setHeadsetMode(boolean onlyHeadset) {
        mOnlyHeadset = onlyHeadset;
    }

}
