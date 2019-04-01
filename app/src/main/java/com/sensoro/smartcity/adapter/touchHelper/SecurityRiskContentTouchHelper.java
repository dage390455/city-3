package com.sensoro.smartcity.adapter.touchHelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.sensoro.smartcity.adapter.SecurityRisksContentAdapter;

/**
 * 安全隐患特定的touchHelper
 */
public class SecurityRiskContentTouchHelper extends ItemTouchHelper.Callback {

    private final SecurityRisksContentAdapter adapter;

    public SecurityRiskContentTouchHelper(SecurityRisksContentAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int dragFlag = 0;
        int swipeFlag = 0;
        if(viewHolder.getAdapterPosition() == adapter.getItemCount() -1){
            //最后一条没有拖放，滑动事件
            return makeMovementFlags(dragFlag, swipeFlag);
        }
        if (layoutManager instanceof GridLayoutManager) {
            dragFlag = ItemTouchHelper.DOWN | ItemTouchHelper.UP
                    | ItemTouchHelper.START | ItemTouchHelper.END; //网格布局的，则上下左右均为拖放
        } else if (layoutManager instanceof LinearLayoutManager) {
            dragFlag = ItemTouchHelper.DOWN | ItemTouchHelper.UP; //设置上下方向为拖放
            swipeFlag = ItemTouchHelper.START ; //设置左方向为滑动删除,右方向不能删除
        }
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    public interface ItemTouchHelperAdapter {
        void onItemDismiss(int position);
        void onItemMove(int fromPosition, int toPosition);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return super.isItemViewSwipeEnabled();
    }
}
