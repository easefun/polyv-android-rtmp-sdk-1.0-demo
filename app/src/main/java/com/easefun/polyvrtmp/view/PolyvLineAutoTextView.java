package com.easefun.polyvrtmp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class PolyvLineAutoTextView extends AutoCompleteTextView{
    private Paint mPaint;

    public PolyvLineAutoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mPaint = new Paint();
//        mPaint.setStyle(Paint.Style.STROKE);
//        // 你可以根据自己的具体需要在此处对画笔做更多个性化设置
//        mPaint.setColor(Color.WHITE);

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画底线
//        canvas.drawLine(0, this.getHeight() - 1, this.getWidth() * 2, this.getHeight() - 1, mPaint);
    }
}
