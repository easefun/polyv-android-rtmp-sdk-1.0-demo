package com.easefun.polyvrtmp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.easefun.polyvrtmp.R;

public class PolyvLaunchActivity extends Activity {
    private static final int LAUCHFINISH = 12;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LAUCHFINISH:
                    startActivity(new Intent(PolyvLaunchActivity.this, PolyvLoginActivity.class));
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyv_rtmp_activity_launch);
        handler.sendEmptyMessageDelayed(LAUCHFINISH, 800);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(LAUCHFINISH);
    }
}
