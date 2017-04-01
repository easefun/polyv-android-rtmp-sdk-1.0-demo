package com.easefun.polyvrtmp.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import com.easefun.polyvrtmp.R;

public class PolyvLineEdittext extends EditText{
    private Paint mPaint;

    public PolyvLineEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        // 你可以根据自己的具体需要在此处对画笔做更多个性化设置
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PolyvLineEdittext, 0, 0);
        mPaint.setColor(a.getColor(R.styleable.PolyvLineEdittext_line_color, Color.WHITE));
        a.recycle();

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画底线
        canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, mPaint);
    }
}
