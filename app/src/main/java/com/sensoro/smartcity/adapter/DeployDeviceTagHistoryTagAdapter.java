package com.sensoro.smartcity.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.common.callback.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployDeviceTagHistoryTagAdapter extends RecyclerView.Adapter<DeployDeviceTagHistoryTagAdapter.DeployDeviceTagHistoryTagHolder> {
    private final Context mContext;
    private final List<String> mList = new ArrayList<>();
    private RecycleViewItemClickListener itemClickListener;

    public DeployDeviceTagHistoryTagAdapter(Context context) {
        mContext = context;
    }

    public List<String> getSearchHistoryList() {
        return mList;
    }

    public void setRecycleViewItemClickListener(RecycleViewItemClickListener listener) {
        itemClickListener = listener;
    }

    @Override
    public DeployDeviceTagHistoryTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_deploy_device_history_tag, parent, false);
        return new DeployDeviceTagHistoryTagHolder(view);
    }

    @Override
    public void onBindViewHolder(DeployDeviceTagHistoryTagHolder holder, final int position) {
        holder.itemAdapterTv.setText(mList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    public void updateSearchHistoryAdapter(List<String> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class DeployDeviceTagHistoryTagHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_tv)
        TextView itemAdapterTv;

        DeployDeviceTagHistoryTagHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
