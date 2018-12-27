package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.bean.SensorTypeStyles;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlertLogRcContentAdapter extends RecyclerView.Adapter<AlertLogRcContentAdapter.AlertLogRcContentHolder> implements Constants {
    private final Context mContext;
    private final List<AlarmInfo.RecordInfo> timeShaftParentBeans = new ArrayList<>();
    private List<AlarmInfo.RecordInfo.Event> receiveStautus0 = new ArrayList<>();
    private List<AlarmInfo.RecordInfo.Event> receiveStautus1 = new ArrayList<>();
    private List<AlarmInfo.RecordInfo.Event> receiveStautus2 = new ArrayList<>();
    private List<AlarmInfo.RecordInfo.Event> receiveStautus3 = new ArrayList<>();

    public AlertLogRcContentAdapter(Context context) {
        mContext = context;

    }

    private OnPhotoClickListener onPhotoClickListener;

    public void setData(List<AlarmInfo.RecordInfo> recordInfoList) {
        this.timeShaftParentBeans.clear();
        this.timeShaftParentBeans.addAll(recordInfoList);
    }

    public interface OnPhotoClickListener {
        void onPhotoItemClick(int position, List<ScenesData> scenesDataList);
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    @Override
    public AlertLogRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_alert_log_content, parent, false);

        return new AlertLogRcContentHolder(inflate);
    }

    @Override
    public void onBindViewHolder(AlertLogRcContentHolder holder, int position) {

        //

        AlarmInfo.RecordInfo recordInfo = timeShaftParentBeans.get(position);
        String time = DateUtil.getStrTimeToday(mContext, recordInfo.getUpdatedTime(), 0);
        holder.itemAlertContentTvTime.setText(time);
        //
        if ("confirm".equals(recordInfo.getType())) {
            //TODO 设置图标
            holder.itemAlertContentImvIcon.setImageResource(R.drawable.contact_icon);
            String source = recordInfo.getSource();
            String confirm_text = null;
            if ("auto".equals(source)) {
                int day = 2;
                try {
                    long timeout = recordInfo.getTimeout();
                    day = (int) (timeout / 3600);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                confirm_text = day + mContext.getString(R.string.no_one_confirmed_the_system_automatically_confirms);
                SpannableString spannableString = new SpannableString(confirm_text);
                String temp = day + mContext.getString(R.string.hour);
                changTextColor(confirm_text, temp, spannableString, R.color.c_252525);
                temp = mContext.getString(R.string.test_patrol);
                changTextColor(confirm_text, temp, spannableString, R.color.c_8058a5);
                holder.itemAlertContentTvContent.setText(spannableString);
            } else if ("app".equals(source)) {
                confirm_text = mContext.getString(R.string.contact) + " [" + recordInfo.getName() + "] " + mContext.getString(R.string.confirm_that_the_alert_type_app_is) + ":\n" +
                        mContext.getString(confirmStatusArray[recordInfo.getDisplayStatus()]);
                //用span改变字体颜色,换行 用\n
//            String content = "联系人[高鹏]通过 平台 确认本次预警类型为：\n安全隐患";
                SpannableString spannableString = new SpannableString(confirm_text);
                // 改变高鹏 颜色
                String temp = "[" + recordInfo.getName() + "]";
                changTextColor(confirm_text, temp, spannableString, R.color.c_131313);
                //改变安全隐患颜色
                temp = mContext.getString(confirmStatusArray[recordInfo.getDisplayStatus()]);
                changTextColor(confirm_text, temp, spannableString, confirmStatusTextColorArray[recordInfo.getDisplayStatus()]);

                holder.itemAlertContentTvContent.setText(spannableString);
            } else if ("platform".equals(source)) {
                confirm_text = mContext.getString(R.string.contact) + " [" + recordInfo.getName() + "]" + mContext.getString(R.string.confirm_that_the_alert_type_web_is) + ":\n" +
                        mContext.getString(confirmStatusArray[recordInfo.getDisplayStatus()]);
                //用span改变字体颜色,换行 用\n
//            String content = "联系人[高鹏]通过 平台 确认本次预警类型为：\n安全隐患";
                SpannableString spannableString = new SpannableString(confirm_text);
                // 改变高鹏 颜色
                String temp = "[" + recordInfo.getName() + "]";
                changTextColor(confirm_text, temp, spannableString, R.color.c_252525);
                //改变安全隐患颜色
                temp = mContext.getString(confirmStatusArray[recordInfo.getDisplayStatus()]);
                changTextColor(confirm_text, temp, spannableString, confirmStatusTextColorArray[recordInfo.getDisplayStatus()]);

                holder.itemAlertContentTvContent.setText(spannableString);
            }

            //
            holder.llConfirm.setVisibility(View.VISIBLE);
            //预警结果
            int displayStatus = recordInfo.getDisplayStatus();
            StringBuilder stringBuilder = new StringBuilder();
            holder.itemAlarmDetailChildAlarmResult.setText(stringBuilder.append(mContext.getString(confirmStatusArray[displayStatus])).append("(").append(mContext.getString(confirmAlarmResultInfoArray[displayStatus])).append(")").toString());
            //预警成因
            int reason = recordInfo.getReason();
            holder.itemAlarmDetailChildAlarmType.setText(confirmAlarmTypeArray[reason]);
            //预警场所
            int place = recordInfo.getPlace();
            holder.itemAlarmDetailChildAlarmPlace.setText(confirmAlarmPlaceArray[place]);
            //备注说明
            String remark = recordInfo.getRemark();
            if (!TextUtils.isEmpty(remark)) {
                holder.itemAlarmDetailChildAlarmRemarks.setText(remark);
            }
            final List<ScenesData> scenes = recordInfo.getScenes();
            if (scenes != null && scenes.size() > 0) {
                //TODO 防止数据错误清除
                if (holder.rvAlarmPhoto.getTag() instanceof AlarmDetailPhotoAdapter) {
                    holder.rvAlarmPhoto.removeAllViews();
                }
                //
                final GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4) {
                    @Override
                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                };
                holder.rvAlarmPhoto.setLayoutManager(layoutManager);
                holder.rvAlarmPhoto.setHasFixedSize(true);
                AlarmDetailPhotoAdapter adapter = new AlarmDetailPhotoAdapter(mContext);
                adapter.setOnItemClickListener(new AlarmDetailPhotoAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (onPhotoClickListener != null) {
                            onPhotoClickListener.onPhotoItemClick(position, scenes);
                        }
                    }
                });
                holder.rvAlarmPhoto.setAdapter(adapter);
                //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
                holder.rvAlarmPhoto.setNestedScrollingEnabled(false);
                adapter.setImages(scenes);
                //TODO 防止数据错误打标签
                holder.rvAlarmPhoto.setTag(adapter);
            }


        } else if ("recovery".equals(recordInfo.getType())) {
            //TODO 设置图标
            holder.itemAlertContentImvIcon.setImageResource(R.drawable.no_smoke_icon);
            //
            String sensorType = recordInfo.getSensorType();
            try {
                StringBuilder stringBuilder = new StringBuilder();
                SensorTypeStyles sensorTypeStyles = PreferencesHelper.getInstance().getConfigSensorType(sensorType);
                if (sensorTypeStyles != null) {
                    String trueMean = sensorTypeStyles.getTrueMean();
                    boolean bool = sensorTypeStyles.isBool();
                    if (bool) {
                        stringBuilder.append(trueMean).append("，").append(mContext.getString(R.string.back_to_normal));
                    } else {
//                    "电量低于预警值, 恢复正常";
////                    info = "温度 值为 " + thresholds + "°C 达到预警值";
                        String name = sensorTypeStyles.getName();
                        stringBuilder.append(name);
                        stringBuilder.append(mContext.getString(R.string.below_the_warning_value)).append("，").append(mContext.getString(R.string.back_to_normal));
                    }
                    holder.itemAlertContentTvContent.setText(stringBuilder.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.itemAlertContentTvContent.setText(WidgetUtil.getAlarmDetailInfo(sensorType, recordInfo
                        .getThresholds(), 0));
            }
            holder.llConfirm.setVisibility(View.GONE);
        } else if ("sendVoice".equals(recordInfo.getType())) {
            //TODO 设置图标
            holder.itemAlertContentImvIcon.setImageResource(R.drawable.phone_icon);
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(mContext.getString(R.string.the_system_calls_to)).append(":");

            holder.itemAlertContentTvContent.setText(appendResult(stringBuffer, 0, recordInfo.getPhoneList()));
            holder.llConfirm.setVisibility(View.GONE);
        } else if ("sendSMS".equals(recordInfo.getType())) {
            //TODO 设置图标
            holder.itemAlertContentImvIcon.setImageResource(R.drawable.msg_icon);
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mContext.getString(R.string.the_system_sends_msg_to)).append(":");
            holder.itemAlertContentTvContent.setText(appendResult(stringBuilder, 1, recordInfo.getPhoneList()));
            holder.llConfirm.setVisibility(View.GONE);
        } else if ("alarm".equals(recordInfo.getType())) {
            //TODO 设置图标
            holder.itemAlertContentImvIcon.setImageResource(R.drawable.smoke_icon);
            //
            String sensorType = recordInfo.getSensorType();
            StringBuilder stringBuilder = new StringBuilder();
            try {
                SensorTypeStyles sensorTypeStyles = PreferencesHelper.getInstance().getConfigSensorType(sensorType);
                if (sensorTypeStyles!=null){
                    boolean bool = sensorTypeStyles.isBool();
                    if (bool) {
//                    info = "烟雾浓度高，设备预警";
                        int thresholds = recordInfo.getThresholds();
                        switch (thresholds) {
                            case 1:
                                //true
                                String trueMean = sensorTypeStyles.getTrueMean();
                                stringBuilder.append(trueMean).append("，").append(mContext.getString(R.string.equipment_warning));
                                break;
                            case 0:
                                //false
                                String falseMean = sensorTypeStyles.getFalseMean();
                                stringBuilder.append(falseMean).append("，").append(mContext.getString(R.string.equipment_warning));
                                break;
                        }

                    } else {
                        String name = sensorTypeStyles.getName();
                        stringBuilder.append(name);
////                    info = "温度 值为 " + thresholds + "°C 达到预警值";
                        int thresholds = recordInfo.getThresholds();
                        String unit = sensorTypeStyles.getUnit();
                        stringBuilder.append(" ").append(mContext.getString(R.string.value_is)).append(" ").append(thresholds).append(unit).append(" ").append(mContext.getString(R.string.achieve_warning_value));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                stringBuilder.append(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(), recordInfo.getThresholds(), 1));
            }
            //

            String alarmDetailInfo = stringBuilder.toString();
            SpannableString spannableString = new SpannableString(alarmDetailInfo);
            holder.itemAlertContentTvContent.setText(changTextColor(alarmDetailInfo, alarmDetailInfo, spannableString, R.color.c_252525));
            holder.llConfirm.setVisibility(View.GONE);
        }

    }

    /**
     * @param stringBuffer
     * @param type         0表示 系统拨打电话至 1表示 系统发送短信至
     * @param phoneList
     * @return
     */
    private SpannableString appendResult(StringBuilder stringBuffer, int type, AlarmInfo.RecordInfo.Event[] phoneList) {
        //情况集合
        receiveStatusListClear();
        for (int i = 0; i < phoneList.length; i++) {
            AlarmInfo.RecordInfo.Event event = phoneList[i];
            switch (event.getReciveStatus()) {
                case 0:
                    receiveStautus0.add(event);
                    break;
                case 1:
                    receiveStautus1.add(event);
                    break;
                case 2:
                    receiveStautus2.add(event);
                    break;
                default:
                    receiveStautus3.add(event);
                    break;
            }

        }
        List[] receiveStautus = {receiveStautus0, receiveStautus1, receiveStautus2, receiveStautus3};
        StringBuilder temp = null;
        ArrayList<StringBuilder> tempList = new ArrayList<>();
        for (List stautus : receiveStautus) {
            if (stautus.size() > 0) {
                temp = new StringBuilder();
                for (int i = 0; i < stautus.size(); i++) {
                    String number = ((AlarmInfo.RecordInfo.Event) stautus.get(i)).getNumber();
                    if (i != (stautus.size() - 1)) {
                        temp.append(" ").append(number).append(" ;");
                    } else {
                        temp.append(" ").append(number).append(" ");
                    }
                }
                if (type == 0) {
                    switch (((AlarmInfo.RecordInfo.Event) stautus.get(0)).getReciveStatus()) {
                        case 0:
                            stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.telephone_call));
                            break;
                        case 1:
                            stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.telephone_answer_success));
                            break;
                        case 2:
                            stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.telephone_answer_failed));
                            break;
                        default:
                            stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.telephone_answer_unknow));
                            break;
                    }
                } else if (type == 1) {
                    switch (((AlarmInfo.RecordInfo.Event) stautus.get(0)).getReciveStatus()) {
                        case 0:
                            stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.sms_sending));
                            break;
                        case 1:
                            stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.sms_received_successfully));
                            break;
                        case 2:
                            stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.sms_received_failed));
                            break;
                        default:
                            stringBuffer.append(temp).append(" ").append(mContext.getString(R.string.sms_received_unknow));
                            break;
                    }
                }
                tempList.add(temp);
            }
        }
        SpannableString spannableString = new SpannableString(stringBuffer);

        for (StringBuilder sb : tempList) {
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.c_252525));
            int i = stringBuffer.indexOf(sb.toString());
            spannableString.setSpan(foregroundColorSpan, i, i + sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        return spannableString;
    }

    private void receiveStatusListClear() {
        receiveStautus0.clear();
        receiveStautus1.clear();
        receiveStautus2.clear();
        receiveStautus3.clear();
    }

    private SpannableString changTextColor(String content, String temp, SpannableString spannableString, @ColorRes int color) {

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(color));
        int i = content.indexOf(temp);
        spannableString.setSpan(foregroundColorSpan, i, i + temp.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    @Override
    public int getItemCount() {
        return timeShaftParentBeans.size();
    }

    class AlertLogRcContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_alert_content_imv_icon)
        ImageView itemAlertContentImvIcon;
        @BindView(R.id.item_alert_content_tv_content)
        TextView itemAlertContentTvContent;
        @BindView(R.id.item_alert_content_tv_time)
        TextView itemAlertContentTvTime;
        @BindView(R.id.item_alarm_detail_child_alarm_result)
        TextView itemAlarmDetailChildAlarmResult;
        @BindView(R.id.item_alarm_detail_child_alarm_type)
        TextView itemAlarmDetailChildAlarmType;
        @BindView(R.id.item_alarm_detail_child_alarm_place)
        TextView itemAlarmDetailChildAlarmPlace;
        @BindView(R.id.item_alarm_detail_child_alarm_remarks)
        TextView itemAlarmDetailChildAlarmRemarks;
        @BindView(R.id.rv_alarm_photo)
        RecyclerView rvAlarmPhoto;
        @BindView(R.id.ll_confirm)
        LinearLayout llConfirm;

        AlertLogRcContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
