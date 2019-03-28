package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecurityRisksTagAdapter extends RecyclerView.Adapter<SecurityRisksTagAdapter.SecurityRisksTagViewHolder> {

    private final Context mContext;
    private int mStatus = -1;
    private ArrayList<String> list = new ArrayList<>();
    private SecurityRisksTagClickListener listener;

    public SecurityRisksTagAdapter(Context context) {
        mContext = context;
    }

    public SecurityRisksTagAdapter(Context context,int status) {
       this(context);
        mStatus = status;
    }

    @NonNull
    @Override
    public SecurityRisksTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (mStatus == -1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_tag, parent, false);
        }else{
            view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_tag_two, parent, false);
        }


        SecurityRisksTagViewHolder holder = new SecurityRisksTagViewHolder(view);
        holder.ivDelAdapterSecurityRisksTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener !=  null) {
                    listener.onDelItemClick(list.get((Integer) v.getTag()));
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityRisksTagViewHolder holder, int position) {
        holder.ivDelAdapterSecurityRisksTag.setTag(position);
        holder.tvContentAdapterSecurityRisksTag.setText(list.get(position));

    }

    public void setOnSecurityRisksTagClickListener(SecurityRisksTagClickListener listener){
        this.listener = listener;
    }


    public void updateData(ArrayList<String> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class SecurityRisksTagViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_content_adapter_security_risks_tag)
        TextView tvContentAdapterSecurityRisksTag;
        @BindView(R.id.iv_del_adapter_security_risks_tag)
        ImageView ivDelAdapterSecurityRisksTag;
        @BindView(R.id.ll_root_adapter_security_risks_tag)
        LinearLayout llRootAdapterSecurityRisksTag;

        public SecurityRisksTagViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface SecurityRisksTagClickListener{

        void onDelItemClick(String s);

    }

}
