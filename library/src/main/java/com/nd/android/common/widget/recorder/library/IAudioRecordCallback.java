package com.nd.android.common.widget.recorder.library;

import java.util.concurrent.TimeoutException;

/**
 * 录音回调
 *
 * @author Young
 */
public interface IAudioRecordCallback {

    /**
     * 开始录音
     *
     * @author Young
     */
    void startRecord();

    /**
     * 录音异常
     *
     * @param t the t
     * @author Young
     */
    void recordError(Throwable t);

    /**
     * 录音成功结束
     *
     * @author Young
     * @param pRecordPath
     */
    void recordSuccess(String pRecordPath);

    /**
     * 发生取消事件
     *
     * @author Young
     */
    void tryToCancelRecord();

    /**
     * 照常录音
     *
     * @author Young
     */
    void normalRecord();

    /**
     * 更新声音View
     *
     * @param volume the volume
     * @author Young
     */
    void updateVolumeView(double volume);

    /**
     * 更新录音时间
     *
     * @param now   当前时长
     * @param total 允许总时长
     * @author Young
     */
    void updateTime(long now, long total);

    /**
     * 超时异常
     *
     *
     * @param pRecordPath
     * @param pThrowable
     * @author Young
     */
    void recordTooLong(String pRecordPath, TimeoutException pThrowable);
}
