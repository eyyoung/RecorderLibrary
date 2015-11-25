package com.nd.android.common.widget.recorder;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.nd.android.common.widget.recorder.library.AudioRecordConfig;
import com.nd.android.common.widget.recorder.library.AudioRecordManager;
import com.nd.android.common.widget.recorder.library.DefaultAudioRecordCallback;
import com.nd.android.common.widget.recorder.library.UUIDFileNameGenerator;
import com.nd.android.common.widget.recorder.library.player.AudioRecordPlayerConfig;
import com.nd.android.common.widget.recorder.library.player.SensorPlayerCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class MainActivity extends AppCompatActivity {

    private ImageView mIvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIvState = (ImageView) findViewById(R.id.ivState);

        initRecordButton();
    }

    private void initRecordButton() {
        AudioRecordConfig audioRecordConfig = new AudioRecordConfig.Builder()
                .setMaxRecordTime(5 * 1000)
                .setMinRecordTime(1000)
                .setRecrodPathGenerator(new UUIDFileNameGenerator("/sdcard"))
                .setVolumeChangeDuration(300)
                .setCallback(new DefaultAudioRecordCallback(this) {

                    @Override
                    public void tryToCancelRecord() {
                        super.tryToCancelRecord();
                        Log.d("MainActivity", "try");
                    }

                    @Override
                    public void recordError(Throwable t) {
                        super.recordError(t);
                        Log.d("MainActivity", "recordError");
                    }

                    @Override
                    public void startRecord() {
                        super.startRecord();
                        Log.d("MainActivity", "startRecord");
                    }

                    @Override
                    public void normalRecord() {
                        super.normalRecord();
                        Log.d("MainActivity", "normalRecord");
                    }

                    @Override
                    public void recordSuccess(String pRecordPath) {
                        super.recordSuccess(pRecordPath);
                        Log.d("MainActivity", "recordSuccess");
                        MediaPlayer mDurationPlayer = new MediaPlayer();
                        mDurationPlayer.reset();
                        try {
                            mDurationPlayer.setDataSource(pRecordPath);
                            mDurationPlayer.prepare();
                            Toast.makeText(MainActivity.this, "录音成功" + mDurationPlayer.getDuration(), Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            mDurationPlayer.release();
                        }
                    }

                    @Override
                    public void recordTooLong(String pRecordPath, TimeoutException pException) {
                        super.recordTooLong(pRecordPath, pException);
                        Log.d("MainActivity", "recordTooLong");
                        MediaPlayer mDurationPlayer = new MediaPlayer();
                        mDurationPlayer.reset();
                        try {
                            mDurationPlayer.setDataSource(pRecordPath);
                            mDurationPlayer.prepare();
                            Toast.makeText(MainActivity.this, "录音太长" + mDurationPlayer.getDuration(), Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            mDurationPlayer.release();
                        }
                    }
                })
                .build();
        findViewById(R.id.btn)
                .setOnTouchListener(AudioRecordManager.getTouchListener(audioRecordConfig));
    }

    public void play(View view) {
        AudioRecordPlayerConfig playerConfig = new AudioRecordPlayerConfig.Builder(MainActivity.this)
                .setAudioRecordPlayerCallback(new SensorPlayerCallback(MainActivity.this) {
                    @Override
                    public void onStopPlayer() {
                        super.onStopPlayer();
                        final AnimationDrawable drawable = (AnimationDrawable) mIvState.getDrawable();
                        if (drawable != null) {
                            drawable.stop();
                        }
                    }

                    @Override
                    public void onPlayComplete() {
                        super.onPlayComplete();
                        final AnimationDrawable drawable = (AnimationDrawable) mIvState.getDrawable();
                        if (drawable != null) {
                            drawable.stop();
                        }
                        Log.e("TEST", "COMPLETE");
                    }
                })
                .setFilePath("/sdcard/test.amr")
                .build();
        AudioRecordManager.play(MainActivity.this, playerConfig);
        // 开始播放动画
        mIvState.setImageResource(R.drawable.audio_record_default_play);
        final AnimationDrawable drawable = (AnimationDrawable) mIvState.getDrawable();
        drawable.start();
    }

    public void stop(View view) {
        AudioRecordManager.stopPlayer();
        final AnimationDrawable drawable = (AnimationDrawable) mIvState.getDrawable();
        if (drawable != null) {
            drawable.stop();
        }
    }

    public void dlg(View view) {
        final Dialog dialog = new Dialog(this);
        dialog
                .setContentView(R.layout.dlg);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        AudioRecordConfig audioRecordConfig = new AudioRecordConfig.Builder()
                .setMaxRecordTime(120 * 1000)
                .setMinRecordTime(1000)
                .setRecrodPathGenerator(new UUIDFileNameGenerator("/sdcard"))
                .setVolumeChangeDuration(300)
                .setCallback(new DefaultAudioRecordCallback(dialog) {
                    @Override
                    public void recordSuccess(String pRecordPath) {
                        super.recordSuccess(pRecordPath);
                        MediaPlayer mDurationPlayer = new MediaPlayer();
                        mDurationPlayer.reset();
                        try {
                            mDurationPlayer.setDataSource(pRecordPath);
                            mDurationPlayer.prepare();
                            Toast.makeText(MainActivity.this, "录音成功" + mDurationPlayer.getDuration(), Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            mDurationPlayer.release();
                        }
                    }
                })
                .build();
        dialog.findViewById(R.id.btnDlgRecord)
                .setOnTouchListener(AudioRecordManager.getTouchListener(audioRecordConfig));
        dialog.show();
    }

    public void outSideClick(View view) {
        Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
    }
}
