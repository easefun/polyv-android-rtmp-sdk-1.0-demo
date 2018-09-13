package com.easefun.polyvrtmp.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.danmaku.BackgroundCacheStuffer;
import com.easefun.polyvrtmp.danmaku.RelativeImageSpan;

import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;

/**
 * 弹幕布局的Fragment
 */
public class PolyvDanmakuFragment extends Fragment {
    public static final String ON = "on";
    public static final String OFF = "off";
    private View view;
    // 弹幕view
    private IDanmakuView mDanmakuView;
    // 弹幕解析器
    private BaseDanmakuParser mParser;
    // 弹幕配置器
    private DanmakuContext mContext;
    private CropCircleTransformation cropCircleTransformation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.polyv_rtmp_fragment_danmaku, container, false);
        return view;
    }

    private void findId() {
        mDanmakuView = (IDanmakuView) view.findViewById(R.id.dv_danmaku);
    }

    private void initView() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 2); // 滚动弹幕最大显示5行
        maxLinesPair.put(BaseDanmaku.TYPE_FIX_TOP, 2);
        maxLinesPair.put(BaseDanmaku.TYPE_FIX_BOTTOM, 2);
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        //--------------------------------------------------------//

        mContext = DanmakuContext.create();
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE).setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f).setScaleTextSize(1.0f)
                .setCacheStuffer(new BackgroundCacheStuffer(getResources().getColor(R.color.polyv_rtmp_translucence_share),
                        getResources().getDimension(R.dimen.polyv_rtmp_danmaku_iv_radius),
                        getResources().getDimension(R.dimen.polyv_rtmp_danmaku_padding) + getResources().getDimension(R.dimen.polyv_rtmp_danmaku_offset),
                        0 + getResources().getDimension(R.dimen.polyv_rtmp_danmaku_offset)), new BaseCacheStuffer.Proxy() {
                    @Override
                    public void prepareDrawing(BaseDanmaku danmaku, boolean fromWorkerThread) {
                    }

                    @Override
                    public void releaseResource(BaseDanmaku danmaku) {
                        if (danmaku.text instanceof Spanned) {
                            danmaku.text = "";
                        }
                    }
                }) // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair).preventOverlapping(overlappingEnablePair);
        mDanmakuView.showFPS(false);
        mDanmakuView.enableDanmakuDrawingCache(false);
        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                mDanmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {
            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
            }

            @Override
            public void drawingFinished() {
            }
        });
        mDanmakuView.prepare(mParser = new BaseDanmakuParser() {
            @Override
            protected IDanmakus parse() {
                return new Danmakus();
            }
        }, mContext);

        cropCircleTransformation = new CropCircleTransformation(getContext());
    }

    public class GlideRequestListener implements RequestListener<Object, GlideDrawable> {
        private CharSequence message;

        public GlideRequestListener(CharSequence message) {
            this.message = message;
        }

        @Override
        public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
            addDanmaKuShowTextAndImage(message, null);
            return true;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            resource.setBounds(0, 0, (int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_iv_bound), (int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_iv_bound));
            addDanmaKuShowTextAndImage(message, resource);
            return true;
        }
    }

    /**
     * 发送弹幕
     *
     * @param message
     */
    public void addDanmaku(CharSequence message) {
        addDanmaKuShowTextAndImage(message, null);
    }

    /**
     * 发送弹幕
     *
     * @param message 信息
     * @param url     图片地址
     */
    public void addDanmaku(CharSequence message, String url) {
        Glide.with(this).load(url).bitmapTransform(cropCircleTransformation).listener(new GlideRequestListener(message)).into((int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_iv_bound), (int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_iv_bound));
    }

    /**
     * 发送弹幕
     *
     * @param message 信息
     * @param id      图片的资源id
     */
    public void addDanmaku(CharSequence message, @DrawableRes int id) {
        Glide.with(this).load(id).bitmapTransform(cropCircleTransformation).listener(new GlideRequestListener(message)).into((int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_iv_bound), (int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_iv_bound));
    }

    private void addDanmaKuShowTextAndImage(CharSequence message, Drawable drawable) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        SpannableStringBuilder spannable = createSpannable(message, drawable);
        danmaku.text = spannable;
        danmaku.padding = 0;
        danmaku.priority = 1; // 一定会显示, 一般用于本机发送的弹幕
        danmaku.setTime(mDanmakuView.getCurrentTime() + 100);
        danmaku.textSize = getResources().getDimension(R.dimen.polyv_rtmp_danmaku_tv_textsize);
        danmaku.textColor = Color.WHITE;
        mDanmakuView.addDanmaku(danmaku);
    }

    private SpannableStringBuilder createSpannable(CharSequence message, Drawable drawable) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d");
        if (drawable == null) {
            drawable = getResources().getDrawable(R.drawable.polyv_rtmp_default_logo);
            drawable.setBounds(0, 0, 0, (int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_iv_bound));
            drawable.setAlpha(0);
            RelativeImageSpan span = new RelativeImageSpan(drawable, RelativeImageSpan.ALIGN_CENTER);
            spannableStringBuilder.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append("   " + message + "   ");
        } else {
            RelativeImageSpan span = new RelativeImageSpan(drawable, RelativeImageSpan.ALIGN_CENTER, (int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_paddingleft), (int) getResources().getDimension(R.dimen.polyv_rtmp_danmaku_paddingright));// ImageSpan.ALIGN_BOTTOM);
            spannableStringBuilder.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(message + "   ");
        }
        return spannableStringBuilder;
    }

    /**
     * 弹幕开关
     *
     * @param str
     */
    public void toggle(String str) {
        if (mDanmakuView != null) {
            if (str.equals(ON))
                mDanmakuView.show();
            else if (str.equals(OFF))
                mDanmakuView.hide();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findId();
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }
}
