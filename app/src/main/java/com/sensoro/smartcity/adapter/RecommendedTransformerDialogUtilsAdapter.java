package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.RecommendedTransformerValueModel;

import java.util.ArrayList;
import java.util.List;


public class RecommendedTransformerDialogUtilsAdapter extends RecyclerView.Adapter<RecommendedTransformerDialogUtilsAdapter
        .EarlyWarningThresholdDialogUtilsHolder> {

    private Context mContext;
    private final ArrayList<RecommendedTransformerValueModel> mList = new ArrayList<>();
    private RecycleViewItemClickListener listener;

    public RecommendedTransformerDialogUtilsAdapter(Context context) {
        this.mContext = context;
    }

    public void setOnItemClickListener(RecycleViewItemClickListener recycleViewItemClickListener) {
        this.listener = recycleViewItemClickListener;
    }

    public void updateList(List<RecommendedTransformerValueModel> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<RecommendedTransformerValueModel> getData() {
        return mList;
    }

    @Override
    public EarlyWarningThresholdDialogUtilsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recommend_transformer_dialog, parent, false);

        return new EarlyWarningThresholdDialogUtilsHolder(view);
    }

    @Override
    public void onBindViewHolder(EarlyWarningThresholdDialogUtilsHolder holder, int position) {
        RecommendedTransformerValueModel recommendedTransformerValueModel = mList.get(position);
        StringBuilder stringBuilder = new StringBuilder();
        if (recommendedTransformerValueModel.isRecommend) {
            holder.tvValue.setBackground(mContext.getDrawable(R.drawable.shape_bg_corner_1dbb99_shadow));
            holder.tvValue.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.tvValue.setText(stringBuilder.append(recommendedTransformerValueModel.value).append("A").append("(").append(mContext.getString(R.string.recommend)).append(")").toString());
        } else {
            holder.tvValue.setBackground(mContext.getDrawable(R.drawable.shape_bg_corner_f4f4f4_shadow));
            holder.tvValue.setTextColor(mContext.getResources().getColor(R.color.c_5d5d5d));
            holder.tvValue.setText(stringBuilder.append(recommendedTransformerValueModel.value).append("A").toString());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class EarlyWarningThresholdDialogUtilsHolder extends RecyclerView.ViewHolder {
        final TextView tvValue;

        EarlyWarningThresholdDialogUtilsHolder(View itemView) {
            super(itemView);
            tvValue = (TextView) itemView.findViewById(R.id.tv_recommend_transformer_value);
        }
    }
}
