package com.sensoro.smartcity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class AlarmContactRcContentAdapter extends RecyclerView.Adapter<AlarmContactRcContentAdapter.AlarmContactRcContentHolder>{
    @Override
    public AlarmContactRcContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(AlarmContactRcContentHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class AlarmContactRcContentHolder extends RecyclerView.ViewHolder{

        public AlarmContactRcContentHolder(View itemView) {
            super(itemView);
        }
    }
}
