package com.nd.android.common.widget.recorder.library;

import android.text.TextUtils;

/**
 * 录音管理器对外接口
 *
 * @author Young
 */
public class AudioRecordConfig {

    /**
     * 回调
     */
    private IAudioRecordCallback mCallback;
    /**
     * 音量变化周期
     */
    private long mVolumeChangeDuration;
    /**
     * 最长录音时间
     */
    private long mMaxRecordTime;
    /**
     * 最短录音时间
     */
    private long mMinRecordTime;
    /**
     * 录音路径
     */
    private IFileNameGenerator mRecordPathGenerator;

    public IAudioRecordCallback getCallback() {
        return mCallback;
    }

    public long getVolumeChangeDuration() {
        return mVolumeChangeDuration;
    }

    public long getMaxRecordTime() {
        return mMaxRecordTime;
    }

    public long getMinRecordTime() {
        return mMinRecordTime;
    }

    public IFileNameGenerator getRecordPathGenerator() {
        return mRecordPathGenerator;
    }

    public static class Builder {

        public AudioRecordConfig mConfig;

        public Builder() {
            mConfig = new AudioRecordConfig();
        }

        /**
         * 设置获取音量的周期
         *
         * @param duration 周期时长(MILLISECONDS)
         */
        public Builder setVolumeChangeDuration(long duration) {
            mConfig.mVolumeChangeDuration = duration;
            return this;
        }

        /**
         * 设置最长录音时长
         *
         * @param time 时长(MILLISECONDS)
         */
        public Builder setMaxRecordTime(long time) {
            mConfig.mMaxRecordTime = time;
            return this;
        }

        /**
         * 设置最短录音时长
         *
         * @param time 时长(MILLISECONDS)
         */
        public Builder setMinRecordTime(long time) {
            mConfig.mMinRecordTime = time;
            return this;
        }

        /**
         * 设置录音文件路径生成规则
         *
         * @param pFileNameGenerator 路径
         */
        public Builder setRecrodPathGenerator(IFileNameGenerator pFileNameGenerator) {
            mConfig.mRecordPathGenerator = pFileNameGenerator;
            return this;
        }

        /**
         * 设置回调
         *
         * @param pCallback 回调
         * @return the builder
         * @author Young
         */
        public Builder setCallback(IAudioRecordCallback pCallback) {
            mConfig.mCallback = pCallback;
            return this;
        }

        public AudioRecordConfig build() {
            if (mConfig.mMinRecordTime <= 0) {
                throw new IllegalStateException("Min Record Time Error");
            }
            if (mConfig.mMaxRecordTime <= 0) {
                throw new IllegalStateException("Min Record Time Error");
            }
            if (mConfig.mMaxRecordTime <= mConfig.mMinRecordTime) {
                throw new IllegalStateException("Min Record Time Must Greater Than Max Record Time");
            }
            if (mConfig.mRecordPathGenerator == null) {
                throw new IllegalStateException("FileGenerator Set To Null,You can use UUIDFileNameGenerator");
            }
            if (TextUtils.isEmpty(mConfig.mRecordPathGenerator.getFileName())) {
                throw new IllegalStateException("Record Path Error");
            }
            if (mConfig.mCallback == null) {
                throw new IllegalStateException("Not Set Callback");
            }
            if (mConfig.mVolumeChangeDuration <= 0) {
                mConfig.mVolumeChangeDuration = 1000;
            }
            return mConfig;
        }
    }
}
