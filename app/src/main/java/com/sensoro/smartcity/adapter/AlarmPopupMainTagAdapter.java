package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.AlarmPopupModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmPopupMainTagAdapter extends RecyclerView.Adapter<AlarmPopupMainTagAdapter.MyHolder> {

    private final Context mContext;
    private final List<AlarmPopupModel.AlarmPopupTagModel> mList = new ArrayList<>();

    public AlarmPopupMainTagAdapter(Context context) {
        mContext = context;
    }

    public List<AlarmPopupModel.AlarmPopupTagModel> getSearchHistoryList() {
        return mList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_alarm_popup_tag, parent, false);
        return new MyHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        final AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = mList.get(position);
        setSelectState(holder, alarmPopupTagModel.isChose);
        if (TextUtils.isEmpty(alarmPopupTagModel.name)) {
            holder.tvAlarmPopupTag.setText(mContext.getString(R.string.unknown));
        } else {
            holder.tvAlarmPopupTag.setText(alarmPopupTagModel.name);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mList.size(); i++) {
                    AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = mList.get(i);
                    alarmPopupTagModel.isChose = i == position;
                }
                notifyDataSetChanged();
                if (observer != null) {
                    observer.onClick(v, position);
                }
            }
        });
    }

    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_alarm_popup_tag)
        TextView tvAlarmPopupTag;

        MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmPopupMainTagAdapter.MyHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            final AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = mList.get(position);
            setSelectState(holder, alarmPopupTagModel.isChose);
        }

    }

    private OnAlarmPopupMainTagAdapterItemClickObserver observer;

    public interface OnAlarmPopupMainTagAdapterItemClickObserver {
        void onClick(View v, int position);
    }

    public void setOnAlarmPopupMainTagAdapterItemClickObserver(OnAlarmPopupMainTagAdapterItemClickObserver observer) {
        this.observer = observer;
    }

    public void updateAdapter(List<AlarmPopupModel.AlarmPopupTagModel> list) {
        this.mList.clear();
        if (list != null && list.size() > 0) {
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    private void setSelectState(AlarmPopupMainTagAdapter.MyHolder holder, boolean isChose) {
        if (isChose) {
            holder.tvAlarmPopupTag.setBackground(mContext.getResources().getDrawable(R.drawable.shape_bg_solid_f3_20dp_corner));
            holder.tvAlarmPopupTag.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.tvAlarmPopupTag.setBackground(mContext.getResources().getDrawable(R.drawable.shape_bg_solid_f4_20dp_corner));
            holder.tvAlarmPopupTag.setTextColor(mContext.getResources().getColor(R.color.c_252525));
        }
    }

}
