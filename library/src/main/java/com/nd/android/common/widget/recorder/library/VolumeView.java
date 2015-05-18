package com.nd.android.common.widget.recorder.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * 录音音量控件，显示当前录音音量大小
 *
 * @author zhengjh
 */
public class VolumeView extends View {
    private double mMaxVolume = 10000;
    private double mMinVolume = 0.0D;
    int mVolume = 0;

    ArrayList<Bitmap> list = new ArrayList<Bitmap>();
    Context mContext = null;

    public VolumeView(Context paramContext) {
        this(paramContext, null);
        init(paramContext);
    }

    public void init(Context paramContext) {
        mContext = paramContext;
        list.add(drawableToBitmap(R.drawable.audio_record_volume_1));
        list.add(drawableToBitmap(R.drawable.audio_record_volume_2));
        list.add(drawableToBitmap(R.drawable.audio_record_volume_3));
        list.add(drawableToBitmap(R.drawable.audio_record_volume_4));
        list.add(drawableToBitmap(R.drawable.audio_record_volume_5));
        list.add(drawableToBitmap(R.drawable.audio_record_volume_6));
        list.add(drawableToBitmap(R.drawable.audio_record_volume_7));
    }

    public VolumeView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);
    }

    public Bitmap drawableToBitmap(int id) {
        try {
            return BitmapFactory.decodeResource(mContext.getResources(), id);
        } catch (RuntimeException e) {
            System.gc();
        }
        return null;
    }

    int marginTop = 0;
    int marginLeft = 0;

    public void onDraw(Canvas paramCanvas) {
        if (mVolume >= 6) {
            mVolume = 6;
        }
        if (mVolume == -1) {

        } else {
            marginTop = 153 - mVolume * 23 - 15;
            paramCanvas.drawBitmap(list.get(mVolume), 0, marginTop, null);
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        mVolume = 0;
        invalidate();
    }

    /**
     * 设置音量
     *
     * @param volume 不限大小
     */
    public void setVolume(double volume) {
        // 低于最小值设该值为最小值
        if (volume < mMinVolume)
            mMinVolume = volume;
        // 高于最大值设该值为最大值
        if (volume > mMaxVolume)
            mMaxVolume = volume;
        // 如果最大值比最小值大时
        if (mMaxVolume - mMinVolume > 0) {
            // 当前值始终在0~1之间
            mVolume = (int) ((volume - mMinVolume) / (mMaxVolume - mMinVolume) * 6);
        } else {
            mVolume = 6;
        }
        // 请求刷新视图
        invalidate();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.GONE) {
            for (Bitmap bitmap : list) {
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
    }
}