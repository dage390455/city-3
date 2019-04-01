package com.sensoro.smartcity.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.SecurityRisksAdapterModel;
import com.sensoro.smartcity.adapter.touchHelper.SecurityRiskContentTouchHelper;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecurityRisksContentAdapter extends RecyclerView.Adapter<SecurityRisksContentAdapter.SecurityRisksContentHolder>
implements SecurityRiskContentTouchHelper.ItemTouchHelperAdapter {

    //普通类型的item
    private static final int VIEW_TYPE_CONTENT = 1;
    //添加新条目的item
    private static final int VIEW_TYPE_ADD_ITEM = 2;
    private final Context mContext;
    private ArrayList<SecurityRisksAdapterModel> list = new ArrayList<>();
    private SecurityRisksContentClickListener mListener;

    public SecurityRisksContentAdapter(Context context) {
        mContext = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public SecurityRisksContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SecurityRisksContentHolder securityRisksContentHolder;
        if (VIEW_TYPE_ADD_ITEM == viewType) {
            View viewAddItem = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_add_item, parent, false);
            securityRisksContentHolder = new SecurityRisksContentHolder(viewAddItem);
            securityRisksContentHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAddItemClick();
                    }
                }
            });
        } else {
            View viewContent = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_security_risk_content, parent, false);
            securityRisksContentHolder = new SecurityRisksContentHolder(viewContent);
            securityRisksContentHolder.llLocationNameAdapterSecurityRisks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer position = (Integer) v.getTag();
                    for (int i = 0; i < list.size(); i++) {
                        if (position == i) {
                            SecurityRisksAdapterModel model = list.get(position);
                            model.locationColor = R.color.c_252525;
                            model.behaviorColor = R.color.c_a6a6a6;
//                            notifyItemChanged(position);
                        }else{
                            SecurityRisksAdapterModel model = list.get(i);
                            if (model.locationColor == R.color.c_252525 || model.behaviorColor == R.color.c_252525) {
                                model.locationColor = R.color.c_a6a6a6;
                                model.behaviorColor = R.color.c_a6a6a6;
//                                notifyItemChanged(i);

                            }
                        }
                    }
                    notifyDataSetChanged();

                    if (mListener != null) {
                        Log.e("cxy","ll:postion::"+position);
                        mListener.onLocationClick((Integer) position);
                    }
                }
            });


            securityRisksContentHolder.llBehaviorAdapterSecurityRisks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Integer position = (Integer) v.getTag();
                        for (int i = 0; i < list.size(); i++) {
                            if (position == i) {
                                SecurityRisksAdapterModel model = list.get(position);
                                model.locationColor = R.color.c_a6a6a6;
                                model.behaviorColor = R.color.c_252525;
                            }else{
                                SecurityRisksAdapterModel model = list.get(i);
                                if (model.locationColor == R.color.c_252525 || model.behaviorColor == R.color.c_252525) {
                                    model.locationColor = R.color.c_a6a6a6;
                                    model.behaviorColor = R.color.c_a6a6a6;
                                }
                            }
                        }
                        notifyDataSetChanged();
                        if (mListener != null) {
                            mListener.onBehaviorClick((Integer) v.getTag());
                        }
                    }

            });
            securityRisksContentHolder.rvBehaviorsAdapterSecurityRisks.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Integer position = (Integer) v.getTag();
                        for (int i = 0; i < list.size(); i++) {
                            if (position == i) {
                                SecurityRisksAdapterModel model = list.get(position);
                                model.locationColor = R.color.c_a6a6a6;
                                model.behaviorColor = R.color.c_252525;
                            }else{
                                SecurityRisksAdapterModel model = list.get(i);
                                if (model.locationColor == R.color.c_252525 || model.behaviorColor == R.color.c_252525) {
                                    model.locationColor = R.color.c_a6a6a6;
                                    model.behaviorColor = R.color.c_a6a6a6;
                                }
                            }
                        }
                        notifyDataSetChanged();
                        if (mListener != null) {
                            mListener.onBehaviorClick((Integer) v.getTag());
                        }
                    }
                    return true;
                }
            });

            securityRisksContentHolder.ivDelAdapterSecurityRisksTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        Integer position = (Integer) v.getTag();
                        mListener.onLocationDel(list.get(position).place,position);
                    }
                }
            });

        }

        return securityRisksContentHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SecurityRisksContentHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_ADD_ITEM) {
            holder.tvAddAdapterSecurityRisks.setTag(position);
        } else {
            holder.tvLocationNameAdapterSecurityRisks.setTag(position);
            holder.rvBehaviorsAdapterSecurityRisks.setTag(position);
            holder.llLocationNameAdapterSecurityRisks.setTag(position);
            holder.llBehaviorAdapterSecurityRisks.setTag(position);
            holder.ivDelAdapterSecurityRisksTag.setTag(position);

            SecurityRisksAdapterModel model = list.get(position);
            holder.tvLocationAdapterSecurityRisks.setTextColor(mContext.getResources().getColor(model.locationColor));
            holder.tvBehaviorAdapterSecurityRisks.setTextColor(mContext.getResources().getColor(model.behaviorColor));
            if (!TextUtils.isEmpty(model.place)) {
                holder.llLocationNameContentAdapterSecurityRisks.setVisibility(View.VISIBLE);
                holder.tvLocationNameAdapterSecurityRisks.setText(model.place);
            }else{
                holder.llLocationNameContentAdapterSecurityRisks.setVisibility(View.GONE);
            }

            if (model.action.size() > 0) {
                SecurityRisksTagAdapter securityRisksTagAdapter = new SecurityRisksTagAdapter(mContext);
                securityRisksTagAdapter.setOnSecurityRisksTagClickListener(new SecurityRisksTagAdapter.SecurityRisksTagClickListener() {
                    @Override
                    public void onDelItemClick(String tag) {
                        if (mListener != null) {
                            mListener.onBehaviorDel(tag,holder.getAdapterPosition());
                        }
                    }
                });
                SensoroLinearLayoutManager manager = new SensoroLinearLayoutManager(mContext);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder.rvBehaviorsAdapterSecurityRisks.setLayoutManager(manager);
                holder.rvBehaviorsAdapterSecurityRisks.setAdapter(securityRisksTagAdapter);
                securityRisksTagAdapter.updateData(model.action);
            }


        }
    }

    @Override
    public void onBindViewHolder(@NonNull SecurityRisksContentHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder,position,payloads);
        }else{
            SecurityRisksAdapterModel model = list.get(position);
            holder.tvLocationAdapterSecurityRisks.setTextColor(mContext.getResources().getColor(model.locationColor));
            holder.tvBehaviorAdapterSecurityRisks.setTextColor(mContext.getResources().getColor(model.behaviorColor));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() > 19) {
            return VIEW_TYPE_CONTENT;
        } else {
            if (position == getItemCount() - 1) {
                return VIEW_TYPE_ADD_ITEM;
            } else {
                return VIEW_TYPE_CONTENT;
            }
        }
    }

    public void updateData(List<SecurityRisksAdapterModel> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnSecurityRisksItemClickListener(SecurityRisksContentClickListener listener) {
        mListener = listener;
    }


    @Override
    public int getItemCount() {
        int size = list.size();
        return size < 20 ? size + 1 : size;
    }

    public void changLocationOrBehaviorColor(int position, boolean isLocation) {

        for (int i = 0; i < list.size(); i++) {
            if (position == i) {
                if (isLocation) {
                    SecurityRisksAdapterModel model = list.get(position);
                    model.locationColor = R.color.c_252525;
                    model.behaviorColor = R.color.c_a6a6a6;
                }else{
                    SecurityRisksAdapterModel model = list.get(position);
                    model.locationColor = R.color.c_a6a6a6;
                    model.behaviorColor = R.color.c_252525;
                }

//                            notifyItemChanged(position);
            }else{
                SecurityRisksAdapterModel model = list.get(i);
                if (model.locationColor == R.color.c_252525 || model.behaviorColor == R.color.c_252525) {
                    model.locationColor = R.color.c_a6a6a6;
                    model.behaviorColor = R.color.c_a6a6a6;
//                                notifyItemChanged(i);

                }
            }
        }
        notifyDataSetChanged();

    }

    public void clearFocus() {
        for (int i = 0; i < list.size(); i++) {
            SecurityRisksAdapterModel model = list.get(i);
            if (model.locationColor == R.color.c_252525 || model.behaviorColor == R.color.c_252525) {
                model.locationColor = R.color.c_a6a6a6;
                model.behaviorColor = R.color.c_a6a6a6;
                notifyItemChanged(i);
            }
        }
    }

    public void updateLocationTag(String tag, boolean check) {

    }

    /**
     * 删除
     * @param position
     */
    @Override
    public void onItemDismiss(int position) {
        if (mListener != null) {
            mListener.onItemDel(position);
        }
    }

    /*
    * 移动
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    class SecurityRisksContentHolder extends RecyclerView.ViewHolder {
        @Nullable
        @BindView(R.id.tv_location_adapter_security_risks)
        TextView tvLocationAdapterSecurityRisks;
        @Nullable
        @BindView(R.id.tv_location_name_adapter_security_risks)
        TextView tvLocationNameAdapterSecurityRisks;
        @Nullable
        @BindView(R.id.view_divider_adapter_security_risks)
        View viewDividerAdapterSecurityRisks;
        @Nullable
        @BindView(R.id.tv_behavior_adapter_security_risks)
        TextView tvBehaviorAdapterSecurityRisks;
        @Nullable
        @BindView(R.id.rv_behaviors_adapter_security_risks)
        RecyclerView rvBehaviorsAdapterSecurityRisks;
        @Nullable
        @BindView(R.id.tv_add_adapter_security_risks)
        TextView tvAddAdapterSecurityRisks;
        @Nullable
        @BindView(R.id.iv_del_adapter_security_risks_tag)
        ImageView ivDelAdapterSecurityRisksTag;
        @Nullable
        @BindView(R.id.ll_location_name_adapter_security_risks)
        LinearLayout llLocationNameAdapterSecurityRisks;
        @Nullable
        @BindView(R.id.ll_location_name_content_adapter_security_risks)
        LinearLayout llLocationNameContentAdapterSecurityRisks;
        @Nullable
        @BindView(R.id.ll_behavior_adapter_security_risks)
        LinearLayout llBehaviorAdapterSecurityRisks;

        SecurityRisksContentHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.tv_location_adapter_security_risks);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface SecurityRisksContentClickListener {
        void onLocationClick(int position);

        void onBehaviorClick(int position);

        void onAddItemClick();

        void onLocationDel(String tag, Integer position);

        void onBehaviorDel(String tag, int position);

        void onItemDel(int position);
    }
}
