
polyv-android-rtmp-sdk-1.0-demo
===
[![build passing](https://img.shields.io/badge/build-passing-brightgreen.svg)](#)
[![GitHub release](https://img.shields.io/badge/release-v2.0.0-blue.svg)](https://github.com/easefun/polyv-android-rtmp-sdk-1.0-demo/releases/tag/v2.0.0)

### _1 polyv-android-rtmp-sdk-1.0_（以下简称**rtmpSDK1.0**）是什么？
rtmpSDK1.0是Polyv为开发者用户提供的直播推流SDK ，是jar文件。易于集成，内部包含`直播推流` `聊天室` `弹幕` 功能。首先需要在[Polyv官网](http://www.polyv.net)注册账户并开通直播功能，然后集成rtmpSDK1.0到你的项目中。
### _2 polyv-android-rtmp-sdk-1.0-demo_（以下简称**rtmpSDK1.0demo**）是什么？
rtmpSDK1.0demo是rtmpSDK1.0的demo示例Android studio项目工程，其中包含了最新rtmpSDK1.0并且演示了如何在项目中集成rtmpSDK1.0。
***
###  3 运行环境
* JDK 1.7 或以上
* Android SDK 18 或以上
* Android Studio 2.2.0 或以上
***
### 4 支持功能
* 选择推流清晰度
* 选择横竖屏推流
* 支持静音
* 支持美颜
* 支持摄像头切换
* 支持闪光灯操作
* 硬编码
* 弱网络环境处理
* 音频降噪
***
### 5 关于分享
分享功能只有界面，功能需要自行研发。
***
### 6 更多关于rtmpSDK1.0demo和rtmpSDK1.0的详细介绍请看[Wiki](https://github.com/easefun/polyv-android-rtmp-sdk-1.0-demo/wiki)。
1.0.3版API文档请看[v1.0.3 API](http://repo.polyv.net/android/rtmp/sdk/1.0.3/api/index.html)。<br/>
1.0.4版API文档请看[v1.0.4 API](http://repo.polyv.net/android/rtmp/sdk/1.0.4/api/index.html)。<br/>
1.0.5版API文档请看[v1.0.5 API](http://repo.polyv.net/android/rtmp/sdk/1.0.5/api/index.html)。<br/>
1.0.6版API文档请看[v1.0.6 API](http://repo.polyv.net/android/rtmp/sdk/1.0.6/api/index.html)。<br/>
1.0.7版API文档请看[v1.0.7 API](http://repo.polyv.net/android/rtmp/sdk/1.0.7/api/index.html)。<br/>
1.0.8版API文档请看[v1.0.8 API](http://repo.polyv.net/android/rtmp/sdk/1.0.8/api/index.html)。<br/>
2.0.0版API文档请看[v2.0.0 API](http://repo.polyv.net/android/rtmp/sdk/2.0.0/api/index.html)。<br/>
2.1.0版API文档请看[v2.1.0 API](http://repo.polyv.net/android/rtmp/sdk/2.1.0/api/index.html)。<br/>
2.2.0版API文档请看[v2.2.0 API](http://repo.polyv.net/android/rtmp/sdk/2.2.0/api/index.html)。



### 7 项目结构

```
.
├── PolyvApplication.java	//app类
├── activity				//活动界面
├── adapter					//列表适配器
├── com						
├── danmaku					//弹幕
├── fragment				//Fragment
├── permission				//权限请求器
├── util					//工具类
└── view					//自定义View
```



### 8 依赖

#### 8.1 引入推流SDK

引入app/libs目录下的两个jar包

```
polyvRTMP2.2.0.jar
polyvRTMPChat1.0.8.jar
```



#### 8.2 引入第三方依赖

``` groovy
compile 'com.android.support:support-v4:23+'
compile 'com.android.support:appcompat-v7:23+'
compile 'com.android.support:design:23+'
compile 'com.android.support:support-annotations:23+'
//异步加载图片，用于显示在线人数的头像
compile 'jp.wasabeef:glide-transformations:2.0.1'
//圆形图片，用于显示圆形logo
compile 'de.hdodenhof:circleimageview:1.3.0'
//聊天室
compile('io.socket:socket.io-client:0.8.3') {
    // excluding org.json which is provided by Android
    exclude group: 'org.json', module: 'json'
}
//弹幕
compile 'com.github.ctiao:DanmakuFlameMaster:0.6.2'
//裁剪图片的库，用于选择直播logo。下面排除android的库是因为会跟上面自己依赖的android库版本冲突，可以根据自己的版本调整。
compile('com.yalantis:ucrop:2.2.0') {
    exclude group: 'com.android.support', module: 'appcompat-v7'
    exclude group: 'com.android.support', module: 'support-v4'
    exclude group: 'com.android.support', module: 'support-annotations'
}
//bugly
compile 'com.tencent.bugly:crashreport:latest.release'
```

### 9 项目配置

申请权限

``` xml
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_LOGS" />
    <uses-permission android:name="android.permission.CAMERA" /><!-- 属于android6.0运行时权限-->
    <uses-permission android:name="android.permission.RECORD_AUDIO" /><!-- 属于android6.0运行时权限-->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /><!-- 属于android6.0运行时权限-->
```



### 10 代码示例

#### 10.1 初始化

初始化Application中的配置。

```
PolyvRTMPSDKClient client = PolyvRTMPSDKClient.getInstance();
//启动Bugly
client.initCrashReport(getApplicationContext());
//启动Bugly后，在学员登录时设置学员id
client.crashReportSetUserId(userId);
```

初始化登录SDK

``` java
PolyvRTMPLoginVerify.verify(channelId, password, new IPolyvRTMPLoginListener() {
    @Override
    public void onError(PolyvRTMPLoginErrorReason errorReason) {
        //登录错误
    }

    @Override
    public void onSuccess(String[] preview_nickname_avatar) {
        //登录成功

        //登录成功初始化聊天室配置
        PolyvChatManager.initConfig(PolyvRTMPLoginVerify.getPolyvPublishVO().getAppId(), PolyvRTMPLoginVerify.getPolyvPublishVO().getAppSecret());
    }
}, getApplicationContext());
```

注意，在登陆成功的回调中初始化了聊天室，详细的示例代码见`PolyvLoginActivity`

#### 10.2 直播推流

```java
PolyvRTMPView polyvRTMPView = (PolyvRTMPView) findViewById(R.id.polyv_rtmp_view);
//设置监听器
polyvRTMPView.setOnPreparedListener();
polyvRTMPView.setOnErrorListener();
polyvRTMPView.setOnOpenCameraSuccessListener();
polyvRTMPView.setOnCameraChangeListener();
polyvRTMPView.setOnLivingStartSuccessListener();
polyvRTMPView.setOnDisconnectionListener();
polyvRTMPView.setOnPublishFailListener();
polyvRTMPView.setOnTakePictureListener();
polyvRTMPView.setOnCallbackSessionIdListener();
//设置缓冲占位图
polyvRTMPView.setPlayerBufferingIndicator(playerBuffering);
//设置清晰度和方向
polyvRTMPView.setConfiguration(mDefinition, mOrientation);
//设置缩放模式
polyvRTMPView.setRenderScreenSize(PolyvRTMPRenderScreenSize.AR_ASPECT_FIT_PARENT);
//设置美颜效果
if (isBeautyOn){
    polyvRTMPView.setEffect(new BeautyEffect(this));
}else {
    polyvRTMPView.setEffect(new NullEffect(this));
}

//开始推流
polyvRTMPView.beginLive(mChannelId);
//停止推流
polyvRTMPView.stop();
//销毁
polyvRTMPView.destroy();
```

详细使用方式见`PolyvMainActivity`和`PolyvMainFragment`

#### 10.3 聊天室

``` java
//实例化对象
PolyvChatManager chatManager = new PolyvChatManager();
//监听状态和消息
chatManager.setOnChatManagerListener(new PolyvChatManager.ChatManagerListener() {
    @Override
    public void connectStatus(PolyvChatManager.ConnectStatus connect_status) {
        //聊天室连接状态回调
    }
    @Override
    public void receiveChatMessage(PolyvChatMessage chatMessage) {
        //聊天室消息回调
    }
});
//登录聊天室
chatManager.login(userId, channelId, nickName);
//发送消息到聊天室
chatManager.sendChatMsg(chatMessage);
//退出聊天室
chatManager.disconnect();
```

详细使用方式见`PolyvMainFragment`

#### 10.4 弹幕

弹幕功能由一个Fragment来集成：`PolyvDanmakuFragment`

``` java
//发送弹幕
addDanmaku(CharSequence message);
addDanmaku(CharSequence message, String url);//url是头像
//打开或关闭弹幕
toggle(String str)；
```

详细使用方式见`PolyvDanmakuFragment`