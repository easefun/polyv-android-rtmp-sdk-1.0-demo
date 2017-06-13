package com.easefun.polyvrtmp;

import android.app.Application;

import com.easefun.polyvsdk.rtmp.chat.PolyvChatManager;
import com.easefun.polyvsdk.rtmp.core.PolyvRTMPSDKClient;

public class PolyvApplication extends Application {
    public static PolyvApplication CONTEXT;
    /**
     * 登录聊天室所需，请填写自己的appId和appSecret，否则无法登陆
     * appId和appSecret在直播系统管理后台的用户信息页的API设置中用获取
     */
    private static final String appId = "enlv8q80sr";
    private static final String appSecret = "7152158bd61b4d5084da933371cb7e5e";

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
        initPolyvCilent();
        initPolyvChatConfig();
    }

    /**
     * 初始化聊天室配置
     */
    public void initPolyvChatConfig() {
        PolyvChatManager.initConfig(appId, appSecret);
    }

    public void initPolyvCilent() {
        PolyvRTMPSDKClient client = PolyvRTMPSDKClient.getInstance();
        //启动Bugly
//        client.initCrashReport(getApplicationContext());
        //启动Bugly后，在学员登录时设置学员id
//        client.crashReportSetUserId(userId);
    }
}
