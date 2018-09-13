package com.easefun.polyvrtmp.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.util.PolyvScreenUtils;


/**
 * 分享Fragment
 */
public class PolyvShareFragment extends Fragment implements View.OnClickListener {
    private View view;
    // 启动动画，结束动画
    private Animation startAnimation, stopAnimation;
    private LinearLayout ll_parent, ll_bottom, ll_wechat, ll_moments, ll_weibo, ll_qq, ll_qzone;
    // 取消按钮
    private Button bt_cancel;
    private boolean isShow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.polyv_rtmp_fragment_share, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findId();
        initView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetLayout();
    }

    private void resetLayout() {
        if (PolyvScreenUtils.isLandscape(getActivity())) {
            ll_parent.setGravity(Gravity.CENTER);
            ll_bottom.setLayoutParams(new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.polyv_rtmp_share_ll_width), LinearLayout.LayoutParams.WRAP_CONTENT));
        } else {
            ll_parent.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            ll_bottom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public boolean isShow() {
        return isShow;
    }

    public void show() {
        if (!isShow) {
            isShow = true;
            resetLayout();
            ll_bottom.setVisibility(View.VISIBLE);
            if (!PolyvScreenUtils.isLandscape(getActivity())) {
                ll_bottom.clearAnimation();
                ll_bottom.startAnimation(startAnimation);
            }
        }
    }

    public void selectHide() {
        if (isShow) {
            isShow = false;
            if (!PolyvScreenUtils.isLandscape(getActivity())) {
                ll_bottom.clearAnimation();
                ll_bottom.startAnimation(stopAnimation);
            } else {
                hide();
            }
        }
    }

    private void hide() {
        ll_bottom.setVisibility(View.GONE);
        getParentFragment().getChildFragmentManager().beginTransaction().hide(this).commit();
    }

    private void findId() {
        ll_parent = (LinearLayout) view.findViewById(R.id.ll_parent);
        ll_bottom = (LinearLayout) view.findViewById(R.id.ll_botttom);
        ll_wechat = (LinearLayout) view.findViewById(R.id.ll_wechat);
        ll_moments = (LinearLayout) view.findViewById(R.id.ll_moments);
        ll_weibo = (LinearLayout) view.findViewById(R.id.ll_weibo);
        ll_qq = (LinearLayout) view.findViewById(R.id.ll_qq);
        ll_qzone = (LinearLayout) view.findViewById(R.id.ll_qzone);
        bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
    }

    private void initView() {
        ll_wechat.setOnClickListener(this);
        ll_moments.setOnClickListener(this);
        ll_weibo.setOnClickListener(this);
        ll_qq.setOnClickListener(this);
        ll_qzone.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        ll_parent.setOnClickListener(this);

        startAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.polyv_rtmp_share_expand);
        stopAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.polyv_rtmp_share_collapse);
        stopAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hide();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_wechat:
                break;
            case R.id.ll_moments:
                break;
            case R.id.ll_weibo:
                break;
            case R.id.ll_qq:
                break;
            case R.id.ll_qzone:
                break;
            case R.id.ll_parent:
            case R.id.bt_cancel:
                selectHide();
                break;
        }
        selectHide();
    }

    public static String watchUrl = "http://www.polyv.net";
    public static String avatarUrl = "http://live.polyv.net/assets/wimages/pc_images/logo.png";
    public static String title = "非常有价值的直播";
    public static String body = "正在直播，非常不错喔！快来看看吧！";

    /**
     * 初始化分享配置
     *
     * @param watchUrl  观看地址
     * @param avatarUrl 头像地址
     * @param title     标题
     * @param body      内容
     */
    public static void initShareConfig(String watchUrl, String avatarUrl, String title, String body) {
        PolyvShareFragment.watchUrl = TextUtils.isEmpty(watchUrl) ? PolyvShareFragment.watchUrl : watchUrl;
        PolyvShareFragment.avatarUrl = TextUtils.isEmpty(avatarUrl) ? PolyvShareFragment.avatarUrl : avatarUrl;
        PolyvShareFragment.title = TextUtils.isEmpty(title) ? PolyvShareFragment.title : title;
        PolyvShareFragment.body = TextUtils.isEmpty(body) ? PolyvShareFragment.body : body;
    }
}
