package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.HomeTopModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置卡片背景色：holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);
 * 改变阴影颜色，目前cardview 不支持，有其他解决方案，如第三库，再研究吧
 */
public class MainHomeFragRcTypeAdapter extends RecyclerView.Adapter<MainHomeFragRcTypeAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<HomeTopModel> mData = new ArrayList<>();
    private OnTopClickListener onTopClickListener;

    public MainHomeFragRcTypeAdapter(Context context) {
        mContext = context;
    }

    public void setOnTopClickListener(OnTopClickListener onTopClickListener) {
        this.onTopClickListener = onTopClickListener;
    }

    public void setData(List<HomeTopModel> data) {
        mData.clear();
        mData.addAll(data);
    }

    public interface OnTopClickListener {
        void onStatusChange(int status);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_main_home_rc_type, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        HomeTopModel homeTopModel = mData.get(position);
        switch (homeTopModel.type) {
            case 0:
                holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(R.color.c_f34a4a));
                holder.mainRcTypeTvStateTxt.setTextColor(Color.WHITE);
                holder.mainRcTypeTvStateTxt.setText("预警");
                holder.mainRcTypeTvCount.setTextColor(Color.WHITE);
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_warning);
                break;
            case 1:
                holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(R.color.c_29c093));
                holder.mainRcTypeTvStateTxt.setTextColor(Color.WHITE);
                holder.mainRcTypeTvCount.setTextColor(Color.WHITE);
                holder.mainRcTypeTvStateTxt.setText("正常监测点");
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_normal);
                break;
            case 2:
                holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(R.color.c_fafafa));
                holder.mainRcTypeTvStateTxt.setTextColor(mContext.getResources().getColor(R.color.c_6a6a6a));
                holder.mainRcTypeTvCount.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_lose);
                holder.mainRcTypeTvStateTxt.setText("失联");
                break;
            case 3:
                holder.mainRcTypeCv.setCardBackgroundColor(mContext.getResources().getColor(R.color.c_fafafa));
                holder.mainRcTypeTvStateTxt.setTextColor(mContext.getResources().getColor(R.color.c_6a6a6a));
                holder.mainRcTypeTvCount.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                holder.mainRcTypeTvStateTxt.setText("未激活");
                //TODO 失联状态暂时用故障图标
                holder.mainRcTypeImvState.setImageResource(R.drawable.main_type_trouble);
                break;
        }
        holder.mainRcTypeTvCount.setText(String.valueOf(homeTopModel.value));
        if (position == 0) {
//            holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);
        } else {
//            holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTopClickListener != null) {
                    onTopClickListener.onStatusChange(mData.get(position).type);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.main_rc_type_imv_state)
        ImageView mainRcTypeImvState;
        @BindView(R.id.main_rc_type_tv_count)
        TextView mainRcTypeTvCount;
        @BindView(R.id.main_rc_type_cv)
        CardView mainRcTypeCv;
        @BindView(R.id.main_rc_type_tv_state_txt)
        TextView mainRcTypeTvStateTxt;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
