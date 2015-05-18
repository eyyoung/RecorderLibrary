package com.nd.android.common.widget.recorder;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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


public class MainActivity extends ActionBarActivity {

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
                    public void recordSuccess(String pRecordPath) {
                        super.recordSuccess(pRecordPath);
                        Toast.makeText(MainActivity.this, "录音成功", Toast.LENGTH_LONG).show();
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
                    public void onStopPlayer(MediaPlayer pMediaPlayer) {
                        super.onStopPlayer(pMediaPlayer);
                        final AnimationDrawable drawable = (AnimationDrawable) mIvState.getDrawable();
                        if (drawable != null) {
                            drawable.stop();
                        }
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
                        Toast.makeText(MainActivity.this, "录音成功", Toast.LENGTH_LONG).show();
                    }
                })
                .build();
        dialog.findViewById(R.id.btnDlgRecord)
                .setOnTouchListener(AudioRecordManager.getTouchListener(audioRecordConfig));
        dialog.show();
    }
}
