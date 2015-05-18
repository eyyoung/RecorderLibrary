package com.nd.android.common.widget.recorder.library.player;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

class RxMediaPlayer {

    public static Subscriber<? super ExtendMediaPlayer> mSubscriber;

    static
    @NonNull
    Observable<ExtendMediaPlayer> prepare(@NonNull final ExtendMediaPlayer mp) {
        return Observable.create(new Observable.OnSubscribe<ExtendMediaPlayer>() {
            @Override
            public void call(final Subscriber<? super ExtendMediaPlayer> subscriber) {
                try {
                    mp.prepare();
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            subscriber.onNext((ExtendMediaPlayer) mp);
                            subscriber.onCompleted();
                        }
                    });
                } catch (IOException e) {
                    mp.reset();
                    mp.release();
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 播放流
     *
     * @param mp the mp
     * @return the observable
     * @author Young
     */
    public static
    @NonNull
    Observable<Pair<Integer, Integer>> play(@NonNull final ExtendMediaPlayer mp) {
        return prepare(mp).flatMap(new Func1<ExtendMediaPlayer, Observable<Pair<Integer, Integer>>>() {
            @Override
            public Observable<Pair<Integer, Integer>> call(ExtendMediaPlayer t1) {
                return stream(mp);
            }
        });
    }

    static
    @NonNull
    Observable<Pair<Integer, Integer>> stream(@NonNull final ExtendMediaPlayer mp) {
        return Observable.create(new Observable.OnSubscribe<Pair<Integer, Integer>>() {
            @Override
            public void call(Subscriber<? super Pair<Integer, Integer>> subscriber) {
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        if (mp.isPlaying()) {
                            mp.stop();
                        }
                        mp.reset();
                        mp.release();
                    }
                }));
                mp.start();
                subscriber.add(ticks(mp)
                        .takeUntil(complete(mp))
                        .subscribe(subscriber));
            }
        });
    }

    static
    @NonNull
    Observable<Pair<Integer, Integer>> ticks(@NonNull final ExtendMediaPlayer mp) {
        return Observable.interval(16, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Pair<Integer, Integer>>() {
                    @Override
                    public Pair<Integer, Integer> call(Long t1) {
                        return Pair.create(mp.getCurrentPosition(), mp.getDuration());
                    }
                });
    }

    static
    @NonNull
    Observable<ExtendMediaPlayer> complete(@NonNull final ExtendMediaPlayer player) {
        return Observable.create(new Observable.OnSubscribe<ExtendMediaPlayer>() {
            @Override
            public void call(final Subscriber<? super ExtendMediaPlayer> subscriber) {
                mSubscriber = subscriber;
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        subscriber.onNext(player);
                        subscriber.onCompleted();
                    }
                });
                player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        subscriber.onNext(player);
                        subscriber.onCompleted();
                        return true;
                    }
                });
            }
        });
    }

    public static void stop(final ExtendMediaPlayer pExtendMediaPlayer) {
        try {
            if (pExtendMediaPlayer != null) {
                pExtendMediaPlayer.stop();
            }
        } catch (Exception e) {

        }
        if (mSubscriber != null) {
            mSubscriber.onNext(pExtendMediaPlayer);
            mSubscriber.onCompleted();
            mSubscriber.unsubscribe();
            mSubscriber = null;
        }
    }

}