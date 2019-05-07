package com.easefun.polyvrtmp.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.fragment.PolyvMainFragment;
import com.easefun.polyvrtmp.util.PolyvDisplayUtils;
import com.easefun.polyvrtmp.util.PolyvErrorMessageUtils;
import com.easefun.polyvrtmp.util.PolyvScreenUtils;
import com.easefun.polyvsdk.rtmp.core.util.PolyvRTMPSDKUtil;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPDefinition;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPErrorReason;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPOrientation;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPRenderScreenSize;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPView;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnCallbackSessionIdListener;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnCameraChangeListener;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnDisconnectionListener;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnErrorListener;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnLivingStartSuccessListener;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnOpenCameraSuccessListener;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnPreparedListener;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnPublishFailListener;
import com.easefun.polyvsdk.rtmp.sopcast.video.effect.BeautyEffect;

public class PolyvMainActivity extends FragmentActivity {
    private PolyvRTMPView polyvRTMPView = null;
    private ProgressBar playerBuffering = null;
    // 倒计时控件
    private ImageView iv_time = null;
    // 主视图的Fragment
    private PolyvMainFragment mainFragment = null;
    private AlertDialog alertDialog = null;

    private String mChannelId;
    private int mOrientation;
    private int mDefinition;

    private static final int START = 1;
    private static final int TIME_COUNT = 2;
    private long time = 0L;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    iv_time.setVisibility(View.GONE);
//                    polyvRTMPView.capture(mChannelId, "appId", "appSecret");
                    polyvRTMPView.beginLive(mChannelId);
                    break;
                case TIME_COUNT:
                    time = time + 1000;
                    mainFragment.getTimeView().setText(PolyvDisplayUtils.getVideoDisplayTime(time));
                    handler.sendEmptyMessageDelayed(TIME_COUNT, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_rtmp_activity_main);
        initExtraParam();
        initOrientation();
        initViews();
        initPolyvRTMPView();
        addFragment(mChannelId);
    }

    private void addFragment(String channelId) {
        mainFragment = new PolyvMainFragment();
        mainFragment.loginChatRoom(Build.SERIAL + "123", channelId, "主持人");
        mainFragment.setPolyvRTMPView(polyvRTMPView);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, mainFragment, "mainFragment").commit();
    }

    private void initExtraParam() {
        mChannelId = getIntent().getStringExtra("channelId");
        mOrientation = getIntent().getIntExtra("orientation", PolyvRTMPOrientation.SCREEN_ORIENTATION_LANDSCAPE);
        mDefinition = getIntent().getIntExtra("definition", PolyvRTMPDefinition.GAO_QING);
    }

    private void initOrientation() {
        switch (mOrientation) {
            case PolyvRTMPOrientation.SCREEN_ORIENTATION_PORTRAIT:
                PolyvScreenUtils.setPortrait(this);
                break;

            case PolyvRTMPOrientation.SCREEN_ORIENTATION_LANDSCAPE:
                PolyvScreenUtils.setLandscape(this);
                break;
        }
    }

    private void initViews() {
        polyvRTMPView = (PolyvRTMPView) findViewById(R.id.polyv_rtmp_view);
        playerBuffering = (ProgressBar) findViewById(R.id.player_buffering);
        iv_time = (ImageView) findViewById(R.id.iv_time);
    }

    private void initPolyvRTMPView() {
        polyvRTMPView.setPlayerBufferingIndicator(playerBuffering);
        polyvRTMPView.setOnPreparedListener(new IPolyvRTMPOnPreparedListener() {
            @Override
            public void onPrepared() {
                if (iv_time.getVisibility() == View.VISIBLE) {
                    // 倒计时
                    AnimationDrawable anim = new AnimationDrawable();
                    for (int i = 3; i > 0; i--) {
                        int id = getResources().getIdentifier("polyv_rtmp_number_" + i, "drawable", getPackageName());
                        Drawable drawable = getResources().getDrawable(id);
                        anim.addFrame(drawable, 1000);
                    }
                    anim.setOneShot(true);
                    iv_time.setImageDrawable(anim);
                    anim.start();

                    handler.sendEmptyMessageDelayed(START, 3000);
                }
            }
        });

        polyvRTMPView.setOnErrorListener(new IPolyvRTMPOnErrorListener() {
            @Override
            public void onError(PolyvRTMPErrorReason errorReason) {
                if (errorReason.getType() == PolyvRTMPErrorReason.GET_NGB_PUSH_URL_EMPTY) {
                    showRestartAlertDialog("提示", "推流失败，是否重试?(error code " + errorReason.getType() + ")", "确定", "取消");
                } else {
                    String message = PolyvErrorMessageUtils.getRTMPErrorMessage(errorReason.getType());
                    message += "(error code " + errorReason.getType() + ")";
                    AlertDialog.Builder builder = new AlertDialog.Builder(PolyvMainActivity.this);
                    builder.setTitle("错误");
                    builder.setMessage(message);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }

                mainFragment.getTimeView().setText(PolyvDisplayUtils.getVideoDisplayTime(0));
                handler.removeMessages(TIME_COUNT);
            }
        });

        polyvRTMPView.setOnOpenCameraSuccessListener(new IPolyvRTMPOnOpenCameraSuccessListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PolyvMainActivity.this, "打开摄像机成功", Toast.LENGTH_SHORT).show();
            }
        });

        polyvRTMPView.setOnCameraChangeListener(new IPolyvRTMPOnCameraChangeListener() {
            @Override
            public void onChange() {
                Toast.makeText(PolyvMainActivity.this, "切换摄像机", Toast.LENGTH_SHORT).show();
            }
        });

        polyvRTMPView.setOnLivingStartSuccessListener(new IPolyvRTMPOnLivingStartSuccessListener() {
            @Override
            public void onSuccess() {
                mainFragment.getTimeView().setVisibility(View.VISIBLE);
                handler.sendEmptyMessageDelayed(TIME_COUNT, 1000);
                Toast.makeText(PolyvMainActivity.this, "推流开始", Toast.LENGTH_SHORT).show();
            }
        });

        polyvRTMPView.setOnDisconnectionListener(new IPolyvRTMPOnDisconnectionListener() {
            @Override
            public void onDisconnection() {
                handler.removeMessages(START);
                handler.removeMessages(TIME_COUNT);
                if (!PolyvRTMPSDKUtil.isOpenNetwork(getApplicationContext())) {
                    //断线增加网络改变回调事件
                    Toast.makeText(PolyvMainActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
                    if (!isReceiver) {
                        isReceiver = true;
                        PolyvMainActivity.this.registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                    }
                } else {
                    showRestartAlertDialog("当前直播已停止", "网络情况不佳，请稍后重试", "重试", "忽略");
                }
            }
        });

        polyvRTMPView.setOnPublishFailListener(new IPolyvRTMPOnPublishFailListener() {
            @Override
            public void onPublishFail() {
                showRestartAlertDialog("提示", "推流失败，是否重试?", "确定", "取消");
            }
        });

        polyvRTMPView.setOnCallbackSessionIdListener(new IPolyvRTMPOnCallbackSessionIdListener() {
            @Override
            public void onSuccess(String sessionId) {
                //TODO 如有需要，请根据自己的业务开发逻辑
                Log.i("PolyvMainActivity", sessionId);
//                Toast.makeText(PolyvMainActivity.this, "场次id：" + polyvRTMPView.getSessionId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                //TODO 如有需要，请根据自己的业务开发逻辑
//                Toast.makeText(PolyvMainActivity.this, "场次id获取失败", Toast.LENGTH_SHORT).show();
//                showRestartAlertDialog("提示", "推流失败，是否重试?", "确定", "取消");
            }
        });

        polyvRTMPView.setConfiguration(mDefinition, mOrientation);
        polyvRTMPView.setRenderScreenSize(PolyvRTMPRenderScreenSize.AR_ASPECT_FIT_PARENT);
        polyvRTMPView.setEffect(new BeautyEffect(this));
    }

    private void showRestartAlertDialog(String title, String message, String positiveButtonText, String NegativeButtonText) {
        if (alertDialog == null || !alertDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PolyvMainActivity.this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    handler.sendEmptyMessage(START);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(NegativeButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog = builder.show();
            // show之后才可以获取，否则获取为null
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.polyv_rtmp_gray_main_d));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.polyv_rtmp_color_custom));
        }
    }

    // 网络恢复，重连推流
    private boolean isReceiver = false;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PolyvRTMPSDKUtil.isOpenNetwork(getApplicationContext())) {
                handler.sendEmptyMessage(START);
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (iv_time.getVisibility() != View.VISIBLE)
            polyvRTMPView.callOnTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(START);
        handler.removeMessages(TIME_COUNT);
        polyvRTMPView.stop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        handler.sendEmptyMessageDelayed(START, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        polyvRTMPView.destroy();
        if (isReceiver) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onBackPressed() {
        if (mainFragment.isShowChild()) {
            mainFragment.hideChild();
            return;
        }
        super.onBackPressed();
    }
}
