package com.sensoro.smartcity.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.contractmanager.R;
import com.sensoro.common.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ContractTemplateShowAdapter extends RecyclerView.Adapter<ContractTemplateShowAdapter
        .ContractTemplateShowViewHolder> {

    private Context mContext;
    private final ArrayList<ContractsTemplateInfo> mList = new ArrayList<>();

    public ContractTemplateShowAdapter(Context context) {
        this.mContext = context;
    }

    public void updateList(List<ContractsTemplateInfo> list) {
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

        if(position == 0){
            holder.contractItemView.setVisibility(View.GONE);
        }else{
            holder.contractItemView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ContractTemplateShowViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView contractItemShowNum;
        private final View contractItemView;

        ContractTemplateShowViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_contacts_template_show_name);
            contractItemShowNum = (TextView) itemView.findViewById(R.id.et_contract_item_show_num);
            contractItemView = itemView.findViewById(R.id.view_contract_item);
            //
        }
    }
}
