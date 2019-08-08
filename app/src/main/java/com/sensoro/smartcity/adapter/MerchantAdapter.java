package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.factory.MerchantSubFactory;
import com.sensoro.smartcity.model.MerchantSubModel;

import java.util.ArrayList;
import java.util.List;


public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder> implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;
    private final List<UserInfo> mList = new ArrayList<>();

    public MerchantAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MerchantViewHolder(mInflater.inflate(R.layout.item_merchant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder holder, int position) {
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
        //只有一项
        final View.OnClickListener onArrowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 处理下拉逻辑
                if (holder.itemRvMerchantSub.getVisibility() == View.VISIBLE) {
                    holder.itemRvMerchantSub.setVisibility(View.GONE);
                    holder.itemBottomS.setVisibility(View.VISIBLE);
                    holder.itemIvMerchantArrow.setImageResource(R.drawable.merchant_arrow_close);
                } else {
                    holder.itemRvMerchantSub.setVisibility(View.VISIBLE);
                    holder.itemBottomS.setVisibility(View.GONE);
                    holder.itemIvMerchantArrow.setImageResource(R.drawable.merchant_arrow_open);
                }
            }
        };
        final MerchantSubAdapter.OnMerchantClickListener onUserInfoClickListener = new MerchantSubAdapter.OnMerchantClickListener() {
            @Override
            public void onClickMerchant(UserInfo userInfo) {
                if (MerchantAdapter.this.listener != null) {
                    MerchantAdapter.this.listener.onClickMerchant(userInfo);
                }
            }
        };
        if (mList.size() > 0 && mList.size() <= 1) {
            //有子账户
            if (merchantSubList != null && merchantSubList.size() > 0) {
                if (holder.itemRvMerchantSub.getTag() instanceof MerchantSubAdapter) {
                    holder.itemRvMerchantSub.removeAllViews();
                }
                holder.itemRvMerchantSub.setLayoutManager(new LinearLayoutManager(mContext));
                holder.itemRvMerchantSub.setHasFixedSize(true);

                MerchantSubAdapter adapter = new MerchantSubAdapter(mContext);
                holder.itemRvMerchantSub.setAdapter(adapter);
                adapter.setOnMerchantClickListener(onUserInfoClickListener);
                //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
                holder.itemRvMerchantSub.setNestedScrollingEnabled(false);
                holder.itemRvMerchantSub.setTag(adapter);
                adapter.updateData(merchantSubList);
                holder.flArrow.setVisibility(View.VISIBLE);
                //TODO 处理下拉和上拉逻辑
                holder.flArrow.setOnClickListener(onArrowClickListener);
                holder.itemRvMerchantSub.setVisibility(View.VISIBLE);
                holder.itemBottomS.setVisibility(View.GONE);
                holder.itemIvMerchantArrow.setImageResource(R.drawable.merchant_arrow_open);

            } else {
                //没有子账户
                mList.size();
                if (position == mList.size() - 1) {
                    holder.itemBottomS.setVisibility(View.GONE);
                } else {
                    holder.itemBottomS.setVisibility(View.VISIBLE);
                }
                holder.itemRvMerchantSub.setVisibility(View.GONE);
                holder.flArrow.setVisibility(View.INVISIBLE);
            }
        } else {
            //存在多个账户
            if (merchantSubList != null && merchantSubList.size() > 0) {
                if (holder.itemRvMerchantSub.getTag() instanceof MerchantSubAdapter) {
                    holder.itemRvMerchantSub.removeAllViews();
                }
                holder.itemRvMerchantSub.setLayoutManager(new LinearLayoutManager(mContext));
                holder.itemRvMerchantSub.setHasFixedSize(true);

                MerchantSubAdapter adapter = new MerchantSubAdapter(mContext);
                holder.itemRvMerchantSub.setAdapter(adapter);
                adapter.setOnMerchantClickListener(onUserInfoClickListener);
                //设置包裹不允许滑动，套一层父布局解决最后一项可能不显示的问题
                holder.itemRvMerchantSub.setNestedScrollingEnabled(false);
                holder.itemRvMerchantSub.setTag(adapter);
                adapter.updateData(merchantSubList);
                holder.flArrow.setVisibility(View.VISIBLE);
                //TODO 处理下拉和上拉逻辑
                holder.flArrow.setOnClickListener(onArrowClickListener);
                if (holder.itemRvMerchantSub.getVisibility() == View.VISIBLE) {
                    holder.itemBottomS.setVisibility(View.GONE);
                } else {
                    holder.itemBottomS.setVisibility(View.VISIBLE);
                }

            } else {
                if (position == mList.size() - 1) {
                    holder.itemBottomS.setVisibility(View.GONE);
                } else {
                    holder.itemBottomS.setVisibility(View.VISIBLE);
                }
                holder.itemRvMerchantSub.setVisibility(View.GONE);
                holder.flArrow.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
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

    static class MerchantViewHolder extends RecyclerView.ViewHolder {

        FrameLayout flArrow;
        TextView item_name;
        ImageView itemIvMerchantArrow;
        RecyclerView itemRvMerchantSub;
        View itemBottomS;

        MerchantViewHolder(View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_merchant_name);
            itemIvMerchantArrow = itemView.findViewById(R.id.iv_merchant_arrow);
            itemRvMerchantSub = itemView.findViewById(R.id.rv_merchant_sub);
            itemBottomS = itemView.findViewById(R.id.item_bottom_s);
            flArrow = itemView.findViewById(R.id.fl_arrow);
        }
    }
}