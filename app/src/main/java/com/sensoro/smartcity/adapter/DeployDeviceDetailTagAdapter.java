package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployDeviceDetailTagAdapter extends RecyclerView.
        Adapter<DeployDeviceDetailTagAdapter.DeployDeviceDetailTagHolder> {
    private final Context mContext;
    private final List<String> tags = new ArrayList<>();

    public DeployDeviceDetailTagAdapter(Context context) {
        mContext = context;
    }

    public void updateTags(List<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        notifyDataSetChanged();
    }

    @Override
    public DeployDeviceDetailTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_text_view, parent, false);

        return new DeployDeviceDetailTagHolder(view);
    }

    @Override
    public void onBindViewHolder(DeployDeviceDetailTagHolder holder, int position) {
        holder.itemAdapterTv.setText(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    class DeployDeviceDetailTagHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_tv)
        TextView itemAdapterTv;

        DeployDeviceDetailTagHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
