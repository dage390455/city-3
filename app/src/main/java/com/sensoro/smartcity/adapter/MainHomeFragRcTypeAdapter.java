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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置卡片背景色：holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);
 * 改变阴影颜色，目前cardview 不支持，有其他解决方案，如第三库，再研究吧
 */
public class MainHomeFragRcTypeAdapter extends RecyclerView.Adapter<MainHomeFragRcTypeAdapter.MyViewHolder> {

    private final Context mContext;

    public MainHomeFragRcTypeAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_main_home_rc_type, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(position==0){
//            holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);

        }else{
            holder.mainRcTypeCv.setCardBackgroundColor(Color.WHITE);
            holder.mainRcTypeTvStateTxt.setTextColor(mContext.getResources().getColor(R.color.c_6a6a6a));
            holder.mainRcTypeTvCount.setTextColor(mContext.getResources().getColor(R.color.c_252525));
            holder.mainRcTypeImvState.setImageResource(R.drawable.main_menu_close);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
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
            ButterKnife.bind(this,itemView);

        }
    }
}
