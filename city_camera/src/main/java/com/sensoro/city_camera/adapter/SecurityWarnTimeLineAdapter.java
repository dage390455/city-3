package com.sensoro.city_camera.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.city_camera.R;
import com.sensoro.city_camera.R2;
import com.sensoro.city_camera.constants.SecurityConstants;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.server.security.bean.SecurityAlarmEventInfo;
import com.sensoro.common.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author : bin.tian
 * date   : 2019-06-28
 */
public class SecurityWarnTimeLineAdapter extends RecyclerView.Adapter<SecurityWarnTimeLineAdapter.SecurityTimeLineViewHolder> {
    private List<SecurityAlarmEventInfo> mDataList = new ArrayList<>();
    private Context mContext;

    public SecurityWarnTimeLineAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public SecurityTimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SecurityTimeLineViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.security_warn_detail_log_itme_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityTimeLineViewHolder holder, int position) {
        SecurityAlarmEventInfo securityAlarmEventInfo = mDataList.get(position);
        if (securityAlarmEventInfo != null) {
            if (TextUtils.equals(securityAlarmEventInfo.type, "2")) {
                holder.mIconIv.setImageResource(R.drawable.icon_security_log_remarked);
                if (!TextUtils.isEmpty(securityAlarmEventInfo.content)) {
                    holder.mRemarksTv.setVisibility(View.VISIBLE);
                    holder.mRemarksTv.setText(mContext.getString(R.string.security_warn_timeline_remark, securityAlarmEventInfo.content));
                } else {
                    holder.mRemarksTv.setVisibility(View.GONE);
                }

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(mContext.getString(R.string.security_warn_timeline_title, securityAlarmEventInfo.handler.name, securityAlarmEventInfo.source));
                ssb.append(" ");
                if (securityAlarmEventInfo.status == SecurityConstants.SECURITY_INVALID) {
                    String invalid = mContext.getString(R.string.word_unvalid);
                    ssb.append(invalid);
                    ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.c_a6a6a6)), ssb.length() - invalid.length(), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    String valid = mContext.getString(R.string.word_valid);
                    ssb.append(valid);
                    ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.c_f35a58)), ssb.length() - valid.length(), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.mTitleTv.setText(ssb);
            } else {
                holder.mIconIv.setImageResource(R.drawable.icon_security_log);
                holder.mTitleTv.setText(securityAlarmEventInfo.content);
            }

            holder.mTimeTv.setText(DateUtil.getStrTimeToday(mContext, securityAlarmEventInfo.createTime, 0));
        }
    }

    public void setDataList(List<SecurityAlarmEventInfo> list) {
        mDataList.clear();
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class SecurityTimeLineViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.icon)
        ImageView mIconIv;
        @BindView(R2.id.security_warn_detail_log_item_title_tv)
        TextView mTitleTv;
        @BindView(R2.id.security_warn_detail_log_item_remarks_tv)
        TextView mRemarksTv;
        @BindView(R2.id.security_warn_detail_log_item_time_tv)
        TextView mTimeTv;

        public SecurityTimeLineViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
