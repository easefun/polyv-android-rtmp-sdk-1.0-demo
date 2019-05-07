package com.easefun.polyvrtmp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.permission.PolyvPermission;
import com.easefun.polyvrtmp.util.PolyvStatusBarUtil;
import com.easefun.polyvrtmp.view.PolyvGrayImageView;
import com.easefun.polyvsdk.rtmp.chat.permission.PolyvPermissionManager;
import com.easefun.polyvsdk.rtmp.core.userinterface.PolyvAuthTypeSetting;
import com.easefun.polyvsdk.rtmp.core.userinterface.PolyvTitleUpdate;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPDefinition;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPOrientation;
import com.yalantis.ucrop.UCrop;

import java.io.File;

/**
 * 引导页Activity
 */
public class PolyvSettingActivity extends Activity implements View.OnClickListener {
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    public static final String ACTION_FINISH = "polyv.activity.setting.receiver";
    private static final String LOGO_NAME = "polyvlivelogo";
    // logo
    private PolyvGrayImageView iv_logo;
    private ImageView iv_portrait, iv_landscape, iv_sc, iv_hd, iv_sd, iv_public, iv_password, iv_pay;
    private RelativeLayout rl_portrait, rl_landscape, rl_sc, rl_hd, rl_sd, rl_public, rl_password, rl_pay;
    private ImageView iv_wechat, iv_moments, iv_weibo, iv_qq, iv_share_link;
    // 标题编辑框
    private EditText et_title;
    private TextView tv_count;
    // 注销按钮
    private TextView tv_logoff;
    // 开始按钮
    private Button bt_start;
    // 分享的平台
    private String sharePlatform;
    // 标题
    private String title;
    private PolyvPermission polyvPermission = null;

    private String mChannelId;
    private int mOrientation = PolyvRTMPOrientation.SCREEN_ORIENTATION_LANDSCAPE;
    private int mDefinition = PolyvRTMPDefinition.GAO_QING;
    // 观看认证类型
    private String authType = PolyvAuthTypeSetting.AUTHTYPE_NONE;
    // 密码
    private String code;
    // 价格
    private double price;
    private FinishBroadcastReceiver receiver;

    private PolyvPermissionManager permissionManager;
    private final int myRequestCode = 135;

    private class FinishBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && ACTION_FINISH.equals(intent.getAction())) {
                finish();
            }
        }
    }

    //注册广播接收者
    private void registerReceiver() {
        receiver = new FinishBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FINISH);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_rtmp_activity_setting);
        // edittext->adjustPan(否则部分手机会有延迟的白色背景)
        try {
            PolyvStatusBarUtil.setColor(this, getResources().getColor(R.color.polyv_rtmp_color_custom_setting_top_color), 0);
        }catch (Exception e){
            // ignore
        }
        title = getIntent().getStringExtra("title");
        findId();
        initView();
        registerReceiver();
        mChannelId = getIntent().getStringExtra("channelId");

        polyvPermission = new PolyvPermission();
        polyvPermission.setResponseCallback(new PolyvPermission.ResponseCallback() {
            @Override
            public void callback() {
                gotoPlay();
            }
        });
    }

    private void findId() {
        iv_portrait = (ImageView) findViewById(R.id.iv_portrait);
        iv_landscape = (ImageView) findViewById(R.id.iv_landscape);
        iv_sc = (ImageView) findViewById(R.id.iv_sc);
        iv_hd = (ImageView) findViewById(R.id.iv_hd);
        iv_sd = (ImageView) findViewById(R.id.iv_sd);
        iv_public = (ImageView) findViewById(R.id.iv_public);
        iv_password = (ImageView) findViewById(R.id.iv_password);
        iv_pay = (ImageView) findViewById(R.id.iv_pay);

        rl_portrait = (RelativeLayout) findViewById(R.id.rl_portrait);
        rl_landscape = (RelativeLayout) findViewById(R.id.rl_landscape);
        rl_sc = (RelativeLayout) findViewById(R.id.rl_sc);
        rl_hd = (RelativeLayout) findViewById(R.id.rl_hd);
        rl_sd = (RelativeLayout) findViewById(R.id.rl_sd);
        rl_public = (RelativeLayout) findViewById(R.id.rl_public);
        rl_password = (RelativeLayout) findViewById(R.id.rl_password);
        rl_pay = (RelativeLayout) findViewById(R.id.rl_pay);

        iv_logo = (PolyvGrayImageView) findViewById(R.id.iv_logo);
        iv_wechat = (ImageView) findViewById(R.id.iv_wechat);
        iv_moments = (ImageView) findViewById(R.id.iv_moments);
        iv_weibo = (ImageView) findViewById(R.id.iv_weibo);
        iv_qq = (ImageView) findViewById(R.id.iv_qq);
        iv_share_link = (ImageView) findViewById(R.id.iv_share_link);
        bt_start = (Button) findViewById(R.id.bt_start);
        et_title = (EditText) findViewById(R.id.et_title);
        tv_logoff = (TextView) findViewById(R.id.tv_logoff);
        tv_count = (TextView) findViewById(R.id.tv_count);
    }

    private void initView() {
        rl_portrait.setOnClickListener(this);
        rl_landscape.setOnClickListener(this);
        rl_sc.setOnClickListener(this);
        rl_hd.setOnClickListener(this);
        rl_sd.setOnClickListener(this);
        rl_public.setOnClickListener(this);
        rl_password.setOnClickListener(this);
        rl_pay.setOnClickListener(this);
        iv_logo.setOnClickListener(this);
        iv_wechat.setOnClickListener(this);
        iv_moments.setOnClickListener(this);
        iv_weibo.setOnClickListener(this);
        iv_qq.setOnClickListener(this);
        iv_share_link.setOnClickListener(this);
        bt_start.setOnClickListener(this);
        tv_logoff.setOnClickListener(this);
        et_title.setText(title);
        tv_count.setText(et_title.getText().length() + "/20");
        et_title.setSelection(et_title.length());
        et_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_count.setText(et_title.getText().length() + "/20");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        resetDefinition(PolyvRTMPDefinition.GAO_QING);
        resetMode(PolyvRTMPOrientation.SCREEN_ORIENTATION_LANDSCAPE);
        iv_public.setSelected(true);

        final String logoPath = getSharedPreferences(LOGO_NAME, MODE_PRIVATE).getString(LOGO_NAME, null);
        if (!TextUtils.isEmpty(logoPath) && new File(logoPath).exists()) {
            iv_logo.setImageDrawable(Drawable.createFromPath(logoPath), true);
            canShowDeleteLogoDialog(logoPath);
        }
    }

    private void canShowDeleteLogoDialog(final String logoPath) {
        iv_logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(v.getContext()).setTitle("提示")
                        .setMessage("是否要删除logo")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getSharedPreferences(LOGO_NAME, MODE_PRIVATE).edit().remove(LOGO_NAME).commit();
                                new File(logoPath).delete();
                                iv_logo.setImageResource(R.drawable.polyv_rtmp_setting_iv_pressed);
                                iv_logo.changeDrawColor(false);
                                iv_logo.setOnLongClickListener(null);
                            }
                        })
                        .setNegativeButton("取消", null).show();
                return true;
            }
        });
    }

    private void resetMode(int orientation) {
        switch (orientation) {
            case PolyvRTMPOrientation.SCREEN_ORIENTATION_PORTRAIT:
                iv_landscape.setVisibility(View.GONE);
                iv_portrait.setVisibility(View.VISIBLE);
                break;
            case PolyvRTMPOrientation.SCREEN_ORIENTATION_LANDSCAPE:
                iv_portrait.setVisibility(View.GONE);
                iv_landscape.setVisibility(View.VISIBLE);
                break;
        }

        mOrientation = orientation;
    }

    private void resetDefinition(int definition) {
        switch (definition) {
            case PolyvRTMPDefinition.CHAO_QING:
                iv_hd.setVisibility(View.GONE);
                iv_sd.setVisibility(View.GONE);
                iv_sc.setVisibility(View.VISIBLE);
                break;
            case PolyvRTMPDefinition.GAO_QING:
                iv_sc.setVisibility(View.GONE);
                iv_sd.setVisibility(View.GONE);
                iv_hd.setVisibility(View.VISIBLE);
                break;
            case PolyvRTMPDefinition.LIU_CHANGE:
                iv_sc.setVisibility(View.GONE);
                iv_hd.setVisibility(View.GONE);
                iv_sd.setVisibility(View.VISIBLE);
                break;
        }

        mDefinition = definition;
    }

    private void resetPermission(final int permission) {
        code = null;
        price = 0;
        switch (permission) {
            case 0:
                iv_password.setSelected(false);
                iv_pay.setSelected(false);
                iv_public.setSelected(true);
                authType = PolyvAuthTypeSetting.AUTHTYPE_NONE;
                break;
            case 1:
                final EditText editText = new EditText(this);
                editText.setKeyListener(DigitsKeyListener.getInstance("123456789"));
                new AlertDialog.Builder(this).setTitle("请输入密码")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(editText.getText().toString())) {
                                    Toast.makeText(PolyvSettingActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                iv_public.setSelected(false);
                                iv_pay.setSelected(false);
                                iv_password.setSelected(true);
                                code = editText.getText().toString();
                                authType = PolyvAuthTypeSetting.AUTHTYPE_CODE;
                            }
                        }).setNegativeButton("取消", null).show();
                break;
            case 2:
                final EditText pEditText = new EditText(this);
                pEditText.setKeyListener(DigitsKeyListener.getInstance("123456789."));
                new AlertDialog.Builder(this).setTitle("请输入价格")
                        .setView(pEditText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(pEditText.getText().toString())) {
                                    Toast.makeText(PolyvSettingActivity.this, "价格不能为空", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                iv_public.setSelected(false);
                                iv_password.setSelected(false);
                                iv_pay.setSelected(true);
                                price = Double.parseDouble(pEditText.getText().toString());
                                authType = PolyvAuthTypeSetting.AUTHTYPE_PAY;
                            }
                        }).setNegativeButton("取消", null).show();
                break;
        }
    }

    private void selectLogo() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(this, "Cannot retrieve selected image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }

        if (requestCode == myRequestCode && resultCode == Activity.RESULT_CANCELED) {
            permissionManager.request();
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = LOGO_NAME + ".jpg";
        int w = Math.max(300, iv_logo.getMeasuredWidth());
        int h = Math.max(300, iv_logo.getMeasuredHeight());
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getDiskCacheDir(this), destinationFileName)))
                .withMaxResultSize(w, h)
                .withAspectRatio(1, 1);
        uCrop.start(this);
    }

    private File getDiskCacheDir(Context context) {
        return (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) ? context.getExternalCacheDir() : context.getCacheDir();
    }

    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unexpected error", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            iv_logo.setImageDrawable(Drawable.createFromPath(resultUri.getPath()), true);
            getSharedPreferences(LOGO_NAME, MODE_PRIVATE).edit().putString(LOGO_NAME, resultUri.getPath()).commit();
            canShowDeleteLogoDialog(resultUri.getPath());
        } else {
            Toast.makeText(this, "Cannot retrieve cropped image", Toast.LENGTH_SHORT).show();
        }
    }

    private void start() {
        polyvPermission.applyPermission(PolyvSettingActivity.this, PolyvPermission.OperationType.play);
    }

    private void logoff() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_portrait:
                resetMode(PolyvRTMPOrientation.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.rl_landscape:
                resetMode(PolyvRTMPOrientation.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case R.id.rl_sc:
                resetDefinition(PolyvRTMPDefinition.CHAO_QING);
                break;
            case R.id.rl_hd:
                resetDefinition(PolyvRTMPDefinition.GAO_QING);
                break;
            case R.id.rl_sd:
                resetDefinition(PolyvRTMPDefinition.LIU_CHANGE);
                break;
            case R.id.rl_public:
                resetPermission(0);
                break;
            case R.id.rl_password:
                // 待完善
//                resetPermission(1);
                break;
            case R.id.rl_pay:
                // 待完善
//                resetPermission(2);
                break;
            case R.id.iv_logo:
                // 待完善
//                selectLogo();
                break;
            case R.id.bt_start:
                start();
                break;
            case R.id.tv_logoff:
                logoff();
                break;
            case R.id.iv_wechat:
                break;
            case R.id.iv_moments:
                break;
            case R.id.iv_weibo:
                break;
            case R.id.iv_qq:
                break;
            case R.id.iv_share_link:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void gotoPlay() {
        String title = et_title.getText().toString();
        if (title.trim().length() == 0) {
            Toast.makeText(this, "活动标题不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        // 更新活动标题
        new PolyvTitleUpdate().updateTitle(mChannelId, title, null);
        // 待完善
//        new PolyvAuthTypeSetting().setAuthType(mChannelId, authType, code, price, null);
        Intent intent = new Intent(PolyvSettingActivity.this, PolyvMainActivity.class);
        intent.putExtra("channelId", mChannelId);
        intent.putExtra("orientation", mOrientation);
        intent.putExtra("definition", mDefinition);
        intent.putExtra("avatarUrl", getIntent().getStringExtra("avatarUrl"));
        startActivity(intent);
    }

    /**
     * This is the method that is hit after the user accepts/declines the
     * permission you requested. For the purpose of this example I am showing a "success" header
     * when the user accepts the permission and a snackbar when the user declines it.  In your application
     * you will want to handle the accept/decline in a way that makes sense.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == myRequestCode) {
            permissionManager.onPermissionResult(permissions, grantResults);
            return;
        }

        if (polyvPermission.operationHasPermission(requestCode)) {
            gotoPlay();
        } else {
            polyvPermission.makePostRequestSnack();
        }
    }
}
