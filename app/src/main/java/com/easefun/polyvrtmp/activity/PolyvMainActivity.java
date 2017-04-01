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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.fragment.PolyvMainFragment;
import com.easefun.polyvrtmp.util.PolyvDisplayUtils;
import com.easefun.polyvrtmp.util.PolyvScreenUtils;
import com.easefun.polyvsdk.rtmp.core.util.PolyvRTMPSDKUtil;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPDefinition;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPOrientation;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPErrorReason;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPView;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPRenderScreenSize;
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
    private long startTime = 0L;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    iv_time.setVisibility(View.GONE);
                    polyvRTMPView.capture();
                    break;
                case TIME_COUNT:
                    long timeInMillies = System.currentTimeMillis() - startTime;
                    mainFragment.getTimeView().setText(PolyvDisplayUtils.getVideoDisplayTime(timeInMillies));
                    handler.sendEmptyMessageDelayed(TIME_COUNT, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_activity_main);
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
                        int id = getResources().getIdentifier("polyv_number_" + i, "drawable", getPackageName());
                        Drawable drawable = getResources().getDrawable(id);
                        anim.addFrame(drawable, 1000);
                    }
                    anim.setOneShot(true);
                    iv_time.setImageDrawable(anim);
                    anim.start();
                }

                handler.sendEmptyMessageDelayed(START, 3000);
            }
        });

        polyvRTMPView.setOnErrorListener(new IPolyvRTMPOnErrorListener() {
            @Override
            public void onError(PolyvRTMPErrorReason errorReason) {
                switch (errorReason.getType()) {
                    case PolyvRTMPErrorReason.GET_NGB_PUSH_URL_EMPTY:
                        Toast.makeText(PolyvMainActivity.this, "获取NGB推流地址为空，请重试", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.NETWORK_DENIED:
                        Toast.makeText(PolyvMainActivity.this, "请连接网络", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.NOT_CAMERA:
                        Toast.makeText(PolyvMainActivity.this, "没有摄像头，请更换设备", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.AUDIO_AEC_ERROR:
                        Toast.makeText(PolyvMainActivity.this, "不支持音频aec", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.AUDIO_CONFIGURATION_ERROR:
                        Toast.makeText(PolyvMainActivity.this, "音频编解码器配置错误", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.AUDIO_ERROR:
                        Toast.makeText(PolyvMainActivity.this, "不能记录音频", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.AUDIO_TYPE_ERROR:
                        Toast.makeText(PolyvMainActivity.this, "音频类型错误", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.CAMERA_DISABLED:
                        Toast.makeText(PolyvMainActivity.this, "摄相机被禁用", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.CAMERA_ERROR:
                        Toast.makeText(PolyvMainActivity.this, "摄像机没有开启", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.CAMERA_NOT_SUPPORT:
                        Toast.makeText(PolyvMainActivity.this, "摄相机不支持", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.CAMERA_OPEN_FAILED:
                        Toast.makeText(PolyvMainActivity.this, "摄相机打开失败", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.SDK_VERSION_ERROR:
                        Toast.makeText(PolyvMainActivity.this, "Android sdk 版本低于18（Android 4.3.1）", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.VIDEO_CONFIGURATION_ERROR:
                        Toast.makeText(PolyvMainActivity.this, "视频编解码器配置错误", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.VIDEO_TYPE_ERROR:
                        Toast.makeText(PolyvMainActivity.this, "视频类型错误", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.NOT_LOGIN:
                        Toast.makeText(PolyvMainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                        break;
                    case PolyvRTMPErrorReason.RELOGIN_FAIL:
                        Toast.makeText(PolyvMainActivity.this, "请重新登陆", Toast.LENGTH_SHORT).show();
                        break;
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
                startTime = System.currentTimeMillis();
                handler.sendEmptyMessageDelayed(TIME_COUNT, 1000);
                Toast.makeText(PolyvMainActivity.this, "推流开始", Toast.LENGTH_SHORT).show();
            }
        });

        polyvRTMPView.setOnDisconnectionListener(new IPolyvRTMPOnDisconnectionListener() {
            @Override
            public void onDisconnection() {
                handler.removeMessages(START);
                handler.removeMessages(TIME_COUNT);
                Toast.makeText(PolyvMainActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
                //断线增加网络改变回调事件
                if (!isReceiver) {
                    isReceiver = true;
                    PolyvMainActivity.this.registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                }
            }
        });

        polyvRTMPView.setOnPublishFailListener(new IPolyvRTMPOnPublishFailListener() {
            @Override
            public void onPublishFail() {
                AlertDialog.Builder builder = new AlertDialog.Builder(PolyvMainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("推流失败，是否重试?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        handler.sendEmptyMessage(START);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog = builder.show();
                // show之后才可以获取，否则获取为null
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray_main_d));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_share));
            }
        });

        polyvRTMPView.setConfiguration(mDefinition, mOrientation);
        polyvRTMPView.setRenderScreenSize(PolyvRTMPRenderScreenSize.AR_ASPECT_FIT_PARENT);
        polyvRTMPView.setEffect(new BeautyEffect(this));
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
    protected void onStop() {
        super.onStop();
        polyvRTMPView.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        polyvRTMPView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(START);
        handler.removeMessages(TIME_COUNT);
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
