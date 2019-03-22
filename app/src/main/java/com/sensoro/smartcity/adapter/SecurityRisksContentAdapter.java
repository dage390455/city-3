package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SecurityRisksContentAdapter extends RecyclerView.Adapter<SecurityRisksContentAdapter.SecurityRisksContentHolder> {

    private final Context mContext;

    public SecurityRisksContentAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public SecurityRisksContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityRisksContentHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SecurityRisksContentHolder extends RecyclerView.ViewHolder{

        public SecurityRisksContentHolder(View itemView) {
            super(itemView);
        }
    }
}
