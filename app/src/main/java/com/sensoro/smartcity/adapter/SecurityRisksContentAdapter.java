package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.SecurityRisksAdapterModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecurityRisksContentAdapter extends RecyclerView.Adapter<SecurityRisksContentAdapter.SecurityRisksContentHolder> {

    //普通类型的item
    private static final int VIEW_TYPE_CONTENT = 1;
    //添加新条目的item
    private static final int VIEW_TYPE_ADD_ITEM = 2;
    private final Context mContext;
    private ArrayList<SecurityRisksAdapterModel> list;
    private SecurityRisksContentClickListener mListener;

    public SecurityRisksContentAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public SecurityRisksContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SecurityRisksContentHolder securityRisksContentHolder;
        if (VIEW_TYPE_ADD_ITEM == viewType) {
            View viewAddItem = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_add_item, parent, false);
            securityRisksContentHolder = new SecurityRisksContentHolder(viewAddItem);
            securityRisksContentHolder.tvAddAdapterSecurityRisks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAddItemClick();
                    }
                }
            });
        } else {
            View viewContent = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_content, parent, false);
            securityRisksContentHolder = new SecurityRisksContentHolder(viewContent);
            securityRisksContentHolder.tvLocationNameAdapterSecurityRisks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onLocationClick((Integer) v.getTag());
                    }
                }
            });
            securityRisksContentHolder.rvBehaviorsAdapterSecurityRisks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onBehaviorClick((Integer) v.getTag());
                    }
                }
            });

        }

        return securityRisksContentHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityRisksContentHolder holder, int position) {
        holder.tvLocationNameAdapterSecurityRisks.setTag(position);
        if (getItemViewType(position) == VIEW_TYPE_ADD_ITEM) {
                holder.tvAddAdapterSecurityRisks.setTag(position);
        }else{
            holder.tvLocationNameAdapterSecurityRisks.setTag(position);
            holder.rvBehaviorsAdapterSecurityRisks.setTag(position);

            SecurityRisksAdapterModel model = list.get(position);
            if (!TextUtils.isEmpty(model.location)) {
                holder.tvLocationNameAdapterSecurityRisks.setText(model.location);
            }

            SecurityRisksTagAdapter securityRisksTagAdapter = new SecurityRisksTagAdapter();


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItemCount() > 19) {
            return VIEW_TYPE_CONTENT;
        } else {
            if (position == getItemCount() - 1) {
                return VIEW_TYPE_ADD_ITEM;
            } else {
                return VIEW_TYPE_CONTENT;
            }
        }
    }

    public void updateData(List<SecurityRisksAdapterModel> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnSecurityRisksItemClickListener(SecurityRisksContentClickListener listener){
        mListener = listener;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class SecurityRisksContentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_location_adapter_security_risks)
        TextView tvLocationAdapterSecurityRisks;
        @BindView(R.id.iv_location_oval_adapter_security_risks)
        ImageView ivLocationOvalAdapterSecurityRisks;
        @BindView(R.id.tv_location_name_adapter_security_risks)
        TextView tvLocationNameAdapterSecurityRisks;
        @BindView(R.id.view_divider_adapter_security_risks)
        View viewDividerAdapterSecurityRisks;
        @BindView(R.id.tv_behavior_adapter_security_risks)
        TextView tvBehaviorAdapterSecurityRisks;
        @BindView(R.id.iv_behavior_oval_adapter_security_risks)
        ImageView ivBehaviorOvalAdapterSecurityRisks;
        @BindView(R.id.rv_behaviors_adapter_security_risks)
        RecyclerView rvBehaviorsAdapterSecurityRisks;
        @BindView(R.id.tv_add_adapter_security_risks)
        TextView tvAddAdapterSecurityRisks;

        SecurityRisksContentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    interface SecurityRisksContentClickListener{
        void onLocationClick(int position);

        void onBehaviorClick(int position);

        void onAddItemClick();

    }
}
