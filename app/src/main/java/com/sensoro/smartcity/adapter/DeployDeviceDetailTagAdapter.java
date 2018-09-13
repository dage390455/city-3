package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensoro.smartcity.R;

public class DeployDeviceDetailTagAdapter extends RecyclerView.
        Adapter<DeployDeviceDetailTagAdapter.DeployDeviceDetailTagHolder>{
    private final Context mContext;

    public DeployDeviceDetailTagAdapter(Context context) {
        mContext = context;
    }

    @Override
    public DeployDeviceDetailTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_text_view, parent, false);

        return new DeployDeviceDetailTagHolder(view);
    }

    @Override
    public void onBindViewHolder(DeployDeviceDetailTagHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class DeployDeviceDetailTagHolder extends RecyclerView.ViewHolder{

        public DeployDeviceDetailTagHolder(View itemView) {
            super(itemView);
        }
    }
}
