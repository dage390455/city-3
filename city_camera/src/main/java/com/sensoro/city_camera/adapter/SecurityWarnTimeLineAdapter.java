package com.sensoro.city_camera.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
    /**
     * 预警时间线-电话/短线处理状态：成功
     */
    int SECURITY_TIMELINE_STATUS_SUCCESS = 1;
    /**
     * 预警时间线-电话/短线处理状态：失败
     */
    int SECURITY_TIMELINE_STATUS_FAILURE = -1;
    /**
     * 预警时间线-电话/短线状态：处理中
     */
    int SECURITY_TIMELINE_STATUS_PROCESS = 0;
    /**
     * 预警时间线-时间类型：创建
     */
    int SECURITY_TIMELINE_EVENT_TYPE_CREATE = 1;
    /**
     * 预警时间线-事件类型：处理 两种状态：有效 无效
     */
    int SECURITY_TIMELINE_EVENT_TYPE_PROCESS = 2;
    /**
     * 预警时间线-事件类型：系统拨打电话 3种状态：成功 失败 呼叫中
     */
    int SECURITY_TIMELINE_EVENT_TYPE_CALL = 3;
    /**
     * 预警时间线-事件类型：系统发送短信 3种状态：成功 失败 发送中
     */
    int SECURITY_TIMELINE_EVENT_TYPE_SMS = 4;

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
            int mEventType;
            try {
                mEventType = Integer.parseInt(securityAlarmEventInfo.type.trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mEventType = -1;
            }
            if (mEventType == SECURITY_TIMELINE_EVENT_TYPE_CREATE) {
                holder.mIconIv.setImageResource(R.drawable.icon_security_log);
                holder.mTitleTv.setText(securityAlarmEventInfo.content);
                holder.mTitleTv.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                holder.mRemarksTv.setVisibility(View.GONE);
            } else if (mEventType == SECURITY_TIMELINE_EVENT_TYPE_PROCESS) {
                holder.mIconIv.setImageResource(R.drawable.icon_security_process_log);
                holder.mTitleTv.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                //安防预警处理
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
            } else if (mEventType == SECURITY_TIMELINE_EVENT_TYPE_CALL) {
                if (securityAlarmEventInfo.records != null && securityAlarmEventInfo.records.size() > 0) {
                    holder.mIconIv.setImageResource(R.drawable.icon_security_phone_log);
                    holder.mTitleTv.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
                    SpannableStringBuilder ssb = new SpannableStringBuilder();
                    for (int i = 0; i < securityAlarmEventInfo.records.size(); i++) {
                        SecurityAlarmEventInfo.EventRecord callRecord = securityAlarmEventInfo.records.get(i);
                        ssb.append(callRecord.content);
                        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.c_252525)), ssb.length() - callRecord.content.length(), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (SECURITY_TIMELINE_STATUS_SUCCESS == callRecord.status) {
                            ssb.append(mContext.getString(R.string.security_warn_timeline_call_success));
                        } else if (SECURITY_TIMELINE_STATUS_PROCESS == callRecord.status) {
                            ssb.append(mContext.getString(R.string.security_warn_timeline_call_process));
                        } else {
                            ssb.append(mContext.getString(R.string.security_warn_timeline_call_failure));
                        }
                        if (i != securityAlarmEventInfo.records.size() - 1) {
                            ssb.append(";");
                        }
                    }
                    holder.mTitleTv.setText(ssb);
                }

            } else if (mEventType == SECURITY_TIMELINE_EVENT_TYPE_SMS) {
                holder.mIconIv.setImageResource(R.drawable.icon_security_msg_log);
                holder.mTitleTv.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                for (int i = 0; i < securityAlarmEventInfo.records.size(); i++) {
                    SecurityAlarmEventInfo.EventRecord callRecord = securityAlarmEventInfo.records.get(i);
                    ssb.append(callRecord.content);
                    ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.c_252525)), ssb.length() - callRecord.content.length(), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (SECURITY_TIMELINE_STATUS_SUCCESS == callRecord.status) {
                        ssb.append(mContext.getString(R.string.security_warn_timeline_sms_success));
                    } else if (SECURITY_TIMELINE_STATUS_PROCESS == callRecord.status) {
                        ssb.append(mContext.getString(R.string.security_warn_timeline_sms_process));
                    } else {
                        ssb.append(mContext.getString(R.string.security_warn_timeline_sms_failure));
                    }
                    if (i != securityAlarmEventInfo.records.size() - 1) {
                        ssb.append(";");
                    }
                }
                holder.mTitleTv.setText(ssb);

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
