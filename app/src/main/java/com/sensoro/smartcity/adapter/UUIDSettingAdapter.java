package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.UuidSettingModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UUIDSettingAdapter extends RecyclerView.Adapter<UUIDSettingAdapter.UUIDSettingViewHolder> {
    private final Activity mActivity;
    final List<UuidSettingModel> mList = new ArrayList<>();
    private UuidSettingModel currentUuidSettingModel;

    public UUIDSettingAdapter(Activity activity) {
        mActivity = activity;
    }

    @NonNull
    @Override
    public UUIDSettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_adapter_uuid_setting, parent, false);
        return new UUIDSettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UUIDSettingViewHolder holder, int position) {
        UuidSettingModel uuidSettingModel = mList.get(position);
        if (!TextUtils.isEmpty(uuidSettingModel.name)) {
            holder.tvUuidItemName.setText(uuidSettingModel.name);
        }
        if (!TextUtils.isEmpty(uuidSettingModel.uuid)) {
            holder.tvUuidItemUuid.setText(uuidSettingModel.uuid);
        }
        holder.ivUuidItemCheck.setVisibility(uuidSettingModel.isCheck ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mList.size(); i++) {
                    UuidSettingModel currentUuidSettingModel = mList.get(i);
                    currentUuidSettingModel.isCheck = i == position;
                }
                currentUuidSettingModel = mList.get(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<UuidSettingModel> notifications) {
        mList.clear();
        mList.addAll(notifications);
        notifyDataSetChanged();
    }

    public UuidSettingModel getCurrentUuidSettingModel() {
        if (currentUuidSettingModel == null) {
            if (mList.size() > 0) {
                return mList.get(0);
            }
        }
        return currentUuidSettingModel;
    }

    class UUIDSettingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_uuid_item_name)
        TextView tvUuidItemName;
        @BindView(R.id.tv_uuid_item_uuid)
        TextView tvUuidItemUuid;
        @BindView(R.id.iv_uuid_item_check)
        ImageView ivUuidItemCheck;

        UUIDSettingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
