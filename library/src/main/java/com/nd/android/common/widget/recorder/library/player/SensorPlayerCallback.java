package com.nd.android.common.widget.recorder.library.player;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

    public SensorPlayerCallback(Context pContext) {
        mContext = pContext;
        mAudioManager = (AudioManager) pContext.getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) pContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onInitPlayer(MediaPlayer pMediaPlayer) {
        mAudioManager.setSpeakerphoneOn(true);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStartPlayer(MediaPlayer pMediaPlayer) {

    }

    @Override
    public void onStopPlayer(MediaPlayer pMediaPlayer) {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.e(TAG, "onSensorChanged");
            float range = event.values[0];
            int mode;
            if (range >= mSensor.getMaximumRange()) {
                mode = AudioManager.MODE_NORMAL;
                mAudioManager.setSpeakerphoneOn(true);
                if (mContext instanceof Activity) {
                    ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);
                }
            } else {
                mode = AudioManager.MODE_IN_CALL;
                mAudioManager.setSpeakerphoneOn(false);
                if (mContext instanceof Activity) {
                    ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                }
            }
            mAudioManager.setMode(mode);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
