package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.ContractListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class ContractListAdapter extends BaseAdapter implements Constants {

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

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ContractViewHolder holder;
        if (convertView == null) {
            holder = new ContractViewHolder();
            convertView = mInflater.inflate(R.layout.item_contracts_manger, null);
            holder.itemTvLine1 = (TextView) convertView.findViewById(R.id.tv_contacts_manger_line1);
            holder.itemEtLine1 = (TextView) convertView.findViewById(R.id.et_contacts_manger_line1);
            holder.itemTvType = (TextView) convertView.findViewById(R.id.tv_contacts_manger_type);
            holder.itemTvStatus = convertView.findViewById(R.id.tv_contacts_manger_status);
            //
            holder.itemTvLine2 = (TextView) convertView.findViewById(R.id.tv_contacts_manger_line2);
            holder.itemEtLine2 = (TextView) convertView.findViewById(R.id.et_contacts_manger_line2);
            holder.itemEtNumber = (TextView) convertView.findViewById(R.id.et_contacts_manger_number);
            holder.itemEtDate = (TextView) convertView.findViewById(R.id.et_contacts_manger_time);
            holder.itemEtSignTime = convertView.findViewById(R.id.et_contacts_manger_sign_time);
            holder.itemRlContactsEnterprise = convertView.findViewById(R.id.rl_contacts_manger_enterprise);
            holder.itemTvContactsEnterprise = convertView.findViewById(R.id.tv_contacts_manger_enterprise);
            holder.itemEtContactsEnterprise = convertView.findViewById(R.id.et_contacts_manger_enterprise);
            convertView.setTag(holder);
        } else {
            holder = (ContractViewHolder) convertView.getTag();
        }
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
                holder.itemTvType.setText(R.string.business_merchant_name);
                holder.itemTvLine1.setText(mContext.getString(R.string.legal_name));
                holder.itemEtLine1.setText(customer_name);
                holder.itemTvContactsEnterprise.setText(R.string.business_merchant_name);
                holder.itemEtContactsEnterprise.setText(customer_enterprise_name);
                holder.itemTvLine2.setText(R.string.register_address);
                holder.itemEtLine2.setText(customer_address);
                break;
            case 2:
                holder.itemTvType.setText(R.string.personal);
                holder.itemTvLine1.setText(R.string.owners_name);
                holder.itemEtLine1.setText(customer_name);
                holder.itemTvContactsEnterprise.setText(R.string.party_a_customer_name);
                holder.itemEtContactsEnterprise.setText(customer_enterprise_name);
                holder.itemTvLine2.setText(R.string.home_address);
                holder.itemEtLine2.setText(customer_address);
                break;
            default:
                holder.itemTvType.setText(R.string.business_merchant_name);
                holder.itemTvLine1.setText(mContext.getString(R.string.legal_name));
                holder.itemEtLine1.setText(customer_name);
                holder.itemTvContactsEnterprise.setText(R.string.business_merchant_name);
                holder.itemEtContactsEnterprise.setText(customer_enterprise_name);
                holder.itemTvLine2.setText(R.string.register_address);
                holder.itemEtLine2.setText(customer_address);
                break;
        }

        if (contractListInfo.isConfirmed()) {
            holder.itemTvStatus.setText(R.string.signed);
            holder.itemTvStatus.setTextColor(mContext.getResources().getColor(R.color.c_29c093));
            holder.itemTvStatus.setBackgroundResource(R.drawable.shape_bg_stroke_1_29c_full_corner);
        } else {
            holder.itemTvStatus.setText(R.string.not_signed);
            holder.itemTvStatus.setTextColor(mContext.getResources().getColor(R.color.c_ff8d34));
            holder.itemTvStatus.setBackgroundResource(R.drawable.shape_bg_stroke_1_ff8d_full_corner);
        }
        String contract_number = contractListInfo.getContract_number();
        holder.itemEtNumber.setText(contract_number);
        //TODO 改为时间戳
        String createdAt = contractListInfo.getCreatedAt();
        if (!TextUtils.isEmpty(createdAt)) {
            try {
                String[] ts = createdAt.split("T");
                createdAt = ts[0];
                createdAt = createdAt.replaceAll("-", ".");
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.itemEtDate.setText(createdAt);
        }
        //
        String confirmTime = contractListInfo.getConfirmTime();
        if (!TextUtils.isEmpty(confirmTime)) {
            try {
                String[] ts = confirmTime.split("T");
                confirmTime = ts[0].replaceAll("-", ".");
            } catch (Exception e) {
                e.printStackTrace();
                confirmTime = "-";
            }

            holder.itemEtSignTime.setText(confirmTime);
        } else {
            holder.itemEtSignTime.setText("-");
        }
        return convertView;
    }


    static class ContractViewHolder {

        TextView itemTvLine1;
        TextView itemEtLine1;
        TextView itemTvType;
        TextView itemTvLine2;
        TextView itemEtLine2;
        TextView itemEtNumber;
        TextView itemEtDate;
        TextView itemTvStatus;
        TextView itemEtSignTime;
        TextView itemTvContactsEnterprise;
        TextView itemEtContactsEnterprise;
        RelativeLayout itemRlContactsEnterprise;

        ContractViewHolder() {

        }
    }
}