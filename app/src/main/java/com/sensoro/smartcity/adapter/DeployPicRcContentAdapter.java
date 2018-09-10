package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeployPicRcContentAdapter extends RecyclerView.Adapter<DeployPicRcContentAdapter.DeployPicRcContentHolder> {
    private final Activity mActivity;


    public DeployPicRcContentAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public DeployPicRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_adapter_deploy_pic_content, parent, false);
        return new DeployPicRcContentHolder(view);
    }

    @Override
    public void onBindViewHolder(DeployPicRcContentHolder holder, int position) {
        if(position<5){
            holder.itemDeployRlThumbnail.setVisibility(View.VISIBLE);
            holder.itemDeployPicLlAddThumbnail.setVisibility(View.GONE);
        }else{
            holder.itemDeployPicLlAddThumbnail.setVisibility(View.VISIBLE);
            holder.itemDeployRlThumbnail.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    class DeployPicRcContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_deploy_pic_imv_thumbnail)
        ImageView itemDeployPicImvThumbnail;
        @BindView(R.id.item_deploy_pic_imv_close)
        ImageView itemDeployPicImvClose;
        @BindView(R.id.item_deploy_rl_thumbnail)
        RelativeLayout itemDeployRlThumbnail;
        @BindView(R.id.item_deploy_pic_ll_add_thumbnail)
        LinearLayout itemDeployPicLlAddThumbnail;

        public DeployPicRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
