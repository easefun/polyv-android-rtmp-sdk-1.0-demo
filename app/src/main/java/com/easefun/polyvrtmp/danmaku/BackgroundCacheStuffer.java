package com.easefun.polyvrtmp.danmaku;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;

public class BackgroundCacheStuffer extends SpannedCacheStuffer {
    // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
    private final Paint paint = new Paint();
    private int color;
    private float radius, padding, offset;

    public BackgroundCacheStuffer(int color, float radius, float padding, float offset) {
        this.color = color;
        this.radius = radius;
        this.padding = padding;
        this.offset = offset;
    }

    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
        danmaku.padding = (int) padding; // 在背景绘制模式下增加padding
        super.measure(danmaku, paint, fromWorkerThread);
    }

    @Override
    public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        RectF rect = new RectF(left + offset, top + offset, left + danmaku.paintWidth - offset, top + danmaku.paintHeight - offset);
        canvas.drawRoundRect(rect, radius, radius, paint);
    }

    @Override
    public void drawStroke(BaseDanmaku danmaku, String lineText, Canvas canvas, float left, float top,
                           Paint paint) {
        // 禁用描边绘制
    }
}
