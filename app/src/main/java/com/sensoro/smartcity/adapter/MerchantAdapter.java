package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.factory.MerchantSubFactory;
import com.sensoro.smartcity.model.MerchantSubModel;

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
            holder.itemIvMerchantArrow = convertView.findViewById(R.id.iv_merchant_arrow);
            holder.itemRvMerchantSub = convertView.findViewById(R.id.rv_merchant_sub);
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
        holder.item_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickMerchant(userInfo);
                }
            }
        });
        //
        List<MerchantSubModel> merchantSubList = MerchantSubFactory.createMerchantSubList(userInfo);
        if (merchantSubList != null && merchantSubList.size() > 0) {
            if (holder.itemRvMerchantSub.getTag() instanceof MerchantSubAdapter) {
                holder.itemRvMerchantSub.removeAllViews();
            }
            holder.itemRvMerchantSub.setLayoutManager(new LinearLayoutManager(mContext));
            holder.itemRvMerchantSub.setHasFixedSize(true);

            MerchantSubAdapter adapter = new MerchantSubAdapter(mContext);
            holder.itemRvMerchantSub.setAdapter(adapter);
            adapter.setOnMerchantClickListener(new MerchantSubAdapter.OnMerchantClickListener() {
                @Override
                public void onClickMerchant(UserInfo userInfo) {
                    if (listener != null) {
                        listener.onClickMerchant(userInfo);
                    }
                }
            });
            //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
            holder.itemRvMerchantSub.setNestedScrollingEnabled(false);
            holder.itemRvMerchantSub.setTag(adapter);
            adapter.updateData(merchantSubList);
            holder.itemIvMerchantArrow.setVisibility(View.VISIBLE);
            //TODO 处理下拉和上拉逻辑
            holder.itemIvMerchantArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.itemRvMerchantSub.getVisibility() == View.VISIBLE) {
                        holder.itemRvMerchantSub.setVisibility(View.GONE);
                        holder.itemBottomS.setVisibility(View.VISIBLE);
                        holder.itemIvMerchantArrow.setImageResource(R.drawable.arrow_down_elect);
                    } else {
                        holder.itemRvMerchantSub.setVisibility(View.VISIBLE);
                        holder.itemBottomS.setVisibility(View.GONE);
                        holder.itemIvMerchantArrow.setImageResource(R.drawable.arrow_up_elect);
                    }
                }
            });
            if (holder.itemRvMerchantSub.getVisibility() == View.VISIBLE) {
                holder.itemBottomS.setVisibility(View.GONE);
            } else {
                holder.itemBottomS.setVisibility(View.VISIBLE);
            }


        } else {
            if (mList.size() == 0 || position == mList.size() - 1) {
                holder.itemBottomS.setVisibility(View.GONE);
            } else {
                holder.itemBottomS.setVisibility(View.VISIBLE);
            }
            holder.itemRvMerchantSub.setVisibility(View.GONE);
            holder.itemIvMerchantArrow.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    private MerchantSubAdapter.OnMerchantClickListener listener;

    public void setOnMerchantClickListener(MerchantSubAdapter.OnMerchantClickListener listener) {
        this.listener = listener;
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
        ImageView itemIvMerchantArrow;
        RecyclerView itemRvMerchantSub;
        View itemBottomS;

        MerchantViewHolder() {

        }
    }
}