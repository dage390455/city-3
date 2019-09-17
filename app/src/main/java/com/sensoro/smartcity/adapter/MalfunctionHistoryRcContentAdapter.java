package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MalfunctionHistoryRcContentAdapter extends RecyclerView.Adapter<MalfunctionHistoryRcContentAdapter.MalfunctionHistoryRcContentViewHolder> {
    private final Context mContext;
    private List<MalfunctionListInfo> mData = new ArrayList<>();

    public MalfunctionHistoryRcContentAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public MalfunctionHistoryRcContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_malfunction_history, parent, false);
        return new MalfunctionHistoryRcContentViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MalfunctionHistoryRcContentViewHolder holder, int position) {
        MalfunctionListInfo malfunctionListInfo = mData.get(position);
        if (malfunctionListInfo != null) {
            holder.tvMalfunctionHistoryContent.setText(String.format("%s %s", DateUtil.getStrTimeToday(mContext, malfunctionListInfo.getCreatedTime(), 0), mContext.getString(R.string.occur_malfunction_alarm)));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(v, position);
                    }
                }
            });
        }

    }

    public List<MalfunctionListInfo> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateAdapter(List<MalfunctionListInfo> mMalfunctionInfoList) {
        mData.clear();
        mData.addAll(mMalfunctionInfoList);
        notifyDataSetChanged();
    }

    class MalfunctionHistoryRcContentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_malfunction_history_content)
        TextView tvMalfunctionHistoryContent;

        MalfunctionHistoryRcContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnMalfunctionHistoryClickListener(OnMalfunctionHistoryClickListener listener) {
        this.listener = listener;
    }

    private OnMalfunctionHistoryClickListener listener;

    public interface OnMalfunctionHistoryClickListener {
        void onItemClick(View v, int position);
    }
}
