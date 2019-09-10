package com.sensoro.smartcity.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MutilTypeAdapter extends RecyclerView.Adapter {

    //定义三种常量  表示三种条目类型
    public static final int TYPE_PULL_IMAGE = 0;
    public static final int TYPE_RIGHT_IMAGE = 1;
    public static final int TYPE_THREE_IMAGE = 2;
    private List<MoreTypeBean> mData;

    public MutilTypeAdapter(List<MoreTypeBean> data) {
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建不同的 ViewHolder
        View view = null;
        //根据viewtype来创建条目

        if (viewType == TYPE_PULL_IMAGE) {
//            view =View.inflate(parent.getContext(), R.layout.item_pull_img,null);
            return new PullImageHolder(view);
        } else if (viewType == TYPE_RIGHT_IMAGE) {
//            view =View.inflate(parent.getContext(),R.layout.item_right_img,null);
            return new RightImageHolder(view);
        } else {
//            view =View.inflate(parent.getContext(),R.layout.item_three_img,null);
            return new ThreeImageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        int itemViewType = holder.getItemViewType();


        switch (itemViewType) {

            case TYPE_PULL_IMAGE:
                PullImageHolder pullImageHolder = (PullImageHolder) holder;


                break;

            case TYPE_RIGHT_IMAGE:
                RightImageHolder rightImageHolder = (RightImageHolder) holder;


                break;
            case TYPE_THREE_IMAGE:
                ThreeImageHolder threeImageHolder = (ThreeImageHolder) holder;


                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {

        MoreTypeBean moreTypeBean = mData.get(position);
        if (moreTypeBean.type == 0) {
            return TYPE_PULL_IMAGE;
        } else if (moreTypeBean.type == 1) {
            return TYPE_RIGHT_IMAGE;
        } else {
            return TYPE_THREE_IMAGE;
        }


    }

    /**
     * 创建三种ViewHolder
     */
    private class PullImageHolder extends RecyclerView.ViewHolder {

        public PullImageHolder(View itemView) {
            super(itemView);
        }
    }

    private class RightImageHolder extends RecyclerView.ViewHolder {

        public RightImageHolder(View itemView) {
            super(itemView);
        }
    }

    private class ThreeImageHolder extends RecyclerView.ViewHolder {

        public ThreeImageHolder(View itemView) {
            super(itemView);
        }
    }

    public class MoreTypeBean {
        public int type;
        public int pic;
    }
}
