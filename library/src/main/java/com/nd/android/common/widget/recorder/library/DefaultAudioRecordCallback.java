package com.nd.android.common.widget.recorder.library;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.TimeoutException;

/**
 * 默认回调
 *
 * @author Young
 */
public class DefaultAudioRecordCallback implements IAudioRecordCallback {

    private final Context mContext;
    private View mParentView;
    private AudioRecordPopWindow mPopWindow;

    /**
     * Instantiates a new Default audio record callback.
     *
     * @param pParentView the p parent view
     * @param pContext    the p context
     */
    public DefaultAudioRecordCallback(View pParentView, Context pContext) {
        mParentView = pParentView;
        mContext = pContext;
    }

    /**
     * Instantiates a new Default audio record callback.
     *
     * @param pDialog the dialog
     */
    public DefaultAudioRecordCallback(Dialog pDialog) {
        mContext = pDialog.getContext();
        mParentView = pDialog.getWindow().getDecorView();
    }


    /**
     * Instantiates a new Default audio record callback.
     *
     * @param pActivity the activity
     */
    public DefaultAudioRecordCallback(Activity pActivity) {
        mContext = pActivity;
        mParentView = pActivity.getWindow().getDecorView();
    }

    /**
     * 获取Activity
     *
     * @return the audio record pop window
     * @author Young
     */
    public AudioRecordPopWindow getPopupWindow() {
        return mPopWindow;
    }

    @Override
    public void startRecord() {
        mPopWindow = new AudioRecordPopWindow(mContext);
        mPopWindow.showAtLocation(mParentView, Gravity.TOP, 0, 0);
    }

    @Override
    public void recordError(Throwable t) {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.patchDismiss();
        }
        if (t instanceof RecordException) {
            Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, R.string.audio_record_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void recordSuccess(String pRecordPath) {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.patchDismiss();
        }
    }

    @Override
    public void tryToCancelRecord() {
        if (mPopWindow != null) {
            mPopWindow.switchState(AudioRecordPopWindow.State.canceling);
        }
    }

    @Override
    public void normalRecord() {
        if (mPopWindow != null) {
            mPopWindow.switchState(AudioRecordPopWindow.State.recording);
        }
    }

    @Override
    public void updateVolumeView(double volume) {
        if (mPopWindow != null) {
            mPopWindow.updateVolume(volume);
        }
    }

    @Override
    public void updateTime(long now, long total) {
        if (mPopWindow != null) {
            mPopWindow.updateTime(String.format("%d/%d''", now, total));
        }
    }

    @Override
    public void recordTooLong(String pRecordPath, TimeoutException pException) {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.patchDismiss();
        }
        Toast.makeText(mContext, pException.getMessage(), Toast.LENGTH_LONG).show();
    }

}
