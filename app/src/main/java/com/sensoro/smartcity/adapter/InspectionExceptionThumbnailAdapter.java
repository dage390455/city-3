package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionExceptionThumbnailAdapter extends RecyclerView.Adapter<InspectionExceptionThumbnailAdapter.InspectionExceptionThumbnailHolder> {
    private final Context mContext;


    public InspectionExceptionThumbnailAdapter(Context context) {
        mContext = context;
    }


    @Override
    public InspectionExceptionThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_inspection_exception_thumbnail, parent, false);
        return new InspectionExceptionThumbnailHolder(view);
    }

    @Override
    public void onBindViewHolder(InspectionExceptionThumbnailHolder holder, int position) {
        //tem_thumbnail 为临时图片，要删除了
        holder.itemAdapterInspectionExceptionImvThumbnail.setImageResource(R.drawable.tem_thumbnail);
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    class InspectionExceptionThumbnailHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_inspection_exception_imv_thumbnail)
        ImageView itemAdapterInspectionExceptionImvThumbnail;

        public InspectionExceptionThumbnailHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
