package com.easefun.polyvrtmp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.util.PolyvScreenUtils;
import com.easefun.polyvsdk.rtmp.chat.PolyvChatManager;
import com.easefun.polyvsdk.rtmp.core.login.IPolyvRTMPLoginListener;
import com.easefun.polyvsdk.rtmp.core.login.PolyvRTMPLoginErrorReason;
import com.easefun.polyvsdk.rtmp.core.login.PolyvRTMPLoginVerify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PolyvLoginActivity extends Activity {

    private static final String TAG = PolyvLoginActivity.class.getSimpleName();

    private AutoCompleteTextView channelIdACTV = null;
    private EditText passwordET = null;
    private CheckBox isSavePasswordCB = null;
    private ProgressDialog progress = null;

    private static final String LAST_ACCOUNT_ID_KEY = "last_account_id";
    private static final String ACCOUNT_ID_LIST_KEY = "account_id_list";
    private static final String CHECK_SELECTED_PREFIX_KEY = "check_selected_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_rtmp_activity_login);

        channelIdACTV = (AutoCompleteTextView) findViewById(R.id.channel_id);
        channelIdACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                String channelId = channelIdACTV.getText().toString();
                if (sharedPreferences.contains(channelId)) {
                    passwordET.setText(sharedPreferences.getString(channelId, ""));
                } else {
                    passwordET.setText("");
                }

                if (sharedPreferences.contains(getCheckSelectKey(channelId))) {
                    isSavePasswordCB.setChecked(true);
                } else {
                    isSavePasswordCB.setChecked(false);
                }
            }
        });

        passwordET = (EditText) findViewById(R.id.password);
        isSavePasswordCB = (CheckBox) findViewById(R.id.is_save_password);
        Button signInBtn = (Button) findViewById(R.id.sign_in);
        progress = new ProgressDialog(this);
        progress.setMessage("正在登录中，请稍等...");
        progress.setCanceledOnTouchOutside(false);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String channelId = channelIdACTV.getText().toString();
                final String password = passwordET.getText().toString();
                if (TextUtils.isEmpty(channelId)) {
                    Toast.makeText(PolyvLoginActivity.this, "请输入直播频道ID", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(PolyvLoginActivity.this, "请输入直播密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                progress.show();
                PolyvRTMPLoginVerify.verify(channelId, password, new IPolyvRTMPLoginListener() {
                    @Override
                    public void onError(PolyvRTMPLoginErrorReason errorReason) {
                        if (!progress.isShowing())
                            return;
                        progress.dismiss();
                        switch (errorReason.getType()) {
                            case PolyvRTMPLoginErrorReason.SERVER_STATUS_EMPTY:
                                Toast.makeText(PolyvLoginActivity.this, "服务返回状态为空", Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvRTMPLoginErrorReason.SERVER_STATUS_FAIL:
                                Toast.makeText(PolyvLoginActivity.this, errorReason.getMsg(), Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvRTMPLoginErrorReason.NETWORK_DENIED:
                                Toast.makeText(PolyvLoginActivity.this, "无法连接网络", Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvRTMPLoginErrorReason.DATA_ERROR:
                                Toast.makeText(PolyvLoginActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvRTMPLoginErrorReason.CHANNEL_ID_EMPTY:
                                Toast.makeText(PolyvLoginActivity.this, "请输入直播频道ID", Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvRTMPLoginErrorReason.PASSWORD_EMPTY:
                                Toast.makeText(PolyvLoginActivity.this, "请输入直播密码", Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvRTMPLoginErrorReason.REQUEST_SERVER_FAIL:
                                Toast.makeText(PolyvLoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                                break;
                            case PolyvRTMPLoginErrorReason.LIVE_SCENE_INCORRECT:
                                Toast.makeText(PolyvLoginActivity.this, "请使用普通直播场景频道登录", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onSuccess(String[] preview_nickname_avatar) {
                        if (!progress.isShowing())
                            return;
                        PolyvScreenUtils.setDecorVisible(PolyvLoginActivity.this);
                        progress.dismiss();

                        //登陆成功初始化聊天室配置
                        PolyvChatManager.initConfig(PolyvRTMPLoginVerify.getPolyvPublishVO().getAppId(), PolyvRTMPLoginVerify.getPolyvPublishVO().getAppSecret());

                        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(LAST_ACCOUNT_ID_KEY, channelId);
                        editor.apply();

                        Set<String> accountIdList = sharedPreferences.getStringSet(ACCOUNT_ID_LIST_KEY, new HashSet<String>());
                        if (!accountIdList.contains(channelId)) {
                            accountIdList.add(channelId);
                            editor = sharedPreferences.edit();
                            editor.putStringSet(ACCOUNT_ID_LIST_KEY, accountIdList);
                            editor.commit();
                        }

                        if (!isSavePasswordCB.isChecked()) {
                            if (sharedPreferences.contains(channelId)) {
                                editor = sharedPreferences.edit();
                                editor.remove(channelId);
                                editor.commit();
                            }

                            if (sharedPreferences.contains(getCheckSelectKey(channelId))) {
                                editor = sharedPreferences.edit();
                                editor.remove(getCheckSelectKey(channelId));
                                editor.commit();
                            }
                        } else {
                            editor = sharedPreferences.edit();
                            editor.putString(channelId, password);
                            editor.commit();

                            if (!sharedPreferences.contains(getCheckSelectKey(channelId))) {
                                editor = sharedPreferences.edit();
                                editor.putBoolean(getCheckSelectKey(channelId), true);
                                editor.commit();
                            }
                        }
                        Intent intent = new Intent(PolyvLoginActivity.this, PolyvSettingActivity.class);
                        intent.putExtra("channelId", channelId);
                        intent.putExtra("title", preview_nickname_avatar[1]);
                        intent.putExtra("avatarUrl", preview_nickname_avatar[2]);
                        startActivity(intent);
                    }
                }, getApplicationContext());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PolyvScreenUtils.hideStatusBar(this);
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Set<String> accountIdList = sharedPreferences.getStringSet(ACCOUNT_ID_LIST_KEY, new HashSet<String>());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(accountIdList));
        channelIdACTV.setAdapter(arrayAdapter);

        if (sharedPreferences.contains(LAST_ACCOUNT_ID_KEY)) {
            String accountId = sharedPreferences.getString(LAST_ACCOUNT_ID_KEY, "");
            channelIdACTV.setText(accountId);
            channelIdACTV.setSelection(channelIdACTV.length());
            if (sharedPreferences.contains(accountId)) {
                passwordET.setText(sharedPreferences.getString(accountId, ""));
            }

            if (sharedPreferences.contains(getCheckSelectKey(accountId))) {
                isSavePasswordCB.setChecked(true);
            }
        }
    }

    private String getCheckSelectKey(String accountId) {
        return CHECK_SELECTED_PREFIX_KEY + accountId;
    }

}
