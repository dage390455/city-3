package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.ScenesData;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionInstructionImageAdapter extends RecyclerView.Adapter<InspectionInstructionImageAdapter.InspectionInstructionImageHolder> {
    private final Context mContext;
    private final int mScreenWidth;
    private List<ScenesData> datas = new ArrayList<>();
    private boolean hasVideo = false;

    public InspectionInstructionImageAdapter(Context context, int screenWidth) {
        mContext = context;
        mScreenWidth = screenWidth;
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
    public void onBindViewHolder(final InspectionInstructionImageHolder holder, final int position) {
        String url;
        if ("image".equals(datas.get(position).type)) {
            url = datas.get(position).url;
        } else {
            url = datas.get(position).thumbUrl;
        }

        Glide.with((Activity) mContext)
                .load(url)
                .asBitmap()
                .error(R.drawable.ic_default_image)
                .placeholder(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {

                            resource.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            int width = resource.getWidth();
                            double resourceHeight = resource.getHeight();
                            double percent = (double) mScreenWidth / (double) width;
                            int height = (int) (resourceHeight * percent);
                            Glide.with(mContext)
                                    .load(baos.toByteArray())
                                    .override(mScreenWidth, height)
                                    .into(holder.itemAdapterInspectionInstructionImv);
                        } catch (Exception e) {
                            Glide.with(mContext)
                                    .load(baos.toByteArray())
                                    .into(holder.itemAdapterInspectionInstructionImv);
                            e.printStackTrace();
                        }

                    }
                });
//                .into(holder.itemAdapterInspectionInstructionImv);
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
