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

import java.util.List;

/**
 * Created by fangping on 2016/7/7.
 */

public class MerchantAdapter extends BaseAdapter implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<UserInfo> mList;
    private int selectedIndex = -1;

    public MerchantAdapter(Context context, List<UserInfo> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mList = list;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
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
        MerchantViewHolder holder = null;
        if (convertView == null) {
            holder = new MerchantViewHolder();
            convertView = mInflater.inflate(R.layout.item_merchant, null);
            holder.item_name = (TextView) convertView.findViewById(R.id.item_merchant_name);
            holder.item_phone = (TextView) convertView.findViewById(R.id.item_merchant_phone);
            holder.item_status = (ImageView) convertView.findViewById(R.id.item_merchant_status);
            holder.item_icon = (ImageView) convertView.findViewById(R.id.item_merchant_icon);
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

        if (selectedIndex == position) {
            holder.item_status.setVisibility(View.VISIBLE);
        } else {
            holder.item_status.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setDataList(List<UserInfo> list) {
    }

    static class MerchantViewHolder {

        TextView item_name;
        TextView item_phone;
        ImageView item_status;
        ImageView item_icon;

        MerchantViewHolder() {

        }
    }
}