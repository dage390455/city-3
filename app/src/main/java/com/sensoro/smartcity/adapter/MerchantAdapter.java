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

import java.util.ArrayList;
import java.util.List;


public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder> implements Constants {

    private Context mContext;
    private LayoutInflater mInflater;
    private final List<UserInfo> mList = new ArrayList<>();
    //展示动画
//    private TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

    //隐藏动画
//    private TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//            -1.0f);

    public MerchantAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
//        mShowAction.setDuration(350);
//        mHiddenAction.setDuration(300);
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
        final MerchantSubAdapter.OnMerchantClickListener onUserInfoClickListener = new MerchantSubAdapter.OnMerchantClickListener() {
            @Override
            public void onClickMerchant(UserInfo userInfo) {
                if (MerchantAdapter.this.listener != null) {
                    MerchantAdapter.this.listener.onClickMerchant(userInfo);
                }
            }
        };
        if (userInfo.expand != null) {
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
            adapter.updateData(userInfo.merchantSubList);
            holder.flArrow.setVisibility(View.VISIBLE);
            //只有一项
            final View.OnClickListener onArrowClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TODO 处理下拉逻辑保证只有一个
                    boolean currentExpand = userInfo.expand;
                    if (currentExpand) {
                        //过去是展开状态
                        userInfo.expand = false;
                    } else {
                        //过去是关闭状态
                        userInfo.expand = true;
                        //其他都设置为关闭
                        for (int i = 0; i < mList.size(); i++) {
                            //处理当前的逻辑跟其他逻辑
                            UserInfo userInfo = mList.get(i);
                            if (userInfo.expand != null) {
                                if (i != position) {
                                    userInfo.expand = false;
                                }
                            }

                        }
                    }
                    MerchantAdapter.this.notifyDataSetChanged();
                    //
//                if (userInfo.expand) {
//                    holder.itemRvMerchantSub.setVisibility(View.VISIBLE);
//                    holder.itemBottomS.setVisibility(View.GONE);
//                    holder.itemIvMerchantArrow.setImageResource(R.drawable.merchant_arrow_open);
//                } else {
//                    holder.itemRvMerchantSub.setVisibility(View.GONE);
//                    holder.itemBottomS.setVisibility(View.VISIBLE);
//                    holder.itemIvMerchantArrow.setImageResource(R.drawable.merchant_arrow_close);
//
//                }a
                }
            };
            holder.flArrow.setOnClickListener(onArrowClickListener);
            //存在展开项
            if (userInfo.expand) {
                //动画展示
//                holder.itemRvMerchantSub.startAnimation(mShowAction);
                holder.itemRvMerchantSub.setVisibility(View.VISIBLE);
//                holder.itemRvMerchantSub.setVisibility(View.VISIBLE);
                holder.itemBottomS.setVisibility(View.GONE);
                holder.itemIvMerchantArrow.setImageResource(R.drawable.merchant_arrow_open);
            } else {
                //动画消失
//                holder.itemRvMerchantSub.startAnimation(mHiddenAction);
                holder.itemRvMerchantSub.setVisibility(View.GONE);
//                holder.itemRvMerchantSub.setVisibility(View.GONE);
                if (position == mList.size() - 1) {
                    holder.itemBottomS.setVisibility(View.GONE);
                } else {
                    holder.itemBottomS.setVisibility(View.VISIBLE);
                }
                holder.itemIvMerchantArrow.setImageResource(R.drawable.merchant_arrow_close);
            }
        } else {
            //不需要展开
            if (position == mList.size() - 1) {
                holder.itemBottomS.setVisibility(View.GONE);
            } else {
                holder.itemBottomS.setVisibility(View.VISIBLE);
            }
            holder.itemRvMerchantSub.setVisibility(View.GONE);
            holder.flArrow.setVisibility(View.INVISIBLE);
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

    public void updateDataList(List<UserInfo> data, boolean isSearch) {
        mList.clear();
        mList.addAll(data);
        //只在一条时显示展开
        if (isSearch) {
            if (mList.size() == 1) {
                UserInfo userInfo = mList.get(0);
                if (userInfo.expand != null) {
                    userInfo.expand = true;
                }
            }
        }
        notifyDataSetChanged();
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