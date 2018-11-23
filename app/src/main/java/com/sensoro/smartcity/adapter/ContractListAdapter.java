package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private final Resources resources;
    //    private OnContractClickListener mListener;

    public ContractListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        resources = context.getResources();
//        this.mListener = listener;
    }

//    public interface OnContractClickListener {
//        void onContractClickItem(View view, int position);
//    }

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
        ContractViewHolder holder = null;
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
            convertView.setTag(holder);
        } else {
            holder = (ContractViewHolder) convertView.getTag();
        }
        ContractListInfo contractListInfo = mList.get(position);
        int contract_type = contractListInfo.getContract_type();
        switch (contract_type) {
            case 1:
                holder.itemTvType.setText(R.string.company);
                break;
            case 2:
                holder.itemTvType.setText(R.string.personal);
                break;
            default:
                holder.itemTvType.setText(R.string.company);
                break;
        }

        if (contractListInfo.isConfirmed()) {
            holder.itemTvStatus.setText(R.string.signed);
            holder.itemTvStatus.setTextColor(resources.getColor(R.color.c_29c093));
            holder.itemTvStatus.setBackgroundResource(R.drawable.shape_bg_stroke_1_29c_full_corner);
        } else {
            holder.itemTvStatus.setText(R.string.not_signed);
            holder.itemTvStatus.setTextColor(resources.getColor(R.color.c_ff8d34));
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
        //
        int created_type = contractListInfo.getCreated_type();
        String customer_name = contractListInfo.getCustomer_name();
        switch (created_type) {
            case 1:
                holder.itemTvLine1.setText(mContext.getString(R.string.legal_representative));
                holder.itemEtLine1.setText(customer_name);
                String customer_enterprise_name = contractListInfo.getCustomer_enterprise_name();
                holder.itemTvLine2.setText(R.string.company_name);
                holder.itemEtLine2.setText(customer_enterprise_name);
                break;
            case 2:
            case 3:
                holder.itemTvLine1.setText(R.string.name);
                holder.itemEtLine1.setText(customer_name);
                String customer_address = contractListInfo.getCustomer_address();
                holder.itemTvLine2.setText(R.string.address);
                holder.itemEtLine2.setText(customer_address);
                break;
//            case 3:
//                String customer_enterprise_name1 = contractListInfo.getCustomer_enterprise_name();
//                holder.itemTvLine1.setText("姓名");
//                holder.itemEtLine1.setText(customer_name);
//                holder.itemTvLine2.setText("住址");
//                holder.itemEtLine2.setText(customer_enterprise_name1);
//                break;
            default:
        }
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
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

        ContractViewHolder() {

        }
    }
}