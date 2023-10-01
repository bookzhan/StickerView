package com.xiaopo.flying.sticker;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * @author wupanjie
 */
public class DrawableSticker extends Sticker {

    private volatile Drawable drawable;
    private volatile Rect realBounds;

    public DrawableSticker() {

    }

    public DrawableSticker(Drawable drawable) {
        this.drawable = drawable;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public DrawableSticker setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public Rect getRealBounds() {
        return realBounds;
    }

    public void setRealBounds(Rect realBounds) {
        this.realBounds = realBounds;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        drawable.setBounds(realBounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public void drawByTime(@NonNull Canvas canvas, long currentTime) {
        //没有设置时间,或者时间设置无效
        if (!startEndTimeAvailable()) {
            draw(canvas);
            return;
        }
        //currentTime不在绘制的区间内,不绘制,留白
        if (!stickerEnable(currentTime)) {
            return;
        }
        long time = currentTime - getStartTime();
        if (time <= mGradientTime && isEnableGradientStart()) {
            drawable.setAlpha((int) (time / mGradientTime * 255));
        } else if (getEndTime() - currentTime <= mGradientTime && isEnableGradientEnd()) {
            drawable.setAlpha((int) ((getEndTime() - currentTime) / mGradientTime * 255));
        } else {
            drawable.setAlpha(255);
        }
        draw(canvas);
    }

    @NonNull
    @Override
    public DrawableSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        drawable.setAlpha(alpha);
        return this;
    }

    @Override
    public int getWidth() {
        int width = drawable.getIntrinsicWidth();
        if (width <= 0) {
            width = drawable.getBounds().width();
        }
        return width;
    }

    @Override
    public int getHeight() {
        int height = drawable.getIntrinsicHeight();
        if (height <= 0) {
            height = drawable.getBounds().height();
        }
        return height;
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }
}
