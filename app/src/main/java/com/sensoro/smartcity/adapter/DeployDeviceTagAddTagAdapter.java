package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployDeviceTagAddTagAdapter extends RecyclerView.Adapter<DeployDeviceTagAddTagAdapter.DeployDeviceTagAddTagHolder> {
    private final Context mContext;
    private DeployDeviceTagAddTagItemClickListener listener;
    private final List<String> tags = new ArrayList<>();

    public DeployDeviceTagAddTagAdapter(Context context) {
        mContext = context;
    }

    @Override
    public DeployDeviceTagAddTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_deploy_add_tag, parent, false);

        return new DeployDeviceTagAddTagHolder(view);
    }

    public void setTags(List<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }


    @Override
    public void onBindViewHolder(DeployDeviceTagAddTagHolder holder, final int position) {

        if (tags.size() == position) {
            holder.itemDeployAdapterLlTag.setVisibility(View.GONE);
            holder.itemDeployAdapterImvAddTag.setVisibility(View.VISIBLE);
        } else {
            holder.itemDeployAdapterTvTagName.setText(tags.get(position));
            holder.itemDeployAdapterLlTag.setVisibility(View.VISIBLE);
            holder.itemDeployAdapterImvAddTag.setVisibility(View.GONE);
        }
        holder.itemDeployAdapterImvDeleteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(position);
                }
            }
        });
        holder.itemDeployAdapterImvAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAddClick();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size() + 1;
    }

    public void setDeployDeviceTagAddTagItemClickListener(DeployDeviceTagAddTagItemClickListener listener) {
        this.listener = listener;
    }


    class DeployDeviceTagAddTagHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_deploy_adapter_tv_tag_name)
        TextView itemDeployAdapterTvTagName;
        @BindView(R.id.item_deploy_adapter_imv_delete_tag)
        ImageView itemDeployAdapterImvDeleteTag;
        @BindView(R.id.item_deploy_adapter_ll_tag)
        LinearLayout itemDeployAdapterLlTag;
        @BindView(R.id.item_deploy_adapter_imv_add_tag)
        ImageView itemDeployAdapterImvAddTag;

        DeployDeviceTagAddTagHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface DeployDeviceTagAddTagItemClickListener {
        void onAddClick();

        void onDeleteClick(int position);
    }
}
