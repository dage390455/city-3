package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.ArrayList;
import java.util.List;

import static android.text.style.DynamicDrawableSpan.ALIGN_BASELINE;

/**
 * Created by sensoro on 17/11/15.
 */

public class TimerShaftAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private OnGroupItemClickListener itemClickListener;
    private OnPhotoClickListener onPhotoClickListener;
    private final List<AlarmInfo.RecordInfo> timeShaftParentBeans = new ArrayList<>();
    //
    private final String[] confirmStatusArray = {"待确认", "真实火警", "误报", "测试/巡检", "安全隐患"};
    private final String[] confirmAlarmResultInfoArray = {"", "监测点或附近发生着火，需要立即进行扑救", "无任何火情和烟雾", "相关人员主动测试发出的预警",
            "未发生着火，但现场确实存在隐患"};
    private final String[] confirmAlarmTypeArray = {"其他", "用电异常", "生产作业", "吸烟", "室内生火", "烹饪", "燃气泄漏", "人为放火", "易燃物自燃"};
    //    private final String[] confirmAlarmPlaceArray = {"其他", "小区", "工厂", "居民作坊", "仓库", "商铺店面", "商场", "出租房",};
    private final String[] confirmAlarmPlaceArray = {"其他", "小区", "工厂", "居民作坊", "仓库", "商铺店面", "商场", "出租房"};
    //
    private AlarmDetailPhotoAdapter adapter;

    public TimerShaftAdapter(Context context, OnGroupItemClickListener
            listener) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        this.itemClickListener = listener;
        adapter = new AlarmDetailPhotoAdapter(mContext);
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    public void setData(List<AlarmInfo.RecordInfo> recordInfoList) {
        this.timeShaftParentBeans.clear();
        this.timeShaftParentBeans.addAll(recordInfoList);
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
            if ("confirm".equals(timeShaftParentBeans.get(groupPosition).getType())) {
                return 1;
            } else {
                return 0;
            }
            //TODO

//            if (!TextUtils.isEmpty(timeShaftParentBeans.getInstance(groupPosition).getRemark())) {
//                return 1;
//            }

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
        //
        if ("confirm".equals(recordInfo.getType())) {
            String source = recordInfo.getSource();
            String confirm_text = null;
            if ("auto".equals(source)) {
                int day = 48;
                try {
                    long timeout = recordInfo.getTimeout();
                    day = (int) (timeout / 3600);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                confirm_text = day + "小时无人确认，系统自动确认为测试/巡检";
            } else if ("app".equals(source)) {
                confirm_text = "联系人[" + recordInfo.getName() + "]" + "通过App端确认本次预警类型为:" +
                        confirmStatusArray[recordInfo.getDisplayStatus()];
            } else if ("platform".equals(source)) {
                confirm_text = "联系人[" + recordInfo.getName() + "]" + "通过Web端确认本次预警类型为:" +
                        confirmStatusArray[recordInfo.getDisplayStatus()];
            }
            //
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(confirm_text).append("\n").append("\n");
            stringBuilder.append("详情 ");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder.toString());
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R
                    .color.popup_selected_text_color));
            int start = stringBuilder.length() - 3;
            int end = stringBuilder.length();
            spannableStringBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    itemClickListener.onGroupItemClick(groupPosition, isExpanded);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

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
                    .shape_status_normal));
            groupHolder.tvTitle.setMovementMethod(LinkMovementMethod.getInstance());
            groupHolder.tvTitle.setText(spannableStringBuilder);


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
            stringBuilder.append(" 查看短信发送结果 ");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder.toString());
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R
                    .color.popup_selected_text_color));
            int start = stringBuilder.length() - 9;
            int end = stringBuilder.length();
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
//                groupHolder.tvAlarmResult.setEditText("未知传感器 值为 " + recordInfo.getThresholds() + " 达到预警值");
//            } else {
//                groupHolder.tvAlarmResult.setEditText(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(),
// recordInfo
// .getThresholds(), 1));
//            }
            groupHolder.tvTitle.setText(WidgetUtil.getAlarmDetailInfo(recordInfo.getSensorType(), recordInfo
                    .getThresholds(), 1));

        }
        //

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
        ChildViewHolder childHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_alarm_detail_child, null);
            childHolder = new ChildViewHolder();
            childHolder.llConfirm = convertView.findViewById(R.id.ll_confirm);
            childHolder.llContact = convertView.findViewById(R.id.ll_contact);
            childHolder.tvAlarmContact = convertView.findViewById(R.id.tv_alarm_contact);
            //
            childHolder.tvAlarmResult = (TextView) convertView.findViewById(R.id
                    .item_alarm_detail_child_alarm_result);
            childHolder.tvAlarmType = (TextView) convertView.findViewById(R.id
                    .item_alarm_detail_child_alarm_type);
            childHolder.tvAlarmPlace = (TextView) convertView.findViewById(R.id
                    .item_alarm_detail_child_alarm_place);
            childHolder.tvAlarmRemark = (TextView) convertView.findViewById(R.id
                    .item_alarm_detail_child_alarm_remarks);
            childHolder.rvAlarmPhoto = convertView.findViewById(R.id.rv_alarm_photo);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildViewHolder) convertView.getTag();
        }
        AlarmInfo.RecordInfo recordInfo = timeShaftParentBeans.get(groupPosition);
        String recordType = recordInfo.getType();
        if ("confirm".equals(recordType)) {
            childHolder.llConfirm.setVisibility(View.VISIBLE);
            childHolder.llContact.setVisibility(View.GONE);
            //TODO 新布局
            //预警结果
            int displayStatus = recordInfo.getDisplayStatus();
            StringBuilder stringBuilder = new StringBuilder();
            childHolder.tvAlarmResult.setText(stringBuilder.append(confirmStatusArray[displayStatus]).append("(").append(confirmAlarmResultInfoArray[displayStatus]).append(")").toString());
            //预警成因
            int reason = recordInfo.getReason();
            childHolder.tvAlarmType.setText(confirmAlarmTypeArray[reason]);
            //预警场所
            int place = recordInfo.getPlace();
            childHolder.tvAlarmPlace.setText(confirmAlarmPlaceArray[place]);
            //备注说明
            String remark = recordInfo.getRemark();
            if (!TextUtils.isEmpty(remark)) {
                childHolder.tvAlarmRemark.setText(remark);
            }
//            final List<String> images = recordInfo.getImages();
//            if (images != null && images.size() > 0) {
//                final GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4) {
//                    @Override
//                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//                        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT);
//                    }
//                };
//                childHolder.rvAlarmPhoto.setLayoutManager(layoutManager);
//                childHolder.rvAlarmPhoto.setHasFixedSize(true);
//                adapter.setOnItemClickListener(new AlarmDetailPhotoAdapter.OnRecyclerViewItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        if (onPhotoClickListener != null) {
//                            onPhotoClickListener.onPhotoItemClick(position, images);
//                        }
//                    }
//                });
//                childHolder.rvAlarmPhoto.setAdapter(adapter);
//                //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
//                childHolder.rvAlarmPhoto.setNestedScrollingEnabled(false);
//                adapter.setImages(images);
//            }
            //
            final List<ScenesData> scenes = recordInfo.getScenes();
            if (scenes != null && scenes.size() > 0) {
                final GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4) {
                    @Override
                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                };
                childHolder.rvAlarmPhoto.setLayoutManager(layoutManager);
                childHolder.rvAlarmPhoto.setHasFixedSize(true);
                adapter.setOnItemClickListener(new AlarmDetailPhotoAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (onPhotoClickListener != null) {
                            onPhotoClickListener.onPhotoItemClick(position, scenes);
                        }
                    }
                });
                childHolder.rvAlarmPhoto.setAdapter(adapter);
                //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
                childHolder.rvAlarmPhoto.setNestedScrollingEnabled(false);
                adapter.setImages(scenes);
            }
        } else {
            AlarmInfo.RecordInfo.Event childBean = (AlarmInfo.RecordInfo.Event) getChild(groupPosition, childPosition);
            if (childBean != null) {
                childHolder.llConfirm.setVisibility(View.GONE);
                childHolder.llContact.setVisibility(View.VISIBLE);
                String receiveTime = childBean.getReceiveTime();
                if ("sendVoice".equals(recordType)) {
                    String statusString = "电话接收中";
                    if (childBean.getReciveStatus() == 1) {
                        statusString = "电话接收成功";
                    } else if (childBean.getReciveStatus() == 2) {
                        statusString = "电话接收失败";
                    }
                    final StringBuilder stringBuilder = new StringBuilder();
                    childHolder.tvAlarmContact.setText(stringBuilder.append(WidgetUtil.distinguishContacts(childBean.getSource())).append(" - ").append(childBean
                            .getName()).append("(").append(childBean.getNumber()).append(")于").append(DateUtil.parseDateToString(receiveTime)).append(statusString).toString());
                } else if ("sendSMS".equals(recordType)) {
                    String statusString = "短信接收中";
                    if (childBean.getReciveStatus() == 1) {
                        statusString = "短信接收成功";
                    } else if (childBean.getReciveStatus() == 2) {
                        statusString = "短信接收失败";
                    }
                    final StringBuilder stringBuilder = new StringBuilder();
                    childHolder.tvAlarmContact.setText(stringBuilder.append(WidgetUtil.distinguishContacts(childBean.getSource())).append(" - ").append(childBean
                            .getName()).append("(").append(childBean.getNumber()).append(")于").append(DateUtil.parseDateToString(receiveTime)).append(statusString).toString());
                }

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

    public interface OnPhotoClickListener {
        void onPhotoItemClick(int position, List<ScenesData> scenesDataList);
    }

    static class GroupViewHolder {
        ImageView ivStatus;
        TextView tvDay;
        View lineView;
        TextView tvTitle;
    }

    static class ChildViewHolder {
        LinearLayout llContact;
        LinearLayout llConfirm;
        TextView tvAlarmResult;
        TextView tvAlarmType;
        TextView tvAlarmPlace;
        TextView tvAlarmRemark;
        TextView tvAlarmContact;
        RecyclerView rvAlarmPhoto;
    }


}
