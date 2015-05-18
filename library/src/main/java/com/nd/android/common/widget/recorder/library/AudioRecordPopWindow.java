package com.nd.android.common.widget.recorder.library;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 弹出窗口
 *
 * @author Young
 */
public class AudioRecordPopWindow extends PopupWindow {

    private final View mLlMobileHint;
    private final View mIvCancel;
    private final View mTvMoveFingerHint;
    private final View mTvReleaseHint;
    private final VolumeView mVolumeView;
    private final TextView mTvTimeRemaining;

    /**
     * 更新音量
     *
     * @param pVolume the p volume
     * @author Young
     */
    public void updateVolume(double pVolume) {
        mVolumeView.setVolume(pVolume);
    }

    /**
     * 更新时间
     *
     * @param pTime the p time
     * @author Young
     */
    public void updateTime(String pTime) {
        mTvTimeRemaining.setText(pTime);
    }

    /**
     * 状态
     *
     * @author $author
     * @date $date
     */
    public enum State {
        recording, //正在录音
        canceling // 准备取消
    }

    public AudioRecordPopWindow(Context pContext) {
        super(pContext);
        setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View view = LayoutInflater.from(pContext).inflate(R.layout.audio_record_popup, null);
        setContentView(view);
        mLlMobileHint = view.findViewById(R.id.llMobileHint);
        mIvCancel = view.findViewById(R.id.ivCancel);
        mVolumeView = (VolumeView) view.findViewById(R.id.volume_view);
        mTvTimeRemaining = (TextView) view.findViewById(R.id.tvRemaining);
        mTvMoveFingerHint = view.findViewById(R.id.tvMoveFingerHint);
        mTvReleaseHint = view.findViewById(R.id.tvReleaseHint);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        setOutsideTouchable(false);
    }

    /**
     * 切换状态
     *
     * @param pState the p state
     * @author Young
     */
    public void switchState(State pState) {
        switch (pState) {
            case recording:
                mLlMobileHint.setVisibility(View.VISIBLE);
                mTvMoveFingerHint.setVisibility(View.VISIBLE);
                mTvReleaseHint.setVisibility(View.INVISIBLE);
                mIvCancel.setVisibility(View.INVISIBLE);
                break;
            case canceling:
                mTvReleaseHint.setVisibility(View.VISIBLE);
                mIvCancel.setVisibility(View.VISIBLE);
                mLlMobileHint.setVisibility(View.INVISIBLE);
                mTvMoveFingerHint.setVisibility(View.INVISIBLE);
                break;
        }
    }

}
