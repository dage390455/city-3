package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fangping on 2016/7/7.
 */

public class ContractTemplateShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private final List<ContractsTemplateInfo> mList = new ArrayList<>();

    public ContractTemplateShowAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<ContractsTemplateInfo> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public List<ContractsTemplateInfo> getData() {
        return mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contracts_template_show, parent, false);

        return new ContractTemplateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String deviceType = mList.get(position).getDeviceType();
        int deviceCount = mList.get(position).getQuantity();
        ((ContractTemplateViewHolder) holder).nameTextView.setText(deviceType);
        ((ContractTemplateViewHolder) holder).contractItemShowNum.setText(deviceCount + "");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ContractTemplateViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView contractItemShowNum;

        public ContractTemplateViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_contacts_template_show_name);
            contractItemShowNum = (TextView) itemView.findViewById(R.id.et_contract_item_show_num);
            //
        }
    }
}
