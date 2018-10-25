package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.widget.CustomVRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainHomeFragRcContentAdapterHorizontal extends RecyclerView.Adapter<MainHomeFragRcContentAdapterHorizontal.MyViewHolder> implements Constants {
    private final Activity mContext;
    private final List<HomeTopModel> mList = new ArrayList<>();
    private OnLoadInnerListener listener;

    public MainHomeFragRcContentAdapterHorizontal(Activity context) {
        mContext = context;
    }


    public List<HomeTopModel> getData() {
        return mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_horizontal, parent, false);
        return new MyViewHolder(inflate);
    }

    public void updateData(final List<HomeTopModel> list) {
//        deviceMergeTypeConfig = PreferencesHelper.getInstance().getLocalDevicesMergeTypes().getConfig();
////        //
////        ThreadPoolManager.getInstance().execute(new Runnable() {
////            @Override
////            public void run() {
////                HomeContentListAdapterDiff homeContentListAdapterDiff = new HomeContentListAdapterDiff(mList, list);
////                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(homeContentListAdapterDiff, true);
////                mContext.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        diffResult.dispatchUpdatesTo(MainHomeFragRcContentAdapter.this);
////                        mList.clear();
////                        mList.addAll(list);
////                    }
////                });
////
////
////            }
////        });
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final HomeTopModel item = mList.get(position);
        if (item.innerAdapter == null) {
            item.innerAdapter = new MainHomeFragRcContentAdapter(mContext);
        }
        if (item.mDeviceList.size() > 0) {
            setNoContentVisible(holder, false);
        } else {
            setNoContentVisible(holder, true);
            return;
        }
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        holder.rvHorizontalItem.setLayoutManager(layoutManager);
        holder.rvHorizontalItem.setAdapter(item.innerAdapter);
        if (item.scrollOffset > 0) {
            layoutManager.scrollToPositionWithOffset(item.scrollPosition, item.scrollOffset);
        }
        holder.refreshLayout.setEnableAutoLoadMore(false);//开启自动加载功能（非必须）
        item.smartRefreshLayout = holder.refreshLayout;
        holder.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                if (listener != null) {
                    listener.onRefresh(refreshLayout, position);
                }
            }
        });
        holder.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                if (listener != null) {
                    listener.onLoadMore(refreshLayout, position);
                }
            }
        });
        holder.rvHorizontalItem.addOnScrollListener(new MyOnScrollListener(item, layoutManager));

        item.innerAdapter.setOnContentItemClickListener(new MainHomeFragRcContentAdapter.OnContentItemClickListener() {
            @Override
            public void onAlarmInfoClick(View v, int position) {
                if (listener != null) {
                    listener.onAlarmInfoClick(v, position);
                }
            }

            @Override
            public void onItemClick(View view, int position) {
                if (listener != null) {
                    listener.onItemClick(view, position);
                }
            }
        });
//        holder.rvHorizontalItem.setNestParent(holder.refreshLayout);
        if (holder.rvHorizontalItem.isComputingLayout()) {
            holder.rvHorizontalItem.post(new Runnable() {
                @Override
                public void run() {
                    item.innerAdapter.updateData(item.mDeviceList);
                }
            });
            return;
        }
        item.innerAdapter.updateData(item.mDeviceList);
//        if (position == 0) {
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.homeContentRoot.getLayoutParams());
//            int pxL = AppUtils.dp2px(mContext, 14);
//            lp.setMargins(pxL, 0, 0, 0);
//            holder.homeContentRoot.setLayoutParams(lp);
//        } else {
//            if (position == mList.size() - 1) {
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.homeContentRoot.getLayoutParams());
//                int pxR = AppUtils.dp2px(mContext, 14);
//                lp.setMargins(0, 0, pxR, 0);
//                holder.homeContentRoot.setLayoutParams(lp);
//            }
//        }
    }

//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull List<Object> payloads) {
//        if (payloads.isEmpty()) {
//            onBindViewHolder(holder, position);
//        } else {
//
//        }
//    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rv_horizontal_item)
        CustomVRecyclerView rvHorizontalItem;
        @BindView(R.id.refreshLayout)
        SmartRefreshLayout refreshLayout;
        @BindView(R.id.ic_no_content)
        LinearLayout noContent;
        @BindView(R.id.home_content_root)
        RelativeLayout homeContentRoot;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnLoadInnerListener {
        void onRefresh(@NonNull final RefreshLayout refreshLayout, int position);

        void onLoadMore(@NonNull final RefreshLayout refreshLayout, int position);

        void onAlarmInfoClick(View v, int position);

        void onItemClick(View view, int position);
    }

    public void setOnLoadInnerListener(OnLoadInnerListener listener) {
        this.listener = listener;
    }

    private class MyOnScrollListener extends RecyclerView.OnScrollListener {

        private LinearLayoutManager mLayoutManager;
        private HomeTopModel mEntity;
        private int mItemWidth;
        private int mItemMargin;

        public MyOnScrollListener(HomeTopModel shopItem, LinearLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
            mEntity = shopItem;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:

                    int offset = recyclerView.computeHorizontalScrollOffset();
                    mEntity.scrollPosition = mLayoutManager.findFirstVisibleItemPosition() < 0 ? mEntity.scrollPosition : mLayoutManager.findFirstVisibleItemPosition() + 1;
                    if (mItemWidth <= 0) {
                        View item = mLayoutManager.findViewByPosition(mEntity.scrollPosition);
                        if (item != null) {
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();
                            mItemWidth = item.getWidth();
                            mItemMargin = layoutParams.rightMargin;
                        }
                    }
                    if (offset > 0 && mItemWidth > 0) {
                        //offset % mItemWidth：得到当前position的滑动距离
                        //mEntity.scrollPosition * mItemMargin：得到（0至position）的所有item的margin
                        //用当前item的宽度-所有margin-当前position的滑动距离，就得到offset。
                        mEntity.scrollOffset = mItemWidth - offset % mItemWidth + mEntity.scrollPosition * mItemMargin;
                    }
                    break;
            }
        }
    }

    private void setNoContentVisible(MyViewHolder holder, boolean isVisible) {
        holder.noContent.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        holder.rvHorizontalItem.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }
}
