package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.AutoSplitTextView;

import java.util.List;

import static android.text.style.DynamicDrawableSpan.ALIGN_BASELINE;

/**
 * Created by sensoro on 17/11/15.
 */

public class TimerShaftAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater = null;
    private Context mContext;
    private OnGroupItemClickListener itemClickListener;
    private List<AlarmInfo.RecordInfo> timeShaftParentBeans;

    public TimerShaftAdapter(Context context, List<AlarmInfo.RecordInfo> timeShaftBeans, OnGroupItemClickListener
            listener) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.timeShaftParentBeans = timeShaftBeans;
        this.mContext = context;
        this.itemClickListener = listener;
    }

    public void setData(List<AlarmInfo.RecordInfo> recordInfoList) {
        this.timeShaftParentBeans = recordInfoList;
    }

    @Override
    public int getGroupCount() {
        return timeShaftParentBeans.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        AlarmInfo.RecordInfo.Event[] events = timeShaftParentBeans.get(groupPosition).getPhoneList();
        if (events != null) {
            return events.length;
        } else {
            if (!TextUtils.isEmpty(timeShaftParentBeans.get(groupPosition).getRemark())) {
                return 1;
            }
            return 0;
        }

    }

    @Override
    public Object getGroup(int groupPosition) {
        return timeShaftParentBeans.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        AlarmInfo.RecordInfo.Event[] events = timeShaftParentBeans.get(groupPosition).getPhoneList();
        if (events != null) {
            return events[childPosition];
        } else {
            return null;
        }

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_alarm_detail_parent, null);
            groupHolder = new GroupViewHolder();
            groupHolder.ivStatus = (ImageView) convertView.findViewById(R.id.item_alarm_detail_parent_status);
            groupHolder.tvDay = (TextView) convertView.findViewById(R.id.item_alarm_detail_parent_time);
            groupHolder.tvTitle = (TextView) convertView.findViewById(R.id.item_alarm_detail_parent_title);
            groupHolder.lineView = convertView.findViewById(R.id.item_alarm_detail_parent_line);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupViewHolder) convertView.getTag();
        }
        AlarmInfo.RecordInfo recordInfo = timeShaftParentBeans.get(groupPosition);
        String time = DateUtil.getFullParseDate(recordInfo.getUpdatedTime());
        groupHolder.tvDay.setText(time);
        groupHolder.lineView.setVisibility(View.VISIBLE);
        if ("confirm".equals(recordInfo.getType())) {
            String[] confirmStatusArray = {"待确认", "真实预警", "误报", "巡检/测试"};
            String source = recordInfo.getSource();
            String confirm_text = null;
            if ("auto".equals(source)) {
                confirm_text = "48小时无人确认，系统自动确认为巡检/测试";
            } else if ("app".equals(source)) {
                confirm_text = "联系人[" + recordInfo.getName() + "]" + "通过App端确认本次预警类型为:" +
                        confirmStatusArray[recordInfo.getDisplayStatus()];
            } else if ("platform".equals(source)) {
                confirm_text = "联系人[" + recordInfo.getName() + "]" + "通过Web端确认本次预警类型为:" +
                        confirmStatusArray[recordInfo.getDisplayStatus()];
            }
            String remark = recordInfo.getRemark();
            if (!TextUtils.isEmpty(remark)) {
                final StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(confirm_text + "\n");
                stringBuffer.append("备注 ");
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuffer.toString());
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R
                        .color.popup_selected_text_color));
                int start = stringBuffer.length() - 3;
                int end = stringBuffer.length();
                spannableStringBuilder.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        itemClickListener.onGroupItemClick(groupPosition, isExpanded);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                int drawableId = R.mipmap.ic_pack_down;
                if (isExpanded) {
                    drawableId = R.mipmap.ic_pack_up;
                }
                spannableStringBuilder.setSpan(new ImageSpan(mContext, drawableId, ALIGN_BASELINE), end - 1, end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable
                        .shape_status_progress));
                groupHolder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
                groupHolder.tvTitle.setText(spannableStringBuilder);
            } else {
                groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable
                        .shape_status_progress));
                groupHolder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
                groupHolder.tvTitle.setText(confirm_text);
//            groupHolder.tvTitle.setText(ToDBC(confirm_text));
                groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable
                        .shape_status_normal));
            }


        } else if ("recovery".equals(recordInfo.getType())) {
            groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shape_status_normal));
            groupHolder.tvTitle.setText(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(), recordInfo
                    .getThresholds(), 0));
        } else if ("sendVoice".equals(recordInfo.getType())) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("系统电话至:");
            int count = recordInfo.getPhoneList().length > 3 ? 3 : recordInfo.getPhoneList().length;
            for (int i = 0; i < count; i++) {
                AlarmInfo.RecordInfo.Event event = recordInfo.getPhoneList()[i];
                if (i == (count - 1)) {
                    stringBuffer.append(event.getName() + "等" + recordInfo.getPhoneList().length + "人");
                } else {
                    stringBuffer.append(event.getName() + ",");
                }
            }
            stringBuffer.append(" 查看电话接听结果 ");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuffer);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R
                    .color.popup_selected_text_color));
            int start = stringBuffer.length() - 9;
            int end = stringBuffer.length();
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    itemClickListener.onGroupItemClick(groupPosition, isExpanded);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableStringBuilder.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int drawableId = R.mipmap.ic_pack_down;
            if (isExpanded) {
                drawableId = R.mipmap.ic_pack_up;
            }
            spannableStringBuilder.setSpan(new ImageSpan(mContext, drawableId, ALIGN_BASELINE), end - 1, end, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
            groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable
                    .shape_status_progress));
            groupHolder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            groupHolder.tvTitle.setText(spannableStringBuilder);
        } else if ("sendSMS".equals(recordInfo.getType())) {

            final StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("系统发送短信至:");
            int count = recordInfo.getPhoneList().length > 3 ? 3 : recordInfo.getPhoneList().length;
            for (int i = 0; i < count; i++) {
                AlarmInfo.RecordInfo.Event event = recordInfo.getPhoneList()[i];
                if (i == (count - 1)) {
                    stringBuffer.append(event.getName() + "等" + recordInfo.getPhoneList().length + "人");
                } else {
                    stringBuffer.append(event.getName() + ",");
                }
            }
            stringBuffer.append(" 查看短信发送结果 ");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuffer.toString());
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R
                    .color.popup_selected_text_color));
            int start = stringBuffer.length() - 9;
            int end = stringBuffer.length();
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    itemClickListener.onGroupItemClick(groupPosition, isExpanded);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int drawableId = R.mipmap.ic_pack_down;
            if (isExpanded) {
                drawableId = R.mipmap.ic_pack_up;
            }
            spannableStringBuilder.setSpan(new ImageSpan(mContext, drawableId, ALIGN_BASELINE), end - 1, end, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE);
            groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable
                    .shape_status_progress));
            groupHolder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            groupHolder.tvTitle.setText(spannableStringBuilder);
        } else if ("alarm".equals(recordInfo.getType())) {
            groupHolder.lineView.setVisibility(View.GONE);
            groupHolder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shape_status_alarm));
//            if (WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(),recordInfo.getThresholds(), 1) == null) {
//                groupHolder.tvTitle.setText("未知传感器 值为 " + recordInfo.getThresholds() + " 达到预警值");
//            } else {
//                groupHolder.tvTitle.setText(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(),recordInfo
// .getThresholds(), 1));
//            }
            groupHolder.tvTitle.setText(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(), recordInfo
                    .getThresholds(), 1));

        }
        if (isExpanded) {
            convertView.setPadding(0, 0, 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 50);
        }

        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup
            parent) {
        ChildViewHolder childHolder = null;
        AlarmInfo.RecordInfo.Event childBean = (AlarmInfo.RecordInfo.Event) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_alarm_detail_child, null);
            childHolder = new ChildViewHolder();
            childHolder.tvTitle = (AutoSplitTextView) convertView.findViewById(R.id.item_alarm_detail_child_title);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildViewHolder) convertView.getTag();
        }
        if ("confirm".equals(timeShaftParentBeans.get(groupPosition).getType())) {
            String remark = timeShaftParentBeans.get(groupPosition).getRemark();
            if (!TextUtils.isEmpty(remark)) {
                childHolder.tvTitle.setText(remark);
            }
        }
        if (childBean != null) {
            AlarmInfo.RecordInfo recordInfo = timeShaftParentBeans.get(groupPosition);
            String recordType = recordInfo.getType();
            String receiveTime = childBean.getReceiveTime();
            if ("sendVoice".equals(recordType)) {
                String statusString = "电话接收中";
                if (childBean.getReciveStatus() == 1) {
                    statusString = "电话接收成功";
                } else if (childBean.getReciveStatus() == 2) {
                    statusString = "电话接收失败";
                }
                childHolder.tvTitle.setText(WidgetUtil.distinguishContacts(childBean.getSource()) + " - " + childBean
                        .getName() + "(" + childBean.getNumber() + ")于"
                        + DateUtil.parseDateToString(receiveTime) + statusString);
            } else if ("sendSMS".equals(recordType)) {
                String statusString = "短信接收中";
                if (childBean.getReciveStatus() == 1) {
                    statusString = "短信接收成功";
                } else if (childBean.getReciveStatus() == 2) {
                    statusString = "短信接收失败";
                }
                childHolder.tvTitle.setText(WidgetUtil.distinguishContacts(childBean.getSource()) + " - " + childBean
                        .getName() + "(" + childBean.getNumber() + ")于"
                        + DateUtil.parseDateToString(receiveTime) + statusString);
            }

        }
        return convertView;
    }

    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    public interface OnGroupItemClickListener {
        void onGroupItemClick(int position, boolean isExpanded);
    }

    private static class GroupViewHolder {
        ImageView ivStatus;
        TextView tvDay;
        View lineView;
        TextView tvTitle;
    }

    private class ChildViewHolder {
        AutoSplitTextView tvTitle;
    }


}
