package com.easefun.polyvrtmp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.fragment.PolyvDanmakuFragment;
import com.easefun.polyvrtmp.fragment.PolyvMainFragment;

/**
 * 发送聊天信息及弹幕开关Activity
 */
public class PolyvChatActivity extends Activity implements View.OnClickListener {
    // 弹幕开关按钮
    private ImageView iv_danmakuswitch;
    // 发送按钮
    private TextView tv_send;
    // 信息编辑框
    private EditText et_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_rtmp_fragment_chat);
        findId();
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void findId() {
        iv_danmakuswitch = (ImageView) findViewById(R.id.iv_danmakuswitch);
        tv_send = (TextView) findViewById(R.id.tv_send);
        et_message = (EditText) findViewById(R.id.et_message);
    }

    private void initView() {
        tv_send.setOnClickListener(this);
        iv_danmakuswitch.setOnClickListener(this);
        String toggle = getIntent().getStringExtra("toggle");
        if (!(toggle == null || toggle.equals(PolyvDanmakuFragment.ON)))
            iv_danmakuswitch.setSelected(true);
    }

    private void toggleDanmaku() {
        Intent intent = new Intent(PolyvMainFragment.ACTION_RECEIVE_SENDMESSAGE);
        if (!iv_danmakuswitch.isSelected()) {
            iv_danmakuswitch.setSelected(true);
            intent.putExtra("toggle", PolyvDanmakuFragment.OFF);
        } else {
            iv_danmakuswitch.setSelected(false);
            intent.putExtra("toggle", PolyvDanmakuFragment.ON);
        }
        sendBroadcast(intent);
    }

    private void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private void sendMessage() {
        String message = et_message.getText().toString();
        if (message.trim().length() == 0) {
            Toast.makeText(this, "发送信息不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(PolyvMainFragment.ACTION_RECEIVE_SENDMESSAGE);
        intent.putExtra("message", message);
        sendBroadcast(intent);
        closeKeybord(et_message, this);
        et_message.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_danmakuswitch:
                toggleDanmaku();
                break;
            case R.id.tv_send:
                sendMessage();
                break;
        }
    }
}
