package com.easefun.polyvrtmp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.adapter.FinishRecyclerViewAdapter;
import com.easefun.polyvrtmp.util.PolyvScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 推流结束后的Activity
 */
public class PolyvFinishActivity extends Activity implements View.OnClickListener {
    // 打赏排行列表控件
    private RecyclerView rv_moneysort;
    // 适配器
    private FinishRecyclerViewAdapter rv_moneysort_adapter;
    // 信息结合
    private List<String> msgs;
    // 父控件
    private RelativeLayout rl_parent;
    // 相机最后一帧的路径
    private String bitmapPath;
    // 返回设置按钮，返回结束按钮
    private Button bt_setting, bt_finish;
    // 时间控件，观看人数控件，收获打赏控件
    private TextView tv_time, tv_watch, tv_money;
    // 时间
    private String time;
    // 总观看人数
    private int totalWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PolyvScreenUtils.isLandscape(this))
            setContentView(R.layout.polyv_rtmp_activity_finish_land);
        else
            setContentView(R.layout.polyv_rtmp_activity_finish_port);
        time = getIntent().getStringExtra("time");
        totalWatcher = getIntent().getIntExtra("totalWatcher", 0);
        bitmapPath = getIntent().getStringExtra("bitmapPath");
        findId();
        initView();
    }

    private void findId() {
        rv_moneysort = (RecyclerView) findViewById(R.id.rv_moneysort);
        bt_setting = (Button) findViewById(R.id.bt_setting);
        bt_finish = (Button) findViewById(R.id.bt_finish);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_watch = (TextView) findViewById(R.id.tv_watch);
        rl_parent = (RelativeLayout) findViewById(R.id.rl_parent);
        // 待完善
        tv_money = (TextView) findViewById(R.id.tv_money);
    }

    private void initView() {
        bt_setting.setOnClickListener(this);
        bt_finish.setOnClickListener(this);
        if (bitmapPath != null)
            rl_parent.setBackground(Drawable.createFromPath(bitmapPath));
        tv_time.setText(toWatchTime(time));
        SpannableStringBuilder span = new SpannableStringBuilder("观看人次    ");
        span.append(String.valueOf(totalWatcher));
        span.setSpan(new RelativeSizeSpan(2f), span.length() - ((totalWatcher + "").length()), span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_watch.setText(span);
        msgs = new ArrayList<>();
        // 待完善
        // 模拟数据
//        for (int i = 0; i < 6; i++)
//            msgs.add("  " + (i + 1) + ".   " + "abc");
//        if (msgs.size() > 1 && PolyvScreenUtils.isLandscape(this)) {
//            while (msgs.size() < 10) {
//                msgs.add("");
//            }
//            Collections.swap(msgs, 1, 2);//2-3
//            Collections.swap(msgs, 1, 4);//3-5
//            Collections.swap(msgs, 3, 6);//4-7
//            Collections.swap(msgs, 1, 8);//5-9
//            Collections.swap(msgs, 5, 1);//6-9
//            Collections.swap(msgs, 5, 7);//8-9
//        }
        rv_moneysort_adapter = new FinishRecyclerViewAdapter(rv_moneysort, msgs);
        rv_moneysort.setHasFixedSize(true);
        rv_moneysort.setNestedScrollingEnabled(false);
        if (PolyvScreenUtils.isLandscape(this)) {
            rv_moneysort.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false));
        } else {
            rv_moneysort.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
        rv_moneysort.setAdapter(rv_moneysort_adapter);
    }

    private CharSequence toWatchTime(String time) {
        String text = "直播时长    ";
        String[] arr = time.split(":");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].startsWith("0"))
                arr[i] = arr[i].substring(1);
        }
        SpannableStringBuilder span = new SpannableStringBuilder();
        span.append(text);
        span.append(arr[0]);
        span.setSpan(new RelativeSizeSpan(1.5f), span.length() - arr[0].length(), span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.append("  小时  ");
        span.append(arr[1]);
        span.setSpan(new RelativeSizeSpan(1.5f), span.length() - arr[1].length(), span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.append("  分钟  ");
        span.append(arr[2]);
        span.setSpan(new RelativeSizeSpan(1.5f), span.length() - arr[2].length(), span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.append("  秒");
        return span;
    }

    private void sendFinishBroadcast() {
        Intent intent = new Intent(PolyvSettingActivity.ACTION_FINISH);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendFinishBroadcast();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_setting:
                finish();
                break;
            case R.id.bt_finish:
                sendFinishBroadcast();
                finish();
                break;
        }
    }
}
