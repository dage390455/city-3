package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InspectionTaskAdapter extends RecyclerView.Adapter<InspectionTaskAdapter.InspectionTaskHolder> {
    private final Context mContext;

    public InspectionTaskAdapter(Context context) {
        mContext = context;
    }

    @Override
    public InspectionTaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_inspection_task, parent, false);
        return new InspectionTaskHolder(view);
    }

    @Override
    public void onBindViewHolder(InspectionTaskHolder holder, int position) {
        holder.itemInspectionAdapterTvTitle.setText("巡检任务2018.05.01");
        holder.itemInspectionAdapterTvTime.setText("2018/05/01 - 2018/05/31");

        if(position==0){
            setTvState(holder.itemInspectionAdapterTvState,R.color.c_8058a5,"待执行");

        }else if(position==1){
            setTvState(holder.itemInspectionAdapterTvState,R.color.c_3aa7f0,"待执行");
        }else if(position==2){
            setTvState(holder.itemInspectionAdapterTvState,R.color.c_ff8d34,"超时未完成");
        }

    }

    private void setTvState(TextView textView, @ColorRes int color, String text) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        ((GradientDrawable)compoundDrawables[0]).setColor(mContext.getResources().getColor(color));
        textView.setText(text);


    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class InspectionTaskHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_inspection_adapter_tv_title)
        TextView itemInspectionAdapterTvTitle;
        @BindView(R.id.item_inspection_adapter_tv_time)
        TextView itemInspectionAdapterTvTime;
        @BindView(R.id.item_inspection_adapter_imv_arrows)
        ImageView itemInspectionAdapterImvArrows;
        @BindView(R.id.item_inspection_adapter_tv_state)
        TextView itemInspectionAdapterTvState;
        @BindView(R.id.item_inspection_adapter_cl_root)
        ConstraintLayout itemInspectionAdapterClRoot;

        InspectionTaskHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
