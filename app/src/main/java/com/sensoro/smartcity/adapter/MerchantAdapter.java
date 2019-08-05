package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.smartcity.R;

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
            holder.itemBottomS = convertView.findViewById(R.id.item_bottom_s);
            convertView.setTag(holder);
        } else {
            holder = (MerchantViewHolder) convertView.getTag();
        }
        //

        UserInfo userInfo = mList.get(position);
        if (userInfo.isStop()) {
            holder.item_name.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
        } else {
            holder.item_name.setTextColor(mContext.getResources().getColor(R.color.c_252525));
        }
        String nickname = userInfo.getNickname();
        if (TextUtils.isEmpty(nickname)) {
            nickname = mContext.getString(R.string.unknown);
        }
        holder.item_name.setText(nickname);

        if (mList.size() == 0 || position == mList.size() - 1) {
            holder.itemBottomS.setVisibility(View.GONE);
        } else {
            holder.itemBottomS.setVisibility(View.VISIBLE);
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
        View itemBottomS;

        MerchantViewHolder() {

        }
    }
}