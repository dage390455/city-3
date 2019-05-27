package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.AlarmPopupModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmPopupSubTagAdapter extends RecyclerView.Adapter<AlarmPopupSubTagAdapter.MyHolder> {

    private final Context mContext;
    private final List<AlarmPopupModel.AlarmPopupTagModel> mList = new ArrayList<>();

    public AlarmPopupSubTagAdapter(Context context) {
        mContext = context;
    }

    public List<AlarmPopupModel.AlarmPopupTagModel> getSearchHistoryList() {
        return mList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_alarm_popup_tag, parent, false);
        MyHolder myHolder = new MyHolder(view);
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理数据
                Integer position = (Integer) v.getTag();
                AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = mList.get(position);
                if (alarmPopupTagModel.isRequire) {
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).isChose = i == position;
                    }
                } else {
                    alarmPopupTagModel.isChose = !alarmPopupTagModel.isChose;
                    for (int i = 0; i < mList.size(); i++) {
                        if (i != position) {
                            mList.get(i).isChose = false;
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });
        return myHolder;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        //一定要设置，因为是通用的，所以要设置这个
        //
        holder.itemView.setTag(position);
        final AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = mList.get(position);
        holder.tvAlarmPopupTag.setTag(alarmPopupTagModel);
        setSelectState(holder, alarmPopupTagModel);
        if (TextUtils.isEmpty(alarmPopupTagModel.name)) {

        } else {
            holder.tvAlarmPopupTag.setText(alarmPopupTagModel.name);
        }

    }

    private void setSelectState(MyHolder holder, AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel) {
        if (alarmPopupTagModel.isChose) {
            holder.tvAlarmPopupTag.setBackground(mContext.getResources().getDrawable(alarmPopupTagModel.resDrawable));
            holder.tvAlarmPopupTag.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.tvAlarmPopupTag.setBackground(mContext.getResources().getDrawable(R.drawable.shape_bg_solid_f4_20dp_corner));
            holder.tvAlarmPopupTag.setTextColor(mContext.getResources().getColor(R.color.c_252525));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            final AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = mList.get(position);
            setSelectState(holder, alarmPopupTagModel);
        }

    }

    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_alarm_popup_tag)
        TextView tvAlarmPopupTag;

        MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void updateAdapter(List<AlarmPopupModel.AlarmPopupTagModel> list) {
        this.mList.clear();
        if (list != null && list.size() > 0) {
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }
}
