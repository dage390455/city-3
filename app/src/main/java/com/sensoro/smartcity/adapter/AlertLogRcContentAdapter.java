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
        String time = DateUtil.getStrTimeToday(recordInfo.getUpdatedTime(),1);
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
                confirm_text = day + "小时无人确认，系统自动确认为测试/巡检";

                holder.itemAlertContentTvContent.setText(confirm_text);
            } else if ("app".equals(source)) {
                confirm_text = "联系人[" + recordInfo.getName() + "]" + "通过App端确认本次预警类型为:\n" +
                        confirmStatusArray[recordInfo.getDisplayStatus()];
                //用span改变字体颜色,换行 用\n
//            String content = "联系人[高鹏]通过 平台 确认本次预警类型为：\n安全隐患";
                SpannableString spannableString = new SpannableString(confirm_text);
                // 改变高鹏 颜色
                String temp = "[" + recordInfo.getName() + "]";
                changTextColor(confirm_text, temp, spannableString, R.color.c_131313);
                //改变安全隐患颜色
                temp = confirmStatusArray[recordInfo.getDisplayStatus()];
                changTextColor(confirm_text, temp, spannableString, R.color.c_f34a4a);

                holder.itemAlertContentTvContent.setText(spannableString);
            } else if ("platform".equals(source)) {
                confirm_text = "联系人[" + recordInfo.getName() + "]" + "通过Web端确认本次预警类型为:\n" +
                        confirmStatusArray[recordInfo.getDisplayStatus()];
                //用span改变字体颜色,换行 用\n
//            String content = "联系人[高鹏]通过 平台 确认本次预警类型为：\n安全隐患";
                SpannableString spannableString = new SpannableString(confirm_text);
                // 改变高鹏 颜色
                String temp = "[" + recordInfo.getName() + "]";
                changTextColor(confirm_text, temp, spannableString, R.color.c_131313);
                //改变安全隐患颜色
                temp = confirmStatusArray[recordInfo.getDisplayStatus()];
                changTextColor(confirm_text, temp, spannableString, R.color.c_f34a4a);

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
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("系统拨打电话至:");
            int count = recordInfo.getPhoneList().length > 3 ? 3 : recordInfo.getPhoneList().length;
            for (int i = 0; i < count; i++) {
                AlarmInfo.RecordInfo.Event event = recordInfo.getPhoneList()[i];
                if (i == (count - 1)) {
                    stringBuffer.append(event.getName() + "等" + recordInfo.getPhoneList().length + "人");
                } else {
                    stringBuffer.append(event.getName() + ",");
                }
            }
            stringBuffer.append(" 电话接收成功");
            holder.itemAlertContentTvContent.setText(stringBuffer);
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
            int count = recordInfo.getPhoneList().length > 3 ? 3 : recordInfo.getPhoneList().length;
            for (int i = 0; i < count; i++) {
                AlarmInfo.RecordInfo.Event event = recordInfo.getPhoneList()[i];
                if (i == (count - 1)) {
                    stringBuilder.append(event.getName()).append("等").append(recordInfo.getPhoneList().length).append("人");
                } else {
                    stringBuilder.append(event.getName()).append(",");
                }
            }
            stringBuilder.append(" 短信接收成功 ");
            holder.itemAlertContentTvContent.setText(stringBuilder);
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
            holder.itemAlertContentTvContent.setText(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(),
                    recordInfo
                            .getThresholds(), 1));
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

    private void changTextColor(String content, String temp, SpannableString spannableString, @ColorRes int color) {
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(color));
        int i = content.indexOf(temp);
        spannableString.setSpan(foregroundColorSpan, i, i + temp.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
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
