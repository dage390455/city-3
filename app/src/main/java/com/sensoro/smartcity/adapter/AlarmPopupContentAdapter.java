package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmPopupContentAdapter extends RecyclerView.Adapter<AlarmPopupContentAdapter.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<AlarmPopupModel.AlarmPopupSubModel> mList = new ArrayList<>();

    public AlarmPopupContentAdapter(Activity context) {
        mContext = context;
    }


    public List<AlarmPopupModel.AlarmPopupSubModel> getData() {
        return mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_alarm_popup_content, parent, false);
        return new MyViewHolder(inflate);
    }

    public void updateData(final List<AlarmPopupModel.AlarmPopupSubModel> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AlarmPopupModel.AlarmPopupSubModel alarmPopupSubModel = mList.get(position);
        boolean isRequire = alarmPopupSubModel.isRequire;
        String title = alarmPopupSubModel.title;
        if (!TextUtils.isEmpty(title)){
            StringBuilder stringBuilder = new StringBuilder(title);
            if (isRequire) {
                holder.tvAlarmPopupAlarmTitle.setText(stringBuilder.append(" (").append(mContext.getString(R.string.required)).append(")").toString());
            } else {
                holder.tvAlarmPopupAlarmTitle.setText(stringBuilder.toString());
            }
        }

        if (TextUtils.isEmpty(alarmPopupSubModel.tips)) {
            holder.tvAlarmPopupAlarmTitleTip.setVisibility(View.GONE);
            holder.rvAlarmPopupAlarmContent.setVisibility(View.VISIBLE);
            AlarmPopupSubTagAdapter alarmPopupSubTagAdapter = new AlarmPopupSubTagAdapter(mContext);
            holder.rvAlarmPopupAlarmContent.setAdapter(alarmPopupSubTagAdapter);
            alarmPopupSubTagAdapter.updateAdapter(alarmPopupSubModel.subTags);
        } else {
            holder.tvAlarmPopupAlarmTitleTip.setVisibility(View.VISIBLE);
            holder.rvAlarmPopupAlarmContent.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_alarm_popup_alarm_title)
        TextView tvAlarmPopupAlarmTitle;
        @BindView(R.id.rv_alarm_popup_alarm_content)
        RecyclerView rvAlarmPopupAlarmContent;
        @BindView(R.id.tv_alarm_popup_alarm_title_tip)
        TextView tvAlarmPopupAlarmTitleTip;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            manager.setAutoMeasureEnabled(true);
            manager.setSmoothScrollbarEnabled(true);
            rvAlarmPopupAlarmContent.setLayoutManager(manager);
            rvAlarmPopupAlarmContent.setNestedScrollingEnabled(false);
            rvAlarmPopupAlarmContent.setHasFixedSize(true);
        }
    }
}
