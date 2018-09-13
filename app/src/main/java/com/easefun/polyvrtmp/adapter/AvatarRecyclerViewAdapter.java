package com.easefun.polyvrtmp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.easefun.polyvrtmp.R;
import com.easefun.polyvsdk.rtmp.core.userinterface.entity.PolyvUser;

import java.util.List;

public class AvatarRecyclerViewAdapter extends AbsRecyclerViewAdapter {
    private List<PolyvUser> urls;

    public AvatarRecyclerViewAdapter(RecyclerView recyclerView, List<PolyvUser> urls) {
        super(recyclerView);
        this.urls = urls;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.polyv_rtmp_recyclerview_item_mian, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            Glide.with(getContext())
                    .load(urls.get(position).getPic())
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .placeholder(R.drawable.polyv_rtmp_missing_face)
                    .error(R.drawable.polyv_rtmp_missing_face)
                    .into(viewHolder.iv_avatar);
        }
        super.onBindViewHolder(holder, position);
    }

    private class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {
        private ImageView iv_avatar;

        public ItemViewHolder(View itemView) {
            super(itemView);
            iv_avatar = $(R.id.iv_avatar);
        }
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }
}
