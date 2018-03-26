package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.SensoroAlarmView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangping on 2016/7/7.
 */

public class AlarmListAdapter extends BaseAdapter implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;
    private AlarmItemClickListener mListener;
    private List<DeviceAlarmLogInfo> mList = new ArrayList<>();
    private int selectedIndex;

    public AlarmListAdapter(Context context, AlarmItemClickListener listener) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    public void setData(List<DeviceAlarmLogInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public List<DeviceAlarmLogInfo> getData() {
        return mList;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        AlarmInfoViewHolder holder = null;
        if (convertView == null) {
            holder = new AlarmInfoViewHolder();
            convertView = mInflater.inflate(R.layout.item_alarm_list, null);
            holder.item_name = (TextView) convertView.findViewById(R.id.alarm_sensor_name);
            holder.item_iv_type = (ImageView) convertView.findViewById(R.id.alarm_sensor_iv_type);
            holder.item_iv_status = (ImageView) convertView.findViewById(R.id.alarm_sensor_status_iv);
            holder.item_alarm_view = (SensoroAlarmView) convertView.findViewById(R.id.alarm_sensor_call);
            holder.item_status = (TextView) convertView.findViewById(R.id.alarm_sensor_status);
            holder.item_display_status = (TextView) convertView.findViewById(R.id.alarm_sensor_display_status);
            holder.item_confirm_status = (TextView) convertView.findViewById(R.id.alarm_sensor_confirm_status);
            holder.item_date = (TextView) convertView.findViewById(R.id.alarm_sensor_date);
            convertView.setTag(holder);
        } else {
            holder = (AlarmInfoViewHolder) convertView.getTag();
        }
        DeviceAlarmLogInfo alarmLogInfo = mList.get(position);
        if (alarmLogInfo != null) {
            String default_name = alarmLogInfo.getDeviceSN() == null ? mContext.getResources().getString(R.string.unname) : alarmLogInfo.getDeviceSN();
            if (alarmLogInfo.getDeviceName() == null) {
                holder.item_name.setText(default_name);
            } else {
                holder.item_name.setText(alarmLogInfo.getDeviceName().equals("") ? default_name : alarmLogInfo.getDeviceName());
            }
            holder.item_date.setText(DateUtil.getFullParseDate(alarmLogInfo.getUpdatedTime()));
            WidgetUtil.judgeSensorType(mContext, holder.item_iv_type, alarmLogInfo.getSensorType());
            AlarmInfo.RecordInfo []recordInfoArray = alarmLogInfo.getRecords();
            for (int i = 0; i < recordInfoArray.length; i++) {
                AlarmInfo.RecordInfo recordInfo = recordInfoArray[i];
                AlarmInfo.RecordInfo.Event [] event = recordInfo.getPhoneList();
//
                if (recordInfo.getType().equals("recovery")) {
                    holder.item_iv_status.setVisibility(View.VISIBLE);
                    holder.item_alarm_view.setVisibility(View.GONE);
                    holder.item_status.setTextColor(mContext.getResources().getColor(R.color.sensoro_normal));
                    holder.item_status.setText("于" + DateUtil.getFullParseDate(recordInfo.getUpdatedTime()) + "恢复正常");
                    holder.item_iv_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shape_status_normal));
                    break;
                } else {
                    holder.item_alarm_view.setVisibility(View.VISIBLE);
                    holder.item_iv_status.setVisibility(View.GONE);
                    holder.item_iv_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shape_status_alarm));
                    holder.item_status.setTextColor(mContext.getResources().getColor(R.color.sensoro_alarm));
                    holder.item_status.setText(R.string.alarming);
                }
            }
            switch (alarmLogInfo.getDisplayStatus()) {
                case DISPLAY_STATUS_CONFIRM:
                    holder.item_confirm_status.setVisibility(View.VISIBLE);
                    holder.item_display_status.setVisibility(View.GONE);
                    holder.item_confirm_status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onItemClick(v, position);
                        }
                    });
//                    holder.item_iv_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shape_status_alarm));
//                    holder.item_status.setTextColor(mContext.getResources().getColor(R.color.sensoro_alarm));
                    break;
                case DISPLAY_STATUS_ALARM:
                    holder.item_confirm_status.setVisibility(View.GONE);
                    holder.item_display_status.setVisibility(View.VISIBLE);
                    holder.item_display_status.setText(R.string.true_alarm);
//                    holder.item_status.setTextColor(mContext.getResources().getColor(R.color.sensoro_normal));
                    break;
                case DISPLAY_STATUS_MISDESCRIPTION:
                    holder.item_confirm_status.setVisibility(View.GONE);
                    holder.item_display_status.setVisibility(View.VISIBLE);
                    holder.item_display_status.setText(R.string.misdescription);
//                    holder.item_status.setTextColor(mContext.getResources().getColor(R.color.sensoro_normal));
                    break;
                case DISPLAY_STATUS_TEST:
                    holder.item_confirm_status.setVisibility(View.GONE);
                    holder.item_display_status.setVisibility(View.VISIBLE);
                    holder.item_display_status.setText(R.string.alarm_test);
//                    holder.item_status.setTextColor(mContext.getResources().getColor(R.color.sensoro_normal));
                    break;
            }
        }

        return convertView;
    }

    public interface AlarmItemClickListener {
        void onItemClick(View view, int position);
    }


    class AlarmInfoViewHolder {

        TextView item_name;
        ImageView item_iv_status;
        ImageView item_iv_type;
        SensoroAlarmView item_alarm_view;
        TextView item_status;
        TextView item_display_status;
        TextView item_confirm_status;
        TextView item_date;

        public AlarmInfoViewHolder() {

        }
    }
}