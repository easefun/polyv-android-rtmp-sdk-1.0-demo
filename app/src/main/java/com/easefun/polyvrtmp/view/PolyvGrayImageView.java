package com.easefun.polyvrtmp.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.easefun.polyvrtmp.R;

public class PolyvGrayImageView extends ImageView {
    private boolean drawColor;

    public PolyvGrayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PolyvGrayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PolyvGrayImageView(Context context) {
        super(context);
    }

    public void setImageDrawable(Drawable drawable, boolean drawColor) {
        setImageDrawable(drawable);
        this.drawColor = drawColor;
    }

    public void changeDrawColor(boolean drawColor){
        this.drawColor = drawColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isPressed() && drawColor)
            canvas.drawColor(getResources().getColor(R.color.polyv_rtmp_translucence_share));
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        invalidate();
    }
}
