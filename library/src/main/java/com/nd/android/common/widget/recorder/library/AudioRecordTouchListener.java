package com.nd.android.common.widget.recorder.library;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Observer;
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

    private PublishSubject<Object> mMinSubject;
    private PublishSubject<Object> mRecordSubject;
    private Observable<Long> mVolumeChangeSubject;

    private AudioRecordConfig mAudioRecordConfig;

    private MediaRecorder mRecorder;
    private Subscription mMinSubscription;
    private Subscription mRecordSubscription;
    private Subscription mVolumeChangeSubscription;
    private Subscription mTimeSubscription;
    private String mRecordPath;

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
                // 最短观察者
                mMinSubject = PublishSubject.create();
                mMinSubscription = mMinSubject
                        .take(mAudioRecordConfig.getMinRecordTime(), TimeUnit.MILLISECONDS, Schedulers.computation())    // 不响应最小时间后面的onNext用以判断
                        .subscribe(mMinSubscriber);

                // 音量变化
                mVolumeChangeSubject = Observable.interval(mAudioRecordConfig.getVolumeChangeDuration(), TimeUnit.MILLISECONDS, Schedulers.computation());
                mVolumeChangeSubscription = mVolumeChangeSubject
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mVolumeChangeSubscriber);

                // 时间变化
                mTimeSubscription = Observable.interval(1000, TimeUnit.MILLISECONDS, Schedulers.computation())
                        .map(new Func1<Long, Long>() {
                            long currentTime = 0;

                            @Override
                            public Long call(Long t1) {
                                return ++currentTime;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long t1) {
                                if (t1 > mAudioRecordConfig.getMaxRecordTime() / 1000) {
                                    mVolumeChangeSubscription.unsubscribe();
                                    // 超时异常
                                    mRecordSubject.onError(new TimeoutException(mContext.getString(R.string.audio_record_too_long)));
                                } else {
                                    mAudioRecordCallback.updateTime(t1, mAudioRecordConfig.getMaxRecordTime() / 1000);
                                }
                            }
                        });

                // 录音操作观察者
                mRecordSubject = PublishSubject.create();
                mRecordSubscription = mRecordSubject
                        .observeOn(Schedulers.immediate())
                        .subscribe(mRecordSubsriber);
                mRecordSubject.onNext(null);
                mAudioRecordCallback.updateTime(0, mAudioRecordConfig.getMaxRecordTime() / 1000);
                break;
            case MotionEvent.ACTION_MOVE:
                y = (int) event.getY();
                if (y < -100) {
                    // 切换图像
                    mAudioRecordCallback.tryToCancelRecord();
                } else {
                    mAudioRecordCallback.normalRecord();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                y = (int) event.getY();
                if (y < -100) {
                    mRecordSubject.onError(new Throwable(mContext.getString(R.string.audio_record_oper_cancel)));
                }
                mMinSubject.onNext(null);

                mMinSubscription.unsubscribe();
                mVolumeChangeSubscription.unsubscribe();
                mRecordSubject.onCompleted();
                mTimeSubscription.unsubscribe();
                mRecordSubscription.unsubscribe();
                break;
        }
        return true;
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

    private final Observer<? super Object> mRecordSubsriber = new Subscriber<Object>() {
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
            Log.e("AudioRecord", e.getMessage());
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
            initRecorder();
        }
    };

    private Observer<? super Object> mMinSubscriber = new Subscriber<Object>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Log.e("AudioRecord", e.getMessage());
        }

        @Override
        public void onNext(Object t) {
            mRecordSubject.onError(new Throwable(mContext.getString(R.string.audio_record_too_short)));
        }
    };

    private Observer<? super Object> mVolumeChangeSubscriber = new Subscriber<Object>() {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Log.e("AudioRecord", e.getMessage());
        }

        @Override
        public void onNext(Object t) {
            int volume = mRecorder.getMaxAmplitude();
            mAudioRecordCallback.updateVolumeView(volume);
        }
    };
}
