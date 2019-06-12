package com.easefun.polyvrtmp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.easefun.polyvrtmp.R;
import com.easefun.polyvrtmp.activity.PolyvFinishActivity;
import com.easefun.polyvrtmp.adapter.AvatarRecyclerViewAdapter;
import com.easefun.polyvrtmp.adapter.ChatRecyclerViewAdapter;
import com.easefun.polyvrtmp.util.PolyvBitmapUtils;
import com.easefun.polyvsdk.rtmp.chat.PolyvChatManager;
import com.easefun.polyvsdk.rtmp.chat.PolyvChatMessage;
import com.easefun.polyvsdk.rtmp.core.userinterface.PolyvIListUsers;
import com.easefun.polyvsdk.rtmp.core.userinterface.entity.PolyvOnlineUsers;
import com.easefun.polyvsdk.rtmp.core.userinterface.entity.PolyvUser;
import com.easefun.polyvsdk.rtmp.core.userinterface.listener.OnlineUpdateListener;
import com.easefun.polyvsdk.rtmp.core.video.PolyvRTMPView;
import com.easefun.polyvsdk.rtmp.core.video.listener.IPolyvRTMPOnTakePictureListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 主视图的Fragment
 */
public class PolyvMainFragment extends Fragment implements View.OnClickListener {
    public static final String ACTION_RECEIVE_SENDMESSAGE = "polyv.fragment.main.receiver";
    private static final int DISCONNECT = 5;
    private static final int RECEIVEMESSAGE = 6;
    private static final int LOGINING = 12;
    private static final int LOGINSUCCESS = 13;
    private static final int RECONNECTING = 19;
    private static final int RECONNECTSUCCESS = 30;
    private static final int UPDATEONLINE = 35;
    private View view;
    private ImageView iv_logo, iv_close, iv_chat, iv_micro, iv_beauty, iv_more, iv_share, iv_switchcamera, iv_flashlight;
    // 右下角的"更多"按钮
    private LinearLayout ll_right_bottom;
    // 在线人数，时间，速度
    private TextView tv_online, tv_time, tv_kb;
    // 聊天列表，头像列表
    private RecyclerView rv_chat, rv_avatar;
    // 聊天适配器
    private ChatRecyclerViewAdapter rv_chat_adatper;
    // 头像适配器
    private AvatarRecyclerViewAdapter rv_avatar_adapter;
    // 聊天信息集合
    private LinkedList<PolyvChatMessage> ls_messages;
    // 头像实体集合
    private ArrayList<PolyvUser> avatar_urls;
    // 聊天室管理类
    private PolyvChatManager chatManager;
    // 昵称，频道id，用户id，用户头像地址
    private String nickName, channelId, userId, avatarUrl;
    // 弹幕开关状态
    private String toggle = PolyvDanmakuFragment.ON;
    // 总观看人数
    private int totalWatcher;
    // 在线人数接口
    private PolyvIListUsers listUsersInterface;
    // 聊天信息/弹幕开关接收者
    private ReceiveSendMessageBroadcastReceiver receiver;
    // 弹幕Fragment
    private PolyvDanmakuFragment danmakuFragment;
    // 是否结束对话框Fragment
    private PolyvAlertDialogFragment alertDialogFragment;
    private PolyvRTMPView polyvRTMPView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (PolyvMainFragment.this.getContext() == null)
                return;
            switch (msg.what) {
                case RECEIVEMESSAGE:
                    PolyvChatMessage chatMessage = (PolyvChatMessage) msg.obj;
                    if (chatMessage.getChatType() == PolyvChatMessage.CHATTYPE_RECEIVE) {
                        rv_chat_adatper.insert(chatMessage);
                        if (danmakuFragment != null && chatMessage.getValues() != null)
                            danmakuFragment.addDanmaku(Html.fromHtml(chatMessage.getValues()[0]));
                    } else if (chatMessage.getChatType() == PolyvChatMessage.CHATTYPE_RECEIVE_NOTICE) {
                        switch (chatMessage.getEvent()) {
                            // 用户被踢，不能发送信息，再次连接聊天室可恢复
                            case PolyvChatMessage.EVENT_KICK:
                                String nick = chatMessage.getUser().getNick();
                                if (chatMessage.getUser().getUserId().equals(userId))
                                    nick = nick + "(自己)";
                                // 这里需要自定义显示的信息
                                chatMessage.setValues(new String[]{nick + "被踢"});
                                break;
                            // 用户被禁言，不能接收或发送信息，不能再次连接聊天室，需要在后台恢复
                            case PolyvChatMessage.EVENT_SHIELD:
                                String nick2 = chatMessage.getUser().getNick();
                                if (chatMessage.getUser().getUserId().equals(userId))
                                    nick2 = nick2 + "(自己)";
                                chatMessage.setValues(new String[]{nick2 + "被禁言"});
                                break;
                            // 聊天室关闭时，不能接收或发送信息
                            case PolyvChatMessage.EVENT_CLOSEROOM:
                                boolean isClose = chatMessage.getValue().isClosed();
                                if (isClose)
                                    chatMessage.setValues(new String[]{"聊天室关闭"});
                                else
                                    chatMessage.setValues(new String[]{"聊天室开启"});
                                break;
                            // 公告
                            case PolyvChatMessage.EVENT_GONGGAO:
                                chatMessage.setValues(new String[]{"公告 " + chatMessage.getContent()});
                                break;
                            // 登录
                            case PolyvChatMessage.EVENT_LOGIN:
                                if (totalWatcher == 0) {
                                    totalWatcher = chatMessage.getOnlineUserNumber();
                                } else {
                                    if (chatMessage.getUser() != null && !userId.equals(chatMessage.getUser().getUserId())) {
                                        totalWatcher = Math.max(chatMessage.getOnlineUserNumber(), ++totalWatcher);
                                    }
                                }
                                break;
                        }
                        if (danmakuFragment != null && chatMessage.getValues() != null)
                            danmakuFragment.addDanmaku(chatMessage.getValues()[0]);
                    }
                    break;
                case DISCONNECT:
                    Toast.makeText(PolyvMainFragment.this.getContext(), "连接聊天室失败(" + ((PolyvChatManager.ConnectStatus) msg.obj).getDescribe() + ")", Toast.LENGTH_SHORT).show();
                    break;
                case LOGINING:
                    break;
                case LOGINSUCCESS:
                    Toast.makeText(PolyvMainFragment.this.getContext(), "登录聊天室成功", Toast.LENGTH_SHORT).show();
                    break;
                case RECONNECTING:
                    break;
                case RECONNECTSUCCESS:
                    Toast.makeText(PolyvMainFragment.this.getContext(), "重连聊天室成功", Toast.LENGTH_SHORT).show();
                    break;
                case UPDATEONLINE:
                    PolyvOnlineUsers onlineUsers = (PolyvOnlineUsers) msg.obj;
                    List<PolyvUser> users = onlineUsers.getUserList();
                    int count = onlineUsers.getCount();
                    if (count > 0) {
                        int oldCount = avatar_urls.size();
                        if (oldCount == 0) {
                            avatar_urls.addAll(users);
                            tv_online.setText(count + "人在线");
                            rv_avatar_adapter.notifyDataSetChanged();
                            return;
                        }
                        int j = 0;
                        int k = 0;
                        // 旧需要移除的索引集合
                        List<Integer> or_pos = new ArrayList<>();
                        // 新需要移除的索引集合
                        List<Integer> nr_pos = new ArrayList<>();
                        for (int i = 0; i < avatar_urls.size(); i++) {
                            if (i + j < users.size())
                                if (!avatar_urls.get(i).getUserId().equals(users.get(i + j).getUserId())) {
                                    or_pos.add(i + j);
                                    j--;
                                } else {
                                    nr_pos.add(i + j + k);
                                    k--;
                                }
                            else {
                                or_pos.add(i + j);
                                j--;
                            }
                        }
                        if (or_pos.size() > 0)
                            for (int i = 0; i < or_pos.size(); i++) {
                                avatar_urls.remove((int) or_pos.get(i));
                                rv_avatar_adapter.notifyItemRemoved((int) or_pos.get(i));
                            }
                        if (nr_pos.size() > 0 && (or_pos.size() > 0 || count != oldCount)) {
                            for (int i = 0; i < nr_pos.size(); i++)
                                users.remove((int) nr_pos.get(i));
                            int tempSize = avatar_urls.size();
                            avatar_urls.addAll(users);
                            tv_online.setText(count + "人在线");
                            rv_avatar_adapter.notifyItemRangeInserted(tempSize, users.size());
                        }
                    } else {
                        tv_online.setText(0 + "人在线");
                        avatar_urls.removeAll(avatar_urls);
                        rv_avatar_adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    // 弹幕开关/聊天信息广播接收者
    private class ReceiveSendMessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && ACTION_RECEIVE_SENDMESSAGE.equals(intent.getAction())) {
                String message = intent.getStringExtra("message");
                String eToggle = intent.getStringExtra("toggle");
                if (eToggle != null) {
                    toggle = eToggle;
                    danmakuFragment.toggle(toggle);
                }
                if (message != null) {
                    PolyvChatMessage chatMessage = new PolyvChatMessage(message);
                    if (chatManager == null)
                        chatManager = new PolyvChatManager();
                    boolean result = chatManager.sendChatMsg(chatMessage);
                    if (result)
                        danmakuFragment.addDanmaku(message, avatarUrl);
                    else
                        Toast.makeText(getContext(), "发送失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.polyv_rtmp_fragment_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findId();
        initView();
        addFragmnet();
        registerReceiver();
    }

    //注册广播接收者
    private void registerReceiver() {
        receiver = new ReceiveSendMessageBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_SENDMESSAGE);
        getActivity().registerReceiver(receiver, filter);
    }

    //添加Fragment
    private void addFragmnet() {
        danmakuFragment = new PolyvDanmakuFragment();
        alertDialogFragment = new PolyvAlertDialogFragment();
		getChildFragmentManager().beginTransaction()
                .add(R.id.fl_danmaku, danmakuFragment, "danmakuFragment")
                .commit();
    }

    private void findId() {
        tv_online = (TextView) view.findViewById(R.id.tv_online);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_kb = (TextView) view.findViewById(R.id.tv_kb);
        iv_logo = (ImageView) view.findViewById(R.id.iv_logo);
        iv_close = (ImageView) view.findViewById(R.id.iv_close);
        iv_chat = (ImageView) view.findViewById(R.id.iv_chat);
        iv_micro = (ImageView) view.findViewById(R.id.iv_micro);
        iv_beauty = (ImageView) view.findViewById(R.id.iv_beauty);
        iv_more = (ImageView) view.findViewById(R.id.iv_more);
        iv_share = (ImageView) view.findViewById(R.id.iv_share);
        iv_switchcamera = (ImageView) view.findViewById(R.id.iv_switchcamera);
        iv_flashlight = (ImageView) view.findViewById(R.id.iv_flashlight);
        ll_right_bottom = (LinearLayout) view.findViewById(R.id.ll_right_bottom);
        rv_chat = (RecyclerView) view.findViewById(R.id.rv_chat);
        rv_avatar = (RecyclerView) view.findViewById(R.id.rv_avatar);
    }

    public TextView getTimeView() {
        return tv_time;
    }

    private void initView() {
        iv_close.setOnClickListener(this);
        iv_chat.setOnClickListener(this);
        iv_micro.setOnClickListener(this);
        iv_beauty.setOnClickListener(this);
        iv_more.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        iv_switchcamera.setOnClickListener(this);
        iv_flashlight.setOnClickListener(this);
        Glide.with(this).load(avatarUrl = getActivity().getIntent().getStringExtra("avatarUrl")).dontAnimate()
                .placeholder(R.drawable.polyv_rtmp_default_logo).error(R.drawable.polyv_rtmp_default_logo).into(iv_logo);
        tv_online.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = tv_online.getMeasuredHeight();
                iv_logo.setLayoutParams(new RelativeLayout.LayoutParams(height, height));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    tv_online.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    tv_online.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        ls_messages = new LinkedList<>();
        rv_chat_adatper = new ChatRecyclerViewAdapter(rv_chat, ls_messages);
        rv_chat.setHasFixedSize(true);
        rv_chat.setNestedScrollingEnabled(false);
        rv_chat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        rv_chat.setAdapter(rv_chat_adatper);

        avatar_urls = new ArrayList<>();
        rv_avatar_adapter = new AvatarRecyclerViewAdapter(rv_avatar, avatar_urls);
        rv_avatar.setHasFixedSize(true);
        rv_avatar.setNestedScrollingEnabled(false);
        rv_avatar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv_avatar.setAdapter(rv_avatar_adapter);
    }

    // 登录聊天室
    public void loginChatRoom(String userId, String channelId, String nickName) {
        this.userId = userId;
        this.nickName = nickName;
        this.channelId = channelId;
        if (chatManager == null)
            chatManager = new PolyvChatManager();
        chatManager.setOnChatManagerListener(new PolyvChatManager.ChatManagerListener() {
            @Override
            public void connectStatus(PolyvChatManager.ConnectStatus connect_status) {
                switch (connect_status) {
                    case DISCONNECT:
                        handler.sendMessage(handler.obtainMessage(DISCONNECT, connect_status.DISCONNECT));
                        break;
                    case LOGINING:
                        handler.sendEmptyMessage(LOGINING);
                        break;
                    case LOGINSUCCESS:
                        handler.sendEmptyMessage(LOGINSUCCESS);
                        break;
                    case RECONNECTING:
                        handler.sendEmptyMessage(RECONNECTING);
                        break;
                    case RECONNECTSUCCESS:
                        handler.sendEmptyMessage(RECONNECTSUCCESS);
                        break;
                }
            }

            @Override
            public void receiveChatMessage(PolyvChatMessage chatMessage) {
                Message message = handler.obtainMessage();
                message.obj = chatMessage;
                message.what = RECEIVEMESSAGE;
                handler.sendMessage(message);
            }
        });
        chatManager.login(userId, channelId, nickName);
        // 获取在线(登录聊天室的)人数
        getOnlineNumber();
    }

    private void getOnlineNumber() {
        // 获取在线人数
        listUsersInterface = new PolyvIListUsers(this.channelId, this.userId, 1, 88888);
        listUsersInterface.getOnlineUsers(6000, new OnlineUpdateListener() {
            @Override
            public void onUserList(PolyvOnlineUsers userList, int totalWatcher) {
                Message message = handler.obtainMessage();
                message.obj = userList;
                message.what = UPDATEONLINE;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatManager != null)
            chatManager.disconnect();
        if (listUsersInterface != null)
            listUsersInterface.destroy();
        getActivity().unregisterReceiver(receiver);
    }

    // 子Fragment是否处于显示状态/结束对话框是否是隐藏状态
    public boolean isShowChild() {
        return !alertDialogFragment.isVisible();
    }

    //隐藏子Fragment/弹出结束对话框
    public void hideChild() {
        showAlertDialogFragment();
    }

    private void showAlertDialogFragment() {
        alertDialogFragment.show(getFragmentManager(), "alertDialogFragment", new PolyvAlertDialogFragment.DialogFragmentClickImpl() {
            @Override
            public void doPositiveClick() {
                //截图
                polyvRTMPView.setOnTakePictureListener(new IPolyvRTMPOnTakePictureListener() {
                    @Override
                    public void onTakePicture(@Nullable Bitmap bitmap) {
                        String bitmapPath = PolyvBitmapUtils.saveBitmap(bitmap, getContext());
                        Intent intent = new Intent(getActivity(), PolyvFinishActivity.class);
                        intent.putExtra("time", tv_time.getText());
                        intent.putExtra("totalWatcher", totalWatcher);
                        intent.putExtra("bitmapPath", bitmapPath);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                polyvRTMPView.takePicture();
            }

            @Override
            public void doNegativeClick() {
            }
        });
    }

    private void resetMoreLayout() {
        if (ll_right_bottom.getVisibility() == View.VISIBLE) {
            ll_right_bottom.setVisibility(View.INVISIBLE);
        } else {
            ll_right_bottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_more:
                resetMoreLayout();
                break;
            case R.id.iv_chat:
                iv_chat.setSelected(!iv_chat.isSelected());
                if (danmakuFragment != null) {
                    danmakuFragment.toggle(iv_chat.isSelected() ? PolyvDanmakuFragment.OFF : PolyvDanmakuFragment.ON);
                }
//                Intent intent = new Intent(getActivity(), PolyvChatActivity.class);
//                intent.putExtra("toggle", toggle);
//                startActivity(intent);
//                getActivity().overridePendingTransition(0, 0);
                break;
            case R.id.iv_close:
                showAlertDialogFragment();
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_switchcamera:
                polyvRTMPView.toggleFrontBackCamera();
                if (!iv_switchcamera.isSelected()) {
                    iv_switchcamera.setSelected(true);
                    iv_flashlight.setSelected(false);
                    iv_flashlight.setEnabled(false);
                } else {
                    iv_switchcamera.setSelected(false);
                    iv_flashlight.setEnabled(true);
                }
                break;
            case R.id.iv_micro:
                polyvRTMPView.mute(!polyvRTMPView.isMute());
                if (!iv_micro.isSelected())
                    iv_micro.setSelected(true);
                else
                    iv_micro.setSelected(false);
                break;
            case R.id.iv_flashlight:
                polyvRTMPView.switchTorch();
                if (!iv_flashlight.isSelected())
                    iv_flashlight.setSelected(true);
                else
                    iv_flashlight.setSelected(false);
                break;
            case R.id.iv_beauty:
                polyvRTMPView.switchEffect();
                if (!iv_beauty.isSelected())
                    iv_beauty.setSelected(true);
                else
                    iv_beauty.setSelected(false);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        iv_flashlight.setSelected(false);
    }

    public void setPolyvRTMPView(PolyvRTMPView polyvRTMPView) {
        this.polyvRTMPView = polyvRTMPView;
    }
}
