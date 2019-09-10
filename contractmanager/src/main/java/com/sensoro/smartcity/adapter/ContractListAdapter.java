package com.sensoro.smartcity.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.common.constant.ContractOrderInfo;
import com.sensoro.contractmanager.R;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.ContractListInfo;
import com.sensoro.common.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class ContractListAdapter extends RecyclerView.Adapter<ContractListAdapter.ContractViewHolder> implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;
    private final List<ContractListInfo> mList = new ArrayList<>();

    public ContractListAdapter(Context context) {
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
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contracts_manger, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, final int position) {
        ContractListInfo contractListInfo = mList.get(position);
        int contract_type = contractListInfo.getContract_type();
        String customer_enterprise_name = contractListInfo.getCustomer_enterprise_name();
        if (TextUtils.isEmpty(customer_enterprise_name)) {
            customer_enterprise_name = mContext.getString(R.string.unknown);
        }
        String customer_name = contractListInfo.getCustomer_name();
        if (TextUtils.isEmpty(customer_name)) {
            customer_name = mContext.getString(R.string.unknown);
        }
        String customer_address = contractListInfo.getCustomer_address();
        if (TextUtils.isEmpty(customer_address)) {
            customer_address = mContext.getString(R.string.unknown);
        }
        switch (contract_type) {
            case 1:
                holder.tvContactsMangerType.setText(R.string.company);
                holder.tvContactsMangerCustom.setText(mContext.getString(R.string.legal_name));
                holder.etContactsMangerCustom.setText(customer_name);
                holder.itemTvContactsEnterprise.setText(R.string.business_merchant_name);
                holder.itemEtContactsEnterprise.setText(customer_enterprise_name);
                holder.tvContactsMangerAddress.setText(R.string.register_address);
                holder.etContactsMangerAddress.setText(customer_address);
                break;
            case 2:
                holder.tvContactsMangerType.setText(R.string.personal);
                holder.tvContactsMangerCustom.setText(R.string.owners_name);
                holder.etContactsMangerCustom.setText(customer_name);
                holder.itemTvContactsEnterprise.setText(R.string.party_a_customer_name);
                holder.itemEtContactsEnterprise.setText(customer_enterprise_name);
                holder.tvContactsMangerAddress.setText(R.string.home_address);
                holder.etContactsMangerAddress.setText(customer_address);
                break;
            default:
                holder.tvContactsMangerType.setText(R.string.company);
                holder.tvContactsMangerCustom.setText(mContext.getString(R.string.legal_name));
                holder.etContactsMangerCustom.setText(customer_name);
                holder.itemTvContactsEnterprise.setText(R.string.business_merchant_name);
                holder.itemEtContactsEnterprise.setText(customer_enterprise_name);
                holder.tvContactsMangerAddress.setText(R.string.register_address);
                holder.etContactsMangerAddress.setText(customer_address);
                break;
        }

        if (contractListInfo.isConfirmed()) {
            holder.tvContactsMangerStatus.setText(R.string.signed);
            holder.tvContactsMangerStatus.setTextColor(mContext.getResources().getColor(R.color.c_1dbb99));
            holder.tvContactsMangerStatus.setBackgroundResource(R.drawable.shape_bg_contract_stroke_1_29c_full_corner);
            holder.tvContactsMangerTime.setText(R.string.contract_signed_time);
            String confirmTime = contractListInfo.getConfirmTime();
            holder.etContactsMangerTime.setText(DateUtil.getChineseCalendar(contractListInfo.getConfirmTimestamp()));
        } else {
            holder.tvContactsMangerStatus.setText(R.string.not_signed);
            holder.tvContactsMangerStatus.setTextColor(mContext.getResources().getColor(R.color.c_ff8d34));
            holder.tvContactsMangerStatus.setBackgroundResource(R.drawable.shape_bg_contract_stroke_1_ff8d_full_corner);
            holder.tvContactsMangerTime.setText(R.string.contract_created_time);
            holder.etContactsMangerTime.setText(DateUtil.getChineseCalendar(contractListInfo.getCreatedAtTimestamp()));
        }
        String contract_number = contractListInfo.getContract_number();
        holder.tvContractNumber.setText(contract_number);
        ContractListInfo.Order order = contractListInfo.getOrder();
        if (order != null) {
            String tradeState = order.getTrade_state();
            holder.ivPayStatus.setVisibility(ContractOrderInfo.SUCCESS.equals(tradeState) ? View.VISIBLE : View.GONE);
        } else {
            holder.ivPayStatus.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ContractViewHolder extends RecyclerView.ViewHolder {

        TextView tvContactsMangerCustom;
        TextView etContactsMangerCustom;
        TextView tvContactsMangerAddress;
        TextView etContactsMangerAddress;
        TextView tvContactsMangerTime;
        TextView etContactsMangerTime;
        TextView tvContractNumber;
        TextView tvContactsMangerType;
        TextView tvContactsMangerStatus;
        ImageView ivPayStatus;
        TextView itemTvContactsEnterprise;
        TextView itemEtContactsEnterprise;

        ContractViewHolder(View itemView) {
            super(itemView);
            tvContractNumber = (TextView) itemView.findViewById(R.id.tv_contract_number);
            tvContactsMangerType = (TextView) itemView.findViewById(R.id.tv_contacts_manger_type);
            tvContactsMangerStatus = itemView.findViewById(R.id.tv_contacts_manger_status);
            ivPayStatus = itemView.findViewById(R.id.iv_pay_status);
            tvContactsMangerCustom = (TextView) itemView.findViewById(R.id.tv_contacts_manger_custom);
            etContactsMangerCustom = (TextView) itemView.findViewById(R.id.et_contacts_manger_custom);
            itemTvContactsEnterprise = itemView.findViewById(R.id.tv_contacts_manger_enterprise);
            itemEtContactsEnterprise = itemView.findViewById(R.id.et_contacts_manger_enterprise);
            //
            tvContactsMangerAddress = (TextView) itemView.findViewById(R.id.tv_contacts_manger_address);
            etContactsMangerAddress = (TextView) itemView.findViewById(R.id.et_contacts_manger_address);

            tvContactsMangerTime = (TextView) itemView.findViewById(R.id.tv_contacts_manger_time);
            etContactsMangerTime = (TextView) itemView.findViewById(R.id.et_contacts_manger_time);

        }
    }

    private OnClickListener listener;

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onClick(View v, int position);
    }
}