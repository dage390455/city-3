package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.popup.SelectDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class SecurityRisksReferTagAdapter extends RecyclerView.Adapter<SecurityRisksReferTagAdapter.SecurityRisksReferTagViewHolder> {
    private final Context mContext;
    private ArrayList<String> list = new ArrayList<>();
    private final Drawable drawable;

    public SecurityRisksReferTagAdapter(Context context) {
        mContext = context;

        drawable = mContext.getResources().getDrawable(R.drawable.security_refer_tag_add);
        drawable.setBounds(0,0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    }

    @NonNull
    @Override
    public SecurityRisksReferTagAdapter.SecurityRisksReferTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_refer_tag, parent, false);
        SecurityRisksReferTagViewHolder holder = new SecurityRisksReferTagViewHolder(view);
        holder.tvTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityRisksReferTagAdapter.SecurityRisksReferTagViewHolder holder, int position) {
        if(position == getItemCount()-1){
            holder.tvTag.setCompoundDrawables(drawable,null,null,null);
            holder.tvTag.setCompoundDrawablePadding(AppUtils.dp2px(mContext,4));
            holder.tvTag.setTextColor(mContext.getResources().getColor(R.color.c_252525));
            holder.tvTag.setBackground(mContext.getDrawable(R.drawable.shape_bg_solid_ff_stroke_df_full_corner));
            holder.tvTag.setText(mContext.getText(R.string.refer_loaction));
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    public void updateData(List<String> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    class SecurityRisksReferTagViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvTag;

        public SecurityRisksReferTagViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tvTag = itemView.findViewById(R.id.tv_tag_security_risk_refer_tag);
        }
    }

}
