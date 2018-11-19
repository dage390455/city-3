package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

public class InspectionUploadExceptionTagAdapter extends RecyclerView.Adapter<InspectionUploadExceptionTagAdapter.InspectionUploadExceptionTagHolder> {
    private final Context mContext;
    private List<String> tags = new ArrayList<>();
    private List<Integer> selectedTags = new ArrayList<>();


    public InspectionUploadExceptionTagAdapter(Context context) {
        mContext = context;
    }

    public void updateTags(List<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        notifyDataSetChanged();
    }

    @Override
    public InspectionUploadExceptionTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_inspection_upload_exception_tag, parent, false);
        return new InspectionUploadExceptionTagHolder(view);
    }

    @Override
    public void onBindViewHolder(InspectionUploadExceptionTagHolder holder, final int position) {
        holder.itemAdapterInspectionUploadExceptionTv.setText(tags.get(position));
        Resources resources = mContext.getResources();
        GradientDrawable gd = (GradientDrawable) resources.getDrawable(R.drawable.shape_bg_inspection_upload_exception_tag_full_corner);
        gd.setBounds(0, 0, gd.getMinimumWidth(), gd.getMinimumHeight());
        if (selectedTags.contains(position)) {
            holder.itemAdapterInspectionUploadExceptionTv.setTextColor(Color.WHITE);
            gd.setColor(resources.getColor(R.color.c_ff8d34));
        } else {
            gd.setColor(Color.WHITE);
            holder.itemAdapterInspectionUploadExceptionTv.setTextColor(resources.getColor(R.color.c_252525));
        }

        holder.itemAdapterInspectionUploadExceptionTv.setBackground(gd);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTags.contains(position)) {
                    selectedTags.remove((Integer) position);
                } else {
                    selectedTags.add(position);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public List<Integer> getSelectTag() {
        return selectedTags;
    }

    class InspectionUploadExceptionTagHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_adapter_inspection_upload_exception_tv)
        TextView itemAdapterInspectionUploadExceptionTv;

        InspectionUploadExceptionTagHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
