package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;

import java.util.ArrayList;
import java.util.List;


public class EarlyWarningThresholdDialogUtilsAdapter extends RecyclerView.Adapter<EarlyWarningThresholdDialogUtilsAdapter
        .EarlyWarningThresholdDialogUtilsHolder> {

    private Context mContext;
    private final ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> mList = new ArrayList<>();

    public EarlyWarningThresholdDialogUtilsAdapter(Context context) {
        this.mContext = context;
    }

    public void updateList(List<EarlyWarningthresholdDialogUtilsAdapterModel> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> getData() {
        return mList;
    }

    @Override
    public EarlyWarningThresholdDialogUtilsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_early_warning_threshold_dialog, parent, false);

        return new EarlyWarningThresholdDialogUtilsHolder(view);
    }

    @Override
    public void onBindViewHolder(EarlyWarningThresholdDialogUtilsHolder holder, int position) {
        String name = mList.get(position).name;
        if (TextUtils.isEmpty(name)) {
            holder.nameTextView.setText(mContext.getString(R.string.unknown));
        } else {
            holder.nameTextView.setText(name);
        }
        String content = mList.get(position).content;
        if (TextUtils.isEmpty(content)) {
            holder.contentTextView.setText(mContext.getString(R.string.unknown));
        } else {
            holder.contentTextView.setText(content);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class EarlyWarningThresholdDialogUtilsHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView contentTextView;

        EarlyWarningThresholdDialogUtilsHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_threshold_name);
            contentTextView = (TextView) itemView.findViewById(R.id.tv_threshold_content);
        }
    }
}
