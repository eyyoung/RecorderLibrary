package com.nd.android.common.widget.recorder.library;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * 录音监听器
 *
 * @author Young
 */
class AudioRecordTouchListener implements View.OnTouchListener {

    private Context mContext;

    private IAudioRecordCallback mAudioRecordCallback;

    private PublishSubject<Object> mRecordSubject;
    private Observable<Long> mVolumeChangeSubject;

    private AudioRecordConfig mAudioRecordConfig;

    private MediaRecorder mRecorder;
    private Subscription mRecordSubscription;
    private Subscription mVolumeChangeSubscription;
    private Subscription mTimeSubscription;
    private String mRecordPath;
    private boolean mIsTryToCancel;

    public AudioRecordTouchListener(AudioRecordConfig pAudioRecordConfig) {
        mAudioRecordConfig = pAudioRecordConfig;
        mAudioRecordCallback = pAudioRecordConfig.getCallback();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mContext = v.getContext();

                // 录音操作观察者
                mRecordSubject = PublishSubject.create();
                mRecordSubscription = mRecordSubject
                        .observeOn(Schedulers.immediate())
                        .subscribe(new RecorderSubscriber());
                mRecordSubject.onNext(null);
                mAudioRecordCallback.updateTime(0, mAudioRecordConfig.getMaxRecordTime() / 1000);
                break;
            case MotionEvent.ACTION_MOVE:
                y = (int) event.getY();
                if (y < -100) {
                    // 切换图像
                    mAudioRecordCallback.tryToCancelRecord();
                    mIsTryToCancel = true;
                } else {
                    // 避免频繁调用
                    if (mIsTryToCancel) {
                        mAudioRecordCallback.normalRecord();
                        mIsTryToCancel = false;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                y = (int) event.getY();
                if (y < -100) {
                    mRecordSubject.onError(new RecordException(mContext.getString(R.string.audio_record_oper_cancel)));
                }

                long duration = 0;
                duration = getDuration();
                if (duration < mAudioRecordConfig.getMinRecordTime()) {
                    mRecordSubject.onError(new RecordException(mContext.getString(R.string.audio_record_too_short)));
                }

                if (mVolumeChangeSubject != null) {
                    mVolumeChangeSubscription.unsubscribe();
                }
                mRecordSubject.onCompleted();
                if (mTimeSubscription != null) {
                    mTimeSubscription.unsubscribe();
                }
                mRecordSubscription.unsubscribe();
                break;
        }
        return true;
    }

    private long getDuration() {
        long duration = 0;
        MediaPlayer durationPlayer = new MediaPlayer();
        durationPlayer.reset();
        try {
            durationPlayer.setDataSource(mRecordPath);
            durationPlayer.prepare();
            duration = durationPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            durationPlayer.release();
        }
        return duration;
    }

    private void initRecorder() {
        try {
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            }

            mRecordPath = mAudioRecordConfig.getRecordPathGenerator().getFileName();
            final File file = new File(mRecordPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            mRecorder.setOutputFile(mRecordPath);
            mRecorder.prepare();
            mRecorder.start();
            mAudioRecordCallback.startRecord();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class RecorderSubscriber extends Subscriber<Object> {
        @Override
        public void onCompleted() {
            try {
                if (mRecorder != null) {
                    mRecorder.stop();
                    mRecorder.reset();
                    mRecorder.release();
                    mRecorder = null;
                }
                mAudioRecordCallback.recordSuccess(mRecordPath);
            } catch (IllegalStateException e) {
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            if (mTimeSubscription != null) {
                mTimeSubscription.unsubscribe();
            }
            try {
                if (mRecorder != null) {
                    mRecorder.stop();
                    mRecorder.reset();
                    mRecorder.release();
                    mRecorder = null;
                }
            } catch (IllegalStateException e2) {
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
            if (e instanceof TimeoutException) {
                mAudioRecordCallback.recordTooLong(mRecordPath, (TimeoutException) e);
            } else {
                final File file = new File(mRecordPath);
                file.delete();
                mAudioRecordCallback.recordError(e);
            }
        }

        @Override
        public void onNext(Object t) {
            // 时间变化
            mTimeSubscription = Observable.interval(500, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .map(new Func1<Long, Long>() {
                        @Override
                        public Long call(Long t1) {
                            long duration = getDuration();
                            return duration;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long t1) {
                            if (getDuration() > (mAudioRecordConfig.getMaxRecordTime())) {
                                mVolumeChangeSubscription.unsubscribe();
                                // 超时异常
                                mRecordSubject.onError(new TimeoutException(mContext.getString(R.string.audio_record_too_long)));
                            } else {
                                mAudioRecordCallback.updateTime(floarDuration(t1), mAudioRecordConfig.getMaxRecordTime() / 1000);
                            }
                        }
                    });

            initRecorder();
            // 音量变化
            mVolumeChangeSubject = Observable.interval(mAudioRecordConfig.getVolumeChangeDuration(), TimeUnit.MILLISECONDS, Schedulers.computation());
            mVolumeChangeSubscription = mVolumeChangeSubject
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new VolumeChangeSubscriber());
        }
    }

    private class VolumeChangeSubscriber extends Subscriber<Object> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Object t) {
            int volume = mRecorder.getMaxAmplitude();
            mAudioRecordCallback.updateVolumeView(volume);
        }
    }

    ;

    private static long floarDuration(long duration) {
        return (int) Math.floor(duration / 1000);
    }
}
