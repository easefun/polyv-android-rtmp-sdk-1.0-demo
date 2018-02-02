package com.easefun.polyvrtmp.util;

import android.support.annotation.NonNull;

import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPErrorReason;

/**
 * 错误类型转成错误信息工具类
 * @author Lion 2018-1-19
 */
public class PolyvErrorMessageUtils {

    /**
     * 获取推流错误信息
     * @param type 推流错误类型
     * @return 错误信息字符串
     */
    @NonNull
    public static String getRTMPErrorMessage(@PolyvRTMPErrorReason.ErrorReason int type) {
        switch (type) {
            case PolyvRTMPErrorReason.GET_NGB_PUSH_URL_EMPTY:
                return "获取NGB推流地址为空，请重新推流或者切换网络重新推流";
            case PolyvRTMPErrorReason.NETWORK_DENIED:
                return "请连接网络";
            case PolyvRTMPErrorReason.NOT_CAMERA:
                return "没有摄像头，请更换设备";
            case PolyvRTMPErrorReason.AUDIO_AEC_ERROR:
                return "不支持音频aec";
            case PolyvRTMPErrorReason.AUDIO_CONFIGURATION_ERROR:
                return "音频编解码器配置错误";
            case PolyvRTMPErrorReason.AUDIO_ERROR:
                return "不能记录音频";
            case PolyvRTMPErrorReason.AUDIO_TYPE_ERROR:
                return "音频类型错误";
            case PolyvRTMPErrorReason.CAMERA_DISABLED:
                return "摄相机被禁用";
            case PolyvRTMPErrorReason.CAMERA_ERROR:
                return "摄像机没有开启";
            case PolyvRTMPErrorReason.CAMERA_NOT_SUPPORT:
                return "摄相机不支持";
            case PolyvRTMPErrorReason.CAMERA_OPEN_FAILED:
                return "摄相机打开失败";
            case PolyvRTMPErrorReason.SDK_VERSION_ERROR:
                return "Android sdk 版本低于18（Android 4.3.1）";
            case PolyvRTMPErrorReason.VIDEO_CONFIGURATION_ERROR:
                return "视频编解码器配置错误";
            case PolyvRTMPErrorReason.VIDEO_TYPE_ERROR:
                return "视频类型错误";
            case PolyvRTMPErrorReason.NOT_LOGIN:
                return "请先登录";
            case PolyvRTMPErrorReason.RELOGIN_FAIL:
                return "请重新登陆";
            case PolyvRTMPErrorReason.PARAM_ERROR:
                return "参数错误，请设置正确的参数重新推流";
            default:
                return "推流失败";
        }
    }
}
