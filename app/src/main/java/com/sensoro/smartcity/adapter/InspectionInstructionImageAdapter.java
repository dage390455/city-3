package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionInstructionImageAdapter extends RecyclerView.Adapter<InspectionInstructionImageAdapter.InspectionInstructionImageHolder> {
    private final Context mContext;
    private List<ScenesData> datas = new ArrayList<>();
    private boolean hasVideo = false;

    public InspectionInstructionImageAdapter(Context context) {
        mContext = context;
    }


    @Override
    public InspectionInstructionImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_inspection_instruction_image, parent, false);
        return new InspectionInstructionImageHolder(view);
    }

    public void setPhotoType(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    @Override
    public void onBindViewHolder(InspectionInstructionImageHolder holder, final int position) {
        String url;
        if ("image".equals(datas.get(position).type)) {
            url = datas.get(position).url;
        } else {
            url = datas.get(position).thumbUrl;
        }

        Glide.with((Activity) mContext)
                .load(url)
                .error(R.drawable.ic_default_image)
                .placeholder(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .override(AppUtils.dp2px(mContext,335),AppUtils.dp2px(mContext,201))
                .into(holder.itemAdapterInspectionInstructionImv);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void updateDataList(List<ScenesData> picUrls) {
        datas.clear();
        datas.addAll(picUrls);
        notifyDataSetChanged();
    }


    public List<ScenesData> getDataList() {
        return datas;
    }

    public ScenesData getItem(int position) {
        return datas.get(position);
    }

    class InspectionInstructionImageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_inspection_instruction_imv)
        ImageView itemAdapterInspectionInstructionImv;

        InspectionInstructionImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }

}
