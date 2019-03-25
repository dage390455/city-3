package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensoro.smartcity.R;

class SecurityRisksTagAdapter extends RecyclerView.Adapter<SecurityRisksTagAdapter.SecurityRisksTagViewHolder>{


    private final Context mContext;

    public SecurityRisksTagAdapter(Context context) {
        mContext = context;

    }

    @NonNull
    @Override
    public SecurityRisksTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_tag,parent,false);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityRisksTagViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SecurityRisksTagViewHolder extends RecyclerView.ViewHolder{

        public SecurityRisksTagViewHolder(View itemView) {
            super(itemView);
        }
    }
}
