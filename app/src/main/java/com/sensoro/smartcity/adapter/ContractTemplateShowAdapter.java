package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;
import java.util.Collections;


public class ContractTemplateShowAdapter extends RecyclerView.Adapter<ContractTemplateShowAdapter
        .ContractTemplateShowViewHolder> {

    private Context mContext;
    private final ArrayList<ContractsTemplateInfo> mList = new ArrayList<>();

    public ContractTemplateShowAdapter(Context context) {
        this.mContext = context;
    }

    public void updateList(ArrayList<ContractsTemplateInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
        Collections.sort(mList);
        notifyDataSetChanged();
    }

    public ArrayList<ContractsTemplateInfo> getData() {
        return mList;
    }

    @Override
    public ContractTemplateShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contracts_template_show, parent, false);

        return new ContractTemplateShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContractTemplateShowViewHolder holder, int position) {
        String name = mList.get(position).getName();
        if (TextUtils.isEmpty(name)) {
            holder.nameTextView.setText(mList.get(position).getDeviceType());
        } else {
            holder.nameTextView.setText(name);
        }
        int deviceCount = mList.get(position).getQuantity();

        holder.contractItemShowNum.setText(String.valueOf(deviceCount));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ContractTemplateShowViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView contractItemShowNum;

        ContractTemplateShowViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_contacts_template_show_name);
            contractItemShowNum = (TextView) itemView.findViewById(R.id.et_contract_item_show_num);
            //
        }
    }
}
