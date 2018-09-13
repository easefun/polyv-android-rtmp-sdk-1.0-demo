package com.easefun.polyvrtmp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easefun.polyvrtmp.R;

import java.util.List;

public class FinishRecyclerViewAdapter extends AbsRecyclerViewAdapter {
    private static final String SPACE_TWO = "  ";
    private static final String SPACE_THREE = ".   ";
    private List<String> msgs;
    private RecyclerView rv_chat;

    public FinishRecyclerViewAdapter(RecyclerView recyclerView, List<String> msgs) {
        super(recyclerView);
        this.msgs = msgs;
        this.rv_chat = recyclerView;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.polyv_rtmp_recyclerview_item_finish, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            if (msgs.get(position).equals("")) {
                viewHolder.rl_parent.setVisibility(View.GONE);
            } else {
                viewHolder.rl_parent.setVisibility(View.VISIBLE);
                viewHolder.tv_name.setText(msgs.get(position));
            }
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    private class ItemViewHolder extends ClickableViewHolder {
        private RelativeLayout rl_parent;
        private TextView tv_name, tv_money;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_name = $(R.id.tv_name);
            tv_money = $(R.id.tv_money);
            rl_parent = $(R.id.rl_parent);
        }
    }
}
