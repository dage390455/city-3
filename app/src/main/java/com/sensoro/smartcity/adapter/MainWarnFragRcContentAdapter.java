package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainWarnFragRcContentAdapter extends RecyclerView.Adapter<MainWarnFragRcContentAdapter.MyViewHolder> implements Constants {

    private final Context mContext;
    private AlarmConfirmStatusClickListener mListener;
    private final List<DeviceAlarmLogInfo> mList = new ArrayList<>();
    private final String[] confirmStatusArray = {"待确认", "真实火警", "误报", "测试/巡检", "安全隐患"};
    private RecycleViewItemClickListener recycleViewItemClickListener;

    public MainWarnFragRcContentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_main_warn_rc_content, parent, false);
        return new MyViewHolder(inflate);
    }

    public void setAlarmConfirmStatusClickListener(AlarmConfirmStatusClickListener listener) {
        mListener = listener;
    }

    public void setOnItemClickListener(RecycleViewItemClickListener recycleViewItemClickListener) {
        this.recycleViewItemClickListener = recycleViewItemClickListener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
//        if(position==1){
//            changeStrokeColor(holder.mainWarnRcContentTvTag,R.color.c_ff8d34);
//            holder.mainWarnRcContentTvTag.setText("误报");
//        }
        DeviceAlarmLogInfo alarmLogInfo = mList.get(position);
        if (alarmLogInfo != null) {
            String deviceName = alarmLogInfo.getDeviceName();
            //
            String deviceSN = alarmLogInfo.getDeviceSN();
            String deviceType = alarmLogInfo.getDeviceType();
            List<String> strings = new ArrayList<String>();
            strings.add(deviceType);
            String default_name = deviceSN.isEmpty() ? mContext.getResources().getString(R.string
                    .unname) : deviceSN;
            if (TextUtils.isEmpty(deviceName)) {
                holder.mainWarnRcContentTvContent.setText(WidgetUtil.parseSensorTypes(mContext, strings) + " " + default_name);
            } else {
                holder.mainWarnRcContentTvContent.setText(WidgetUtil.parseSensorTypes(mContext, strings) + " " + deviceName);
            }
            holder.mainWarnRcContentTvTime.setText(DateUtil.getFullParseDate(alarmLogInfo.getUpdatedTime()));
            //
            AlarmInfo.RecordInfo[] recordInfoArray = alarmLogInfo.getRecords();
//            for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
////                AlarmInfo.RecordInfo.Event[] event = recordInfo.getPhoneList();
//                String type = recordInfo.getType();
//                if ("recovery".equals(type)) {
//                    holder.item_iv_status.setVisibility(View.VISIBLE);
//                    holder.item_alarm_view.setVisibility(View.GONE);
//                    holder.item_status.setTextColor(mContext.getResources().getColor(R.color.sensoro_normal));
//                    holder.item_status.setText("于" + DateUtil.getFullParseDate(recordInfo.getUpdatedTime()) + "恢复正常");
//                    holder.item_iv_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable
//                            .shape_status_normal));
//
//                    break;
//                } else {
//                    holder.item_alarm_view.setVisibility(View.VISIBLE);
//                    holder.item_iv_status.setVisibility(View.GONE);
//                    holder.item_iv_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable
//                            .shape_status_alarm));
//                    holder.item_status.setTextColor(mContext.getResources().getColor(R.color.sensoro_alarm));
//                    holder.item_status.setText(R.string.alarming);
//                }
//            }
            switch (alarmLogInfo.getDisplayStatus()) {
                case DISPLAY_STATUS_CONFIRM:
                    holder.mainWarnRcContentBtnConfirm.setText(R.string.confirming);
                    holder.mainWarnRcContentTvTag.setVisibility(View.GONE);
                    holder.mainWarnRcContentBtnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onConfirmStatusClick(v, position, false);
                        }
                    });
                    holder.mainWarnRcContentTvState.setText("正常");
                    holder.mainWarnRcContentTvState.setTextColor(mContext.getResources().getColor(R.color.c_29c093));
                    break;
                case DISPLAY_STATUS_ALARM:
                    holder.mainWarnRcContentTvTag.setVisibility(View.VISIBLE);
                    holder.mainWarnRcContentBtnConfirm.setText(R.string.confirming_again);
                    holder.mainWarnRcContentBtnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onConfirmStatusClick(v, position, true);
                        }
                    });
                    holder.mainWarnRcContentTvState.setText("报警中");
                    holder.mainWarnRcContentTvState.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));
                    holder.mainWarnRcContentTvTag.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));
                    //TODO 确认预警类型
                    holder.mainWarnRcContentTvTag.setText("真实预警");
                    changeStrokeColor(holder.mainWarnRcContentTvTag, R.color.c_f34a4a);
                    break;
                case DISPLAY_STATUS_MIS_DESCRIPTION:
                    holder.mainWarnRcContentBtnConfirm.setText(R.string.confirming_again);
                    holder.mainWarnRcContentBtnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onConfirmStatusClick(v, position, true);
                        }
                    });
//
                    holder.mainWarnRcContentTvState.setText("失联");
                    holder.mainWarnRcContentTvState.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));
                    holder.mainWarnRcContentTvTag.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));
                    //TODO 确认预警类型
                    holder.mainWarnRcContentTvTag.setText("失联");
                    changeStrokeColor(holder.mainWarnRcContentTvTag, R.color.c_f34a4a);
                    break;
                case DISPLAY_STATUS_TEST:
                    holder.mainWarnRcContentBtnConfirm.setText(R.string.confirming_again);
                    holder.mainWarnRcContentBtnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onConfirmStatusClick(v, position, true);
                        }
                    });
//
                    holder.mainWarnRcContentTvState.setText("测试");
                    holder.mainWarnRcContentTvState.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));
                    holder.mainWarnRcContentTvTag.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));
                    //TODO 确认预警类型
                    holder.mainWarnRcContentTvTag.setText("测试");
                    changeStrokeColor(holder.mainWarnRcContentTvTag, R.color.c_f34a4a);
                    break;
                case DISPLAY_STATUS_RISKS:
                    holder.mainWarnRcContentBtnConfirm.setText(R.string.confirming_again);
                    holder.mainWarnRcContentBtnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onConfirmStatusClick(v, position, true);
                        }
                    });
//
                    holder.mainWarnRcContentTvState.setText("误报");
                    holder.mainWarnRcContentTvState.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));
                    holder.mainWarnRcContentTvTag.setTextColor(mContext.getResources().getColor(R.color.c_f34a4a));
                    //TODO 确认预警类型
                    holder.mainWarnRcContentTvTag.setText("误报");
                    changeStrokeColor(holder.mainWarnRcContentTvTag, R.color.c_f34a4a);
                    break;
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recycleViewItemClickListener != null) {
                    recycleViewItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    private void changeStrokeColor(TextView view, @ColorRes int color) {
        float density = mContext.getResources().getDisplayMetrics().density;
        float corner = density * 16;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(corner);
        gradientDrawable.setStroke((int) density, mContext.getResources().getColor(color));
        view.setBackground(gradientDrawable);
        view.setTextColor(mContext.getResources().getColor(color));
    }

    public void setData(List<DeviceAlarmLogInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public List<DeviceAlarmLogInfo> getData() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
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

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface AlarmConfirmStatusClickListener {
        void onConfirmStatusClick(View view, int position, boolean isReConfirm);
    }

}
