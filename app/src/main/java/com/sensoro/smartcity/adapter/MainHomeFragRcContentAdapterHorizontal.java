package com.sensoro.smartcity.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.HomeTopModel;
import com.sensoro.smartcity.widget.CustomVRecyclerView;
import com.sensoro.smartcity.widget.CustomVRelativeLayout;

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
//        //
//        ThreadPoolManager.getInstance().execute(new Runnable() {
//            @Override
//            public void run() {
//                TopListAdapterDiff homeContentListAdapterDiff = new TopListAdapterDiff(mList, list);
//                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(homeContentListAdapterDiff, false);
//                mContext.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        diffResult.dispatchUpdatesTo(MainHomeFragRcContentAdapterHorizontal.this);
//                        mList.clear();
//                        mList.addAll(list);
//                    }
//                });
//
//
//            }
//        });
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
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//        layoutManager.setOrientation(OrientationHelper.VERTICAL);
//        holder.rvHorizontalItem.setLayoutManager(layoutManager);
        holder.rvHorizontalItem.setAdapter(item.innerAdapter);
//        holder.rvHorizontalItem.setNestedScrollingEnabled(true);
//        if (item.scrollOffset > 0) {
//            layoutManager.scrollToPositionWithOffset(item.scrollPosition, item.scrollOffset);
//        }
//        MyOnScrollListener listener = new MyOnScrollListener(item, layoutManager);
//        holder.rvHorizontalItem.addOnScrollListener(listener);

        item.innerAdapter.setOnContentItemClickListener(new MainHomeFragRcContentAdapter.OnContentItemClickListener() {
            @Override
            public void onAlarmInfoClick(View v, int position) {
                if (MainHomeFragRcContentAdapterHorizontal.this.listener != null) {
                    MainHomeFragRcContentAdapterHorizontal.this.listener.onAlarmInfoClick(v, position);
                }
            }

            @Override
            public void onItemClick(View view, int position) {
                if (MainHomeFragRcContentAdapterHorizontal.this.listener != null) {
                    MainHomeFragRcContentAdapterHorizontal.this.listener.onItemClick(view, position);
                }
            }
        });
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
//        runEnterAnimation(holder.itemView, position);
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
        @BindView(R.id.ic_no_content)
        LinearLayout noContent;
        @BindView(R.id.home_content_root)
        CustomVRelativeLayout homeContentRoot;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(OrientationHelper.VERTICAL);
            rvHorizontalItem.setLayoutManager(layoutManager);
            rvHorizontalItem.setNestedScrollingEnabled(true);
//        if (item.scrollOffset > 0) {
//            layoutManager.scrollToPositionWithOffset(item.scrollPosition, item.scrollOffset);
//        }
//        MyOnScrollListener listener = new MyOnScrollListener(item, layoutManager);
//        holder.rvHorizontalItem.addOnScrollListener(listener);
        }
    }

    public interface OnLoadInnerListener {

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

    //
//    private int lastAnimatedPosition = -1;
//    private boolean animationsLocked = false;
//    private boolean delayEnterAnimation = true;
//    private void runEnterAnimation(View view, int position) {
//
//
//        if (animationsLocked) return;              //animationsLocked是布尔类型变量，一开始为false
//        //确保仅屏幕一开始能够容纳显示的item项才开启动画
//
//
//        if (position > lastAnimatedPosition) {//lastAnimatedPosition是int类型变量，默认-1，
//            //这两行代码确保了recyclerview滚动式回收利用视图时不会出现不连续效果
//            lastAnimatedPosition = position;
////            view.setTranslationY(500);     //Item项一开始相对于原始位置下方500距离
//            view.setAlpha(0.f);           //item项一开始完全透明
//            //每个item项两个动画，从透明到不透明，从下方移动到原始位置
//
//
//            view.animate()
////                    .translationY(0)
//                    .alpha(1.f)                                //设置最终效果为完全不透明
//                    //并且在原来的位置
//                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)//根据item的位置设置延迟时间
//                    //达到依次动画一个接一个进行的效果
//                    .setInterpolator(new DecelerateInterpolator(0.5f))     //设置动画位移先快后慢的效果
//                    .setDuration(1000)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            animationsLocked = true;
//                            //确保仅屏幕一开始能够显示的item项才开启动画
//                            //也就是说屏幕下方还没有显示的item项滑动时是没有动画效果
//                        }
//                    })
//                    .start();
//        }
//    }
}
