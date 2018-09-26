package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.ScenesData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InspectionExceptionThumbnailAdapter extends RecyclerView.Adapter<InspectionExceptionThumbnailAdapter.InspectionExceptionThumbnailHolder> {
    private final Context mContext;
    private List<ScenesData> datas = new ArrayList<>();
    private ExceptionThumbnailItemClickListener listener;


    public InspectionExceptionThumbnailAdapter(Context context) {
        mContext = context;
    }


    @Override
    public InspectionExceptionThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_inspection_exception_thumbnail, parent, false);
        return new InspectionExceptionThumbnailHolder(view);
    }

    @Override
    public void onBindViewHolder(InspectionExceptionThumbnailHolder holder, final int position) {
        String url;
        if ("image".equals(datas.get(position).type)) {
            url = datas.get(position).url;
        }else{
            url = datas.get(position).thumbUrl;
        }

        Glide.with((Activity) mContext)
                .load(url)
                .error(R.drawable.ic_default_image)
                .placeholder(R.drawable.ic_default_image)
                .thumbnail(0.01f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.itemAdapterInspectionExceptionImvThumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onExceptionThumbnailItemClickListener(position);
                }
            }
        });

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
    public void setOnExceptionThumbnailItemClickListener(ExceptionThumbnailItemClickListener listener){
        this.listener = listener;
    }

    public List<ScenesData> getDataList() {
        return datas;
    }

    public ScenesData getItem(int position) {
        return datas.get(position);
    }

    class InspectionExceptionThumbnailHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_inspection_exception_imv_thumbnail)
        ImageView itemAdapterInspectionExceptionImvThumbnail;

        public InspectionExceptionThumbnailHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface ExceptionThumbnailItemClickListener{
        void onExceptionThumbnailItemClickListener(int position);
    }

}
