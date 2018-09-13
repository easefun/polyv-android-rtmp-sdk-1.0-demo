package com.easefun.polyvrtmp;

import android.app.Application;

import com.easefun.polyvsdk.rtmp.core.PolyvRTMPSDKClient;

public class PolyvApplication extends Application {
    /**
     * 登录聊天室所需，请填写自己的appId和appSecret，否则无法登陆
     * appId和appSecret在云直播管理后台的开发设置页的身份认证中用获取
     * 不需要手动设置，改为在登陆成功方法中设置
     */
//    private static final String appId = "";
//    private static final String appSecret = "";

    @Override
    public void onCreate() {
        super.onCreate();
        initPolyvCilent();
        initPolyvChatConfig();
    }

    /**
     * 初始化聊天室配置
     */
    public void initPolyvChatConfig() {
        //不需要手动设置，改为在登陆成功方法中设置，见PolyvLoginActivity
//        PolyvChatManager.initConfig(appId, appSecret);
    }

    public void initPolyvCilent() {
        PolyvRTMPSDKClient client = PolyvRTMPSDKClient.getInstance();
        //启动Bugly
        client.initCrashReport(getApplicationContext());
        //启动Bugly后，在学员登录时设置学员id
//        client.crashReportSetUserId(userId);
    }
}
