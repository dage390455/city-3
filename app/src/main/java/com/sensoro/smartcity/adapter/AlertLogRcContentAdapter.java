package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlertLogRcContentAdapter extends RecyclerView.Adapter<AlertLogRcContentAdapter.AlertLogRcContentHolder> {
    private final Context mContext;


    public AlertLogRcContentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public AlertLogRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_alert_log_content, parent, false);

        return new AlertLogRcContentHolder(inflate);
    }

    @Override
    public void onBindViewHolder(AlertLogRcContentHolder holder, int position) {
        //用span改变字体颜色,换行 用\n
        String content = "联系人[高鹏]通过 平台 确认本次预警类型为：\n安全隐患";
        SpannableString spannableString = new SpannableString(content);
        // 改变高鹏 颜色
        String temp = "[高鹏]";
        changTextColor(content, temp,spannableString, R.color.c_131313);
        //改变安全隐患颜色
        temp = "安全隐患";
        changTextColor(content,temp,spannableString,R.color.c_f34a4a);

        holder.itemAlertContentTvContent.setText(spannableString);


    }

    private void changTextColor(String content, String temp, SpannableString spannableString, @ColorRes int color) {
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(color));
        int i = content.indexOf(temp);
        spannableString.setSpan(foregroundColorSpan, i, i + temp.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class AlertLogRcContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_alert_content_imv_icon)
        ImageView itemAlertContentImvIcon;
        @BindView(R.id.item_alert_content_tv_content)
        TextView itemAlertContentTvContent;
        @BindView(R.id.item_alert_content_tv_time)
        TextView itemAlertContentTvTime;

        public AlertLogRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
