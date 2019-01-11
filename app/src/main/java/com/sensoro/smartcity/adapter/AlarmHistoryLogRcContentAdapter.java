package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmHistoryLogRcContentAdapter extends RecyclerView.Adapter<AlarmHistoryLogRcContentAdapter.HistoryLogHolder> implements Constants {
    private Context mContext;
    private OnAlarmHistoryLogConfirmListener listener;
    private final List<DeviceAlarmLogInfo> mData = new ArrayList<>();

    public AlarmHistoryLogRcContentAdapter(Context context) {
        mContext = context;
    }

    public void setOnAlarmHistoryLogConfirmListener(OnAlarmHistoryLogConfirmListener onAlarmHistoryLogConfirmListener) {
        listener = onAlarmHistoryLogConfirmListener;
    }

    public void updateAdapter(List<DeviceAlarmLogInfo> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<DeviceAlarmLogInfo> getData() {
        return mData;
    }

    public interface OnAlarmHistoryLogConfirmListener {
        void onHistoryConfirm(View v, int position);
    }

    @Override
    public HistoryLogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_alarm_history_log, parent, false);
        return new HistoryLogHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final HistoryLogHolder holder, int position) {

        DeviceAlarmLogInfo alarmLogInfo = mData.get(position);
        if (alarmLogInfo != null) {
            //
            switch (alarmLogInfo.getDisplayStatus()) {
                case DISPLAY_STATUS_CONFIRM:
                    holder.tvAlarmHistoryLogConfirm.setVisibility(View.VISIBLE);
                    holder.tvAlarmHistoryLogConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) {
                                listener.onHistoryConfirm(v, holder.getAdapterPosition());
                            }
                        }
                    });
                    break;
                case DISPLAY_STATUS_ALARM:
                case DISPLAY_STATUS_MIS_DESCRIPTION:
                case DISPLAY_STATUS_TEST:
                case DISPLAY_STATUS_RISKS:
                    holder.tvAlarmHistoryLogConfirm.setVisibility(View.GONE);
                    break;
            }
            holder.tvAlarmHistoryLogContent.setText(DateUtil.getStrTimeToday(mContext, alarmLogInfo.getCreatedTime(), 0) + mContext.getString(R.string.occur_alarmed));
        }
//        AlarmInfo.RecordInfo[] recordInfoArray = alarmLogInfo.getRecords();
//        boolean isAlarm = false;
//        for (AlarmInfo.RecordInfo recordInfo : recordInfoArray) {
////                AlarmInfo.RecordInfo.Event[] event = recordInfo.getPhoneList();
//            String type = recordInfo.getType();
//            if ("alarm".equals(type)) {


//                break;
//            }
//        }
        //
//        holder.tvAlarmHistoryLogContent.setText("今天-------->>>>" + position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class HistoryLogHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_alarm_history_log_content)
        TextView tvAlarmHistoryLogContent;
        @BindView(R.id.tv_alarm_history_log_confirm)
        TextView tvAlarmHistoryLogConfirm;

        HistoryLogHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
