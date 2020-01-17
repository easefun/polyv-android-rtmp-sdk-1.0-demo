package com.easefun.polyvrtmp.util.face;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PLVSTextFaceLoader {

    public static CharSequence messageToSpan(CharSequence charSequence, int size, Context context) {
        return messageToSpan(charSequence, size, context, null);
    }

    public static CharSequence messageToSpan(CharSequence charSequence, int size, Context context, List<int[]> spanIndexs) {
        if (charSequence instanceof String) {
            charSequence = Html.fromHtml((String) charSequence);//html转义
        }
        int reqWidth;
        int reqHeight;
        reqWidth = reqHeight = size;
        SpannableStringBuilder span = new SpannableStringBuilder(charSequence);
        int start;
        int end;
        Pattern pattern = Pattern.compile("\\[[^\\[]{1,5}\\]");
        Matcher matcher = pattern.matcher(charSequence);
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String group = matcher.group();
            Drawable drawable;
            ImageSpan imageSpan;
            try {
                drawable = context.getResources().getDrawable(PLVSFaceManager.getInstance().getFaceId(group));
                imageSpan = new PLVSRelativeImageSpan(drawable, PLVSRelativeImageSpan.ALIGN_CENTER);
            } catch (Exception e) {
                continue;
            }
            drawable.setBounds(0, 0, (int) (reqWidth * 1.5), (int) (reqHeight * 1.5));
            span.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (spanIndexs != null) {
                spanIndexs.add(new int[]{start, end});
            }
        }
        return span;
    }

    public static void setFaceSpan(Context context,SpannableStringBuilder span,int textSize,CharSequence charSequence){
        if (charSequence instanceof String) {
            charSequence = Html.fromHtml((String) charSequence);//html转义
        }
        int reqWidth;
        int reqHeight;
        reqWidth = reqHeight = textSize;
        int start;
        int end;
        Pattern pattern = Pattern.compile("\\[[^\\[]{1,5}\\]");
        Matcher matcher = pattern.matcher(charSequence);
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            String group = matcher.group();
            Drawable drawable;
            ImageSpan imageSpan;
            try {
                drawable = context.getResources().getDrawable(PLVSFaceManager.getInstance().getFaceId(group));
                imageSpan = new PLVSRelativeImageSpan(drawable, PLVSRelativeImageSpan.ALIGN_CENTER);
            } catch (Exception e) {
                continue;
            }
            drawable.setBounds(0, 0, (int) (reqWidth * 1.5), (int) (reqHeight * 1.5));
            span.setSpan(imageSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * 显示带本地表情图片的图文混排
     */
    public static void displayTextImage(CharSequence charSequence, TextView textView) {
        textView.setText(messageToSpan(charSequence, (int) textView.getTextSize(), textView.getContext()));
    }
}
