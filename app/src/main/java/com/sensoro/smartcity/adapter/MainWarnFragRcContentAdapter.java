package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autonavi.ae.pos.LocNGMInfo;
import com.sensoro.smartcity.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainWarnFragRcContentAdapter extends RecyclerView.Adapter<MainWarnFragRcContentAdapter.MyViewHolder> {

    private final Context mContext;


    public MainWarnFragRcContentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_main_warn_rc_content, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(position==1){
            changeStrokeColor(holder.mainWarnRcContentTvTag,R.color.c_ff8d34);
            holder.mainWarnRcContentTvTag.setText("误报");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void changeStrokeColor(View view, @ColorRes int color) {
        float density = mContext.getResources().getDisplayMetrics().density;
        float corner =  density * 16;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(corner);
        gradientDrawable.setStroke((int) density,mContext.getResources().getColor(color));
        view.setBackground(gradientDrawable);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.main_warn_rc_content_tv_state)
        TextView mainWarnRcContentTvState;
        @BindView(R.id.main_warn_rc_content_tv_time)
        TextView mainWarnRcContentTvTime;
        @BindView(R.id.main_warn_rc_content_tv_content)
        TextView mainWarnRcContentTvContent;
        @BindView(R.id.main_warn_rc_content_btn_confirm)
        TextView mainWarnRcContentBtnConfirm;
        @BindView(R.id.main_warn_rc_content_btn_contact_landlord)
        TextView mainWarnRcContentBtnContactLandlord;
        @BindView(R.id.main_warn_rc_content_tv_tag)
        TextView mainWarnRcContentTvTag;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
