package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;


public class MerchantAdapter extends BaseAdapter implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;
    private final List<UserInfo> mList = new ArrayList<>();

    public MerchantAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MerchantViewHolder holder;
        if (convertView == null) {
            holder = new MerchantViewHolder();
            convertView = mInflater.inflate(R.layout.item_merchant, null);
            holder.item_name = (TextView) convertView.findViewById(R.id.item_merchant_name);
            holder.item_phone = (TextView) convertView.findViewById(R.id.item_merchant_phone);
            holder.item_icon = (ImageView) convertView.findViewById(R.id.item_merchant_icon);
            holder.itemBottomS = convertView.findViewById(R.id.item_bottom_s);
            convertView.setTag(holder);
        } else {
            holder = (MerchantViewHolder) convertView.getTag();
        }
        UserInfo userInfo = mList.get(position);
        if (userInfo.isStop()) {
            holder.item_name.setTextColor(mContext.getResources().getColor(R.color.item_sensor_line));
        } else {
            holder.item_name.setTextColor(mContext.getResources().getColor(R.color.slide_menu_item_bg));
        }
        holder.item_name.setText(userInfo.getNickname());
        holder.item_phone.setText(userInfo.getContacts());

        if (mList.size() != 0 && position == mList.size() - 1) {
            holder.itemBottomS.setVisibility(View.VISIBLE);
        } else {
            holder.itemBottomS.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setDataList(List<UserInfo> data) {
        mList.clear();
        mList.addAll(data);
    }

    public List<UserInfo> getData() {
        return mList;
    }

    static class MerchantViewHolder {

        TextView item_name;
        TextView item_phone;
        ImageView item_icon;
        View itemBottomS;

        MerchantViewHolder() {

        }
    }
}