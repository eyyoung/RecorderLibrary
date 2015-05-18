package com.nd.android.common.widget.recorder.library.player;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * 播放配置
 */
public class AudioRecordPlayerConfig implements Serializable{

    /**
     * 文件路径
     */
    private String mFilePath;
    /**
     * 播放回调
     */
    private AudioRecordPlayerCallback mAudioRecordPlayerCallback;

    public String getFilePath() {
        return mFilePath;
    }

    public AudioRecordPlayerCallback getAudioRecordPlayerCallback() {
        return mAudioRecordPlayerCallback;
    }

    public static class Builder {
        private AudioRecordPlayerConfig mConfig;
        private Context mContext;

        public Builder(Context pContext) {
            mContext = pContext;
            mConfig = new AudioRecordPlayerConfig();
        }

        public Builder setAudioRecordPlayerCallback(AudioRecordPlayerCallback pAudioRecordPlayerCallback) {
            mConfig.mAudioRecordPlayerCallback = pAudioRecordPlayerCallback;
            return this;
        }

        public AudioRecordPlayerConfig build() {
            if (TextUtils.isEmpty(mConfig.mFilePath)) {
                throw new IllegalStateException("File Path Error");
            }
            if (mConfig.mAudioRecordPlayerCallback == null) {
                mConfig.mAudioRecordPlayerCallback = new SensorPlayerCallback(mContext);
            }
            return mConfig;
        }

        public Builder setFilePath(String pFilePath) {
            mConfig.mFilePath = pFilePath;
            return this;
        }

    }
}
