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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeployDeviceTagAddTagAdapter extends RecyclerView.Adapter<DeployDeviceTagAddTagAdapter.DeployDeviceTagAddTagHolder> {
    private final Context mContext;
    private DeployDeviceTagAddTagItemClickListener listener;


    public DeployDeviceTagAddTagAdapter(Context context) {
        mContext = context;
    }

    @Override
    public DeployDeviceTagAddTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_deploy_add_tag, parent, false);

        return new DeployDeviceTagAddTagHolder(view);
    }


    @Override
    public void onBindViewHolder(DeployDeviceTagAddTagHolder holder, int position) {
        if (position == 5) {
            holder.itemDeployAdapterLlTag.setVisibility(View.GONE);
            holder.itemDeployAdapterImvAddTag.setVisibility(View.VISIBLE);
        }else{
            holder.itemDeployAdapterLlTag.setVisibility(View.VISIBLE);
            holder.itemDeployAdapterImvAddTag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public void setDeployDeviceTagAddTagItemClickListener(DeployDeviceTagAddTagItemClickListener listener){
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
        public DeployDeviceTagAddTagHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick({R.id.item_deploy_adapter_imv_delete_tag, R.id.item_deploy_adapter_imv_add_tag})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.item_deploy_adapter_imv_delete_tag:
                    if (listener!=null) {
                        listener.onDeleteClick();
                    }
                    break;
                case R.id.item_deploy_adapter_imv_add_tag:
                    if (listener!=null) {
                        listener.onAddClick();
                    }
                    break;
            }
        }
    }

    public interface DeployDeviceTagAddTagItemClickListener{
        void onAddClick();

        void onDeleteClick();
    }
}
