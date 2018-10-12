package com.sensoro.smartcity.adapter.DiffUtils;

import android.support.v7.widget.RecyclerView;

public class DiffUtilsHeadNotifyObserver extends RecyclerView.AdapterDataObserver {

    RecyclerView.AdapterDataObserver mDataObserver;

    int mHeaderSize;

    public DiffUtilsHeadNotifyObserver(RecyclerView.AdapterDataObserver dataObserver, int headerSize) {
        mDataObserver = dataObserver;
        mHeaderSize = headerSize;
    }

    @Override
    public void onChanged() {
        mDataObserver.onChanged();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        mDataObserver.onItemRangeChanged(positionStart + mHeaderSize, itemCount);
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        mDataObserver.onItemRangeChanged(positionStart + mHeaderSize, itemCount, payload);
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        mDataObserver.onItemRangeInserted(positionStart + mHeaderSize, itemCount);
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        mDataObserver.onItemRangeMoved(fromPosition + mHeaderSize, toPosition, itemCount);
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        mDataObserver.onItemRangeRemoved(positionStart + mHeaderSize, itemCount);
    }
}
