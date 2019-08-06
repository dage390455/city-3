package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.utils.DpUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.MerchantSubModel;

import java.util.ArrayList;
import java.util.List;

public class MerchantSubAdapter extends RecyclerView.Adapter<MerchantSubAdapter.MerchantSubAdapterViewHolder> {
    private Context mContext;
    private final List<MerchantSubModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void updateData(List<MerchantSubModel> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<MerchantSubModel> getImages() {
        return mData;
    }

    public MerchantSubAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MerchantSubAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MerchantSubAdapterViewHolder(mInflater.inflate(R.layout.item_merchant_sub, parent, false));
    }

    @Override
    public void onBindViewHolder(MerchantSubAdapterViewHolder holder, int position) {
        MerchantSubModel merchantSubModel = mData.get(position);
        if (merchantSubModel != null) {
            int level = merchantSubModel.level;
            String name = merchantSubModel.name;
            UserInfo userInfo = merchantSubModel.userInfo;
            if (!TextUtils.isEmpty(name)) {
                //
                holder.llCircleRoot.removeAllViews();
                for (int i = 0; i < level; i++) {
                    ImageView imageView = new ImageView(mContext);
                    imageView.setPadding(DpUtils.dp2px(mContext, 2), DpUtils.dp2px(mContext, 2), DpUtils.dp2px(mContext, 2), DpUtils.dp2px(mContext, 2));
                    imageView.setImageResource(R.drawable.shape_oval_b6b6_6dp);
                    holder.llCircleRoot.addView(imageView);
                }
                if (userInfo.isStop()) {
                    holder.itemMerchantSubName.setTextColor(mContext.getResources().getColor(R.color.c_a6a6a6));
                } else {
                    holder.itemMerchantSubName.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                }
                holder.itemMerchantSubName.setText(name);
            }
            holder.itemMerchantSubName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClickMerchant(userInfo);
                    }
                }
            });

        }
    }

    public interface OnMerchantClickListener {
        void onClickMerchant(UserInfo userInfo);
    }

    private OnMerchantClickListener listener;

    public void setOnMerchantClickListener(OnMerchantClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MerchantSubAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView itemMerchantSubName;
        LinearLayout llCircleRoot;

        MerchantSubAdapterViewHolder(View itemView) {
            super(itemView);
            itemMerchantSubName = itemView.findViewById(R.id.item_merchant_sub_name);
            llCircleRoot = itemView.findViewById(R.id.ll_circle_root);
        }

    }
}