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
import com.sensoro.smartcity.util.DateUtil;
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
        String time = DateUtil.getStrTimeToday(recordInfo.getUpdatedTime(), 0);
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
                confirm_text = day + "小时 无人确认，系统自动确认为: 测试/巡检";
                SpannableString spannableString = new SpannableString(confirm_text);
                String temp = day+"小时";
                changTextColor(confirm_text,temp,spannableString,R.color.c_252525);
                temp = "测试/巡检";
                changTextColor(confirm_text,temp,spannableString,R.color.c_8058a5);
                holder.itemAlertContentTvContent.setText(spannableString);
            } else if ("app".equals(source)) {
                confirm_text = "联系人 [" + recordInfo.getName() + "] " + "通过 App 确认本次预警类型为:\n" +
                        confirmStatusArray[recordInfo.getDisplayStatus()];
                //用span改变字体颜色,换行 用\n
//            String content = "联系人[高鹏]通过 平台 确认本次预警类型为：\n安全隐患";
                SpannableString spannableString = new SpannableString(confirm_text);
                // 改变高鹏 颜色
                String temp = "[" + recordInfo.getName() + "]";
                changTextColor(confirm_text, temp, spannableString, R.color.c_131313);
                //改变安全隐患颜色
                temp = confirmStatusArray[recordInfo.getDisplayStatus()];
                changTextColor(confirm_text, temp, spannableString,confirmStatusTextColorArray[recordInfo.getDisplayStatus()]);

                holder.itemAlertContentTvContent.setText(spannableString);
            } else if ("platform".equals(source)) {
                confirm_text = "联系人[" + recordInfo.getName() + "]" + "通过 Web 确认本次预警类型为:\n" +
                        confirmStatusArray[recordInfo.getDisplayStatus()];
                //用span改变字体颜色,换行 用\n
//            String content = "联系人[高鹏]通过 平台 确认本次预警类型为：\n安全隐患";
                SpannableString spannableString = new SpannableString(confirm_text);
                // 改变高鹏 颜色
                String temp = "[" + recordInfo.getName() + "]";
                changTextColor(confirm_text, temp, spannableString, R.color.c_252525);
                //改变安全隐患颜色
                temp = confirmStatusArray[recordInfo.getDisplayStatus()];
                changTextColor(confirm_text, temp, spannableString, confirmStatusTextColorArray[recordInfo.getDisplayStatus()]);

                holder.itemAlertContentTvContent.setText(spannableString);
            }

            //
            holder.llConfirm.setVisibility(View.VISIBLE);
            //预警结果
            int displayStatus = recordInfo.getDisplayStatus();
            StringBuilder stringBuilder = new StringBuilder();
            holder.itemAlarmDetailChildAlarmResult.setText(stringBuilder.append(confirmStatusArray[displayStatus]).append("(").append(confirmAlarmResultInfoArray[displayStatus]).append(")").toString());
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
            holder.itemAlertContentTvContent.setText(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(), recordInfo
                    .getThresholds(), 0));
            holder.llConfirm.setVisibility(View.GONE);
        } else if ("sendVoice".equals(recordInfo.getType())) {
            //TODO 设置图标
            holder.itemAlertContentImvIcon.setImageResource(R.drawable.phone_icon);
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append("系统拨打电话至:");

            holder.itemAlertContentTvContent.setText(appendResult(stringBuffer,0,recordInfo.getPhoneList()));
            holder.llConfirm.setVisibility(View.GONE);
//            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuffer);
//            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R
//                    .color.popup_selected_text_color));
//            int start = stringBuffer.length() - 9;
//            int end = stringBuffer.length();
//            spannableStringBuilder.setSpan(new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    itemClickListener.onGroupItemClick(groupPosition, isExpanded);
//                }
//
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setUnderlineText(false);
//                }
//            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            spannableStringBuilder.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            int drawableId = R.mipmap.ic_pack_down;
//            if (isExpanded) {
//                drawableId = R.mipmap.ic_pack_up;
//            }
//            spannableStringBuilder.setSpan(new ImageSpan(mContext, drawableId, ALIGN_BASELINE), end - 1, end, Spanned
//                    .SPAN_EXCLUSIVE_EXCLUSIVE);
//            groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable
//                    .shape_status_progress));
//            groupHolder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
//            groupHolder.tvTitle.setText(spannableStringBuilder);
        } else if ("sendSMS".equals(recordInfo.getType())) {
            //TODO 设置图标
            holder.itemAlertContentImvIcon.setImageResource(R.drawable.msg_icon);
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("系统发送短信至:");
            holder.itemAlertContentTvContent.setText(appendResult(stringBuilder,1,recordInfo.getPhoneList()));
            holder.llConfirm.setVisibility(View.GONE);
//            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder.toString());
//            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R
//                    .color.popup_selected_text_color));
//            int start = stringBuilder.length() - 9;
//            int end = stringBuilder.length();
//            spannableStringBuilder.setSpan(new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    itemClickListener.onGroupItemClick(groupPosition, isExpanded);
//                }
//
//                @Override
//                public void updateDrawState(TextPaint ds) {
//                    super.updateDrawState(ds);
//                    ds.setUnderlineText(false);
//                }
//            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannableStringBuilder.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            int drawableId = R.mipmap.ic_pack_down;
//            if (isExpanded) {
//                drawableId = R.mipmap.ic_pack_up;
//            }
//            spannableStringBuilder.setSpan(new ImageSpan(mContext, drawableId, ALIGN_BASELINE), end - 1, end, Spanned
//                    .SPAN_EXCLUSIVE_EXCLUSIVE);
//            groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable
//                    .shape_status_progress));
//            groupHolder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
//            groupHolder.tvTitle.setText(spannableStringBuilder);
        } else if ("alarm".equals(recordInfo.getType())) {
            //TODO 设置图标
            holder.itemAlertContentImvIcon.setImageResource(R.drawable.smoke_icon);
            String alarmDetailInfo = WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(), recordInfo.getThresholds(), 1);
            SpannableString spannableString = new SpannableString(alarmDetailInfo);
            holder.itemAlertContentTvContent.setText(changTextColor(alarmDetailInfo, alarmDetailInfo, spannableString, R.color.c_252525));
            holder.llConfirm.setVisibility(View.GONE);
//            groupHolder.lineView.setVisibility(View.GONE);
//            groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shape_status_alarm));
////            if (WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(),recordInfo.getThresholds(), 1) == null) {
////                groupHolder.tvAlarmResult.setEditText("未知传感器 值为 " + recordInfo.getThresholds() + " 达到预警值");
////            } else {
////            }
//            groupHolder.tvTitle.setText(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(), recordInfo
//                    .getThresholds(), 1));


        }
        //

    }

    /**
     *
     * @param receiveStautus
     * @param stringBuffer
     * @param type 0表示 系统拨打电话至 1表示 系统发送短信至
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
                    String number = ((AlarmInfo.RecordInfo.Event)stautus.get(i)).getNumber();
                    if (i != (stautus.size() - 1)) {
                        temp.append(" "+number + " ;");
                    } else {
                        temp.append(" "+number + " ");
                    }
                }
                if(type == 0){
                    switch (((AlarmInfo.RecordInfo.Event)stautus.get(0)).getReciveStatus()){
                        case 0:
                            stringBuffer.append(temp).append(" 电话拨打中");
                            break;
                        case 1:
                            stringBuffer.append(temp).append(" 电话接听成功");
                            break;
                        case 2:
                            stringBuffer.append(temp).append(" 电话接听失败");
                            break;
                        default:
                            stringBuffer.append(temp).append(" 电话接听结果未知");
                            break;
                    }
                }else if (type == 1){
                    switch (((AlarmInfo.RecordInfo.Event)stautus.get(0)).getReciveStatus()){
                        case 0:
                            stringBuffer.append(temp).append(" 短信发送中");
                            break;
                        case 1:
                            stringBuffer.append(temp).append(" 短信接收成功");
                            break;
                        case 2:
                            stringBuffer.append(temp).append(" 短信接收失败");
                            break;
                        default:
                            stringBuffer.append(temp).append(" 短信接收结果未知");
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
