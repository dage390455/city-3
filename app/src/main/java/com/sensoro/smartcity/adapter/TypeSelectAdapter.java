package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeSelectAdapter extends RecyclerView.Adapter<TypeSelectAdapter.TypeSelectHolder> {
    private final Context mContext;
    private String[] types = Constants.SELECT_TYPE;
    private Integer[] typeIcons = Constants.SELECT_TYPE_RESOURCE;
    private int selectPosition = 0;
    private int oldSelectPosition = 0;
    private RecycleViewItemClickListener mListener;
    private List<DeviceTypeModel> mDeviceTypeList = new ArrayList<>();

    public TypeSelectAdapter(Context context) {
        mContext = context;
    }


    @Override
    public TypeSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_adapter_type_select, parent, false);
        TypeSelectHolder holder = new TypeSelectHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(TypeSelectHolder holder, int position) {
        DeviceTypeModel deviceTypeModel = mDeviceTypeList.get(position);
        holder.itemPopSelectImvTypeIcon.setImageResource(deviceTypeModel.iconRes);
        holder.itemPopSelectTvTypeName.setText(deviceTypeModel.name);
        holder.itemPopSelectTvTypeName.setTextColor(position != selectPosition ? Color.WHITE :
                mContext.getResources().getColor(R.color.c_252525));
        changeIconColor(holder, position != selectPosition);
        holder.itemPopSelectLlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldSelectPosition = selectPosition;
                selectPosition = ((TypeSelectHolder) v.getTag()).getAdapterPosition();
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onItemClick(v, selectPosition);
                }

            }
        });


    }

    public DeviceTypeModel getItem(int position){
        return mDeviceTypeList.get(position);
    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    public void updateDeviceTypList(List<DeviceTypeModel> list){
        mDeviceTypeList.clear();
        mDeviceTypeList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(TypeSelectHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (position == selectPosition || position == oldSelectPosition) {
                onBindViewHolder(holder, position);
            }
        }
    }


    @NonNull
    private void changeIconColor(TypeSelectHolder holder, boolean isWhite) {
        holder.itemPopSelectLlRoot.setBackgroundResource(isWhite ? 0 : R.drawable.shape_bg_corner_29c_shadow);
        holder.itemPopSelectTvTypeName.setTextColor(isWhite ? mContext.getResources().getColor(R.color.c_252525) : Color.WHITE);
        Drawable drawable = holder.itemPopSelectImvTypeIcon.getDrawable();
//        Drawable.ConstantState statusTitle = drawable.getConstantState();
//        DrawableCompat.wrap(statusTitle == null ? drawable : statusTitle.newDrawable()).mutate();
//        drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
//        DrawableCompat.setTint(drawable, isWhite ? mContext.getResources().getColor(R.color.c_b6b6b6) : Color.WHITE);
        int color = isWhite ? mContext.getResources().getColor(R.color.c_b6b6b6) : Color.WHITE;
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        holder.itemPopSelectImvTypeIcon.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return mDeviceTypeList.size();
    }

    public List<DeviceTypeModel> getDataList() {
        return mDeviceTypeList;
    }


    class TypeSelectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_pop_select_imv_type_icon)
        ImageView itemPopSelectImvTypeIcon;
        @BindView(R.id.item_pop_select_tv_type_name)
        TextView itemPopSelectTvTypeName;
        @BindView(R.id.item_pop_select_ll_root)
        LinearLayout itemPopSelectLlRoot;

        public TypeSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
