package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.SecurityRisksTagModel;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class SecurityRisksReferTagAdapter extends RecyclerView.Adapter<SecurityRisksReferTagAdapter.SecurityRisksReferTagViewHolder> {
    private final Context mContext;
    private ArrayList<SecurityRisksTagModel> list = new ArrayList<>();
    private final Drawable drawable;
    private boolean mIsLocation;
    private OnTagClickListener mListener;
    private int mCheckCount;

    public SecurityRisksReferTagAdapter(Context context) {
        mContext = context;

        drawable = mContext.getResources().getDrawable(R.drawable.security_refer_tag_add);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    }

    @NonNull
    @Override
    public SecurityRisksReferTagAdapter.SecurityRisksReferTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_refer_tag, parent, false);
        SecurityRisksReferTagViewHolder holder = new SecurityRisksReferTagViewHolder(view);
        holder.tvTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                if (position == getItemCount() - 1 && mListener != null) {
                    mListener.onAddTag(mIsLocation);
                } else {
                    SecurityRisksTagModel model = list.get(position);
                    model.isCheck = !model.isCheck;
                    if (model.isCheck) {
                        mCheckCount++;
                        if(mIsLocation){
                            for (int i = 0; i < list.size(); i++) {
                                if (i != position) {
                                    list.get(i).isCheck = false;
                                }
                            }
                        }

                    }else{
                        mCheckCount--;
                    }

                    if (mCheckCount > 10) {
                        SensoroToast.getInstance().makeText(mContext.getString(R.string.most_support_ten_tag), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    notifyDataSetChanged();
                    if (mListener != null) {
                        mListener.onTagClick(model, mIsLocation, position);
                    }
                }


            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityRisksReferTagAdapter.SecurityRisksReferTagViewHolder holder, int position) {
        holder.tvTag.setTag(position);
        int maxCount = mIsLocation ? 19 : 29;
        if (list.size() > maxCount) {
            SecurityRisksTagModel model = list.get(position);
            holder.tvTag.setCompoundDrawables(null, null, null, null);
            holder.tvTag.setText(model.tag);
            holder.tvTag.setTextColor(model.isCheck ? mContext.getResources().getColor(R.color.white) : mContext.getResources().getColor(R.color.c_252525));
            holder.tvTag.setBackground(model.isCheck ? mContext.getDrawable(R.drawable.shape_bg_solid_29c_full_corner) : mContext.getDrawable(R.drawable.shape_bg_solid_f4_20dp_corner));
        } else {
            if (position == getItemCount() - 1) {
                holder.tvTag.setCompoundDrawables(drawable, null, null, null);
                holder.tvTag.setCompoundDrawablePadding(AppUtils.dp2px(mContext, 4));
                holder.tvTag.setTextColor(mContext.getResources().getColor(R.color.c_252525));
                holder.tvTag.setBackground(mContext.getDrawable(R.drawable.shape_bg_solid_ff_stroke_df_full_corner));
                holder.tvTag.setText(mIsLocation ? mContext.getText(R.string.security_location) : mContext.getText(R.string.security_behavior));
            } else {
                SecurityRisksTagModel model = list.get(position);
                holder.tvTag.setCompoundDrawables(null, null, null, null);
                holder.tvTag.setText(model.tag);
                holder.tvTag.setTextColor(model.isCheck ? mContext.getResources().getColor(R.color.white) : mContext.getResources().getColor(R.color.c_252525));
                holder.tvTag.setBackground(model.isCheck ? mContext.getDrawable(R.drawable.shape_bg_solid_29c_full_corner) : mContext.getDrawable(R.drawable.shape_bg_solid_f4_20dp_corner));
            }
        }

    }


    @Override
    public int getItemCount() {
        if (mIsLocation) {
            return list.size() < 20 ? list.size() + 1 : list.size();
        } else {
            return list.size() < 30 ? list.size() + 1 : list.size();
        }
    }


    public void updateData(ArrayList<SecurityRisksTagModel> data, boolean isLocation) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
        mIsLocation = isLocation;
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        mListener = listener;
    }

    public boolean getIsLocation() {
        return mIsLocation;
    }

    public interface OnTagClickListener {
        void onAddTag(boolean mIsLocation);

        void onTagClick(SecurityRisksTagModel model, boolean mIsLocation, Integer position);
    }

    class SecurityRisksReferTagViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTag;

        public SecurityRisksReferTagViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvTag = itemView.findViewById(R.id.tv_tag_security_risk_refer_tag);
        }
    }

}
