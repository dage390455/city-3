package com.sensoro.nameplate.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.common.adapter.TagAdapter;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.server.bean.ContractListInfo;
import com.sensoro.common.widgets.TouchRecycleView;
import com.sensoro.nameplate.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class NameplateListAdapter extends RecyclerView.Adapter<NameplateListAdapter.NameplateListViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private final List<ContractListInfo> mList = new ArrayList<>();

    public NameplateListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setData(List<ContractListInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public List<ContractListInfo> getData() {
        return mList;
    }


    @NonNull
    @Override
    public NameplateListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_namplate_manger, parent, false);
        return new NameplateListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameplateListViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v, position);
                }
            }
        });
        holder.ivPayStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelete(position);
                }
            }
        });
        holder.tvNameplateAssociated.setText("已关联");
        holder.tvNameplateName.setText("name");
        holder.tvNameplateSensorCount.setText("传感器：5");
        holder.tvNameplateSn.setText("sn");
        //
        TagAdapter tagAdapter = new TagAdapter(mContext, R.color.c_252525, R.color.c_dfdfdf);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mContext, false);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.rvItemAdapterNameplateTag.setLayoutManager(layoutManager);

        holder.rvItemAdapterNameplateTag.setAdapter(tagAdapter);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            strings.add("标签 " + i + position);
        }
        tagAdapter.updateTags(strings);
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class NameplateListViewHolder extends RecyclerView.ViewHolder {

        TextView tvNameplateAssociated;
        TextView tvNameplateSensorCount;
        ImageView ivPayStatus;
        TextView tvNameplateName;
        TextView tvNameplateSn;
        TouchRecycleView rvItemAdapterNameplateTag;

        NameplateListViewHolder(View itemView) {
            super(itemView);
            tvNameplateAssociated = itemView.findViewById(R.id.tv_nameplate_associated);
            tvNameplateSensorCount = itemView.findViewById(R.id.tv_nameplate_sensor_count);
            ivPayStatus = itemView.findViewById(R.id.iv_pay_status);
            //
            tvNameplateName = itemView.findViewById(R.id.tv_nameplate_name);
            tvNameplateSn = itemView.findViewById(R.id.tv_nameplate_sn);
            rvItemAdapterNameplateTag = itemView.findViewById(R.id.rv_item_adapter_nameplate_tag);

        }
    }

    private OnNameplateListAdapterClickListener listener;

    public void setOnClickListener(OnNameplateListAdapterClickListener listener) {
        this.listener = listener;
    }

    public interface OnNameplateListAdapterClickListener {
        void onClick(View v, int position);

        void onDelete(int position);
    }
}