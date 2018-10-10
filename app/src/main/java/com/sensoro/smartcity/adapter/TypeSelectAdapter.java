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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.model.DeviceTypeModel;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeSelectAdapter extends RecyclerView.Adapter<TypeSelectAdapter.TypeSelectHolder> {
    private final Context mContext;
    private String[] types = Constants.SELECT_TYPE;
    private Integer[] typeIcons = Constants.SELECT_TYPE_RESOURCE;
    private int selectPosition = 0;
    private int oldSelectPosition = 0;
    private RecycleViewItemClickListener mListener;
    private List<String> mDeviceTypeList = new ArrayList<>();
    private DeviceMergeTypesInfo localDevicesMergeTypes;

    public TypeSelectAdapter(Context context) {
        mContext = context;
        localDevicesMergeTypes = PreferencesHelper.getInstance().getLocalDevicesMergeTypes();
    }


    @Override
    public TypeSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_adapter_type_select, parent, false);
        TypeSelectHolder holder = new TypeSelectHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TypeSelectHolder holder, final int position) {
        if (position == 0) {
            holder.itemPopSelectTvTypeName.setText("全部");
            changeIconColor(holder, position != selectPosition, mContext.getResources().getDrawable(R.drawable.type_all_test));
        } else {
            final int index = position - 1;
            String deviceType = mDeviceTypeList.get(index);
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            Map<String, DeviceTypeStyles> deviceTypeMap = config.getDeviceType();
            DeviceTypeStyles deviceTypeStyles = deviceTypeMap.get(deviceType);
            Map<String, MergeTypeStyles> mergeType = config.getMergeType();
            MergeTypeStyles mergeTypeStyles = mergeType.get(deviceTypeStyles.getMergeType());
            String name = mergeTypeStyles.getName();
            String image = mergeTypeStyles.getImage();
//            int resId = mergeTypeStyles.getResId();
            //
            holder.itemPopSelectTvTypeName.setText(name);
            Glide.with(mContext)                             //配置上下文
                    .load(image)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            changeIconColor(holder, position != selectPosition, resource);
                            return true;
                        }
                    }).centerCrop().into(holder.itemPopSelectImvTypeIcon);
        }

//        DeviceTypeModel deviceTypeModel = mDeviceTypeList.get(position);
//        holder.itemPopSelectImvTypeIcon.setImageResource(deviceTypeModel.iconRes);
//        holder.itemPopSelectTvTypeName.setText(deviceTypeModel.name);
        //
//        holder.itemPopSelectTvTypeName.setTextColor(position != selectPosition ? Color.WHITE :
//                mContext.getResources().getColor(R.color.c_252525));
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

    public DeviceTypeModel getItem(int position) {
        DeviceTypeModel deviceTypeModel = new DeviceTypeModel();
        if (position == 0) {
            deviceTypeModel.name = "全部";
            deviceTypeModel.iconRes = R.mipmap.type_all;
        } else {
            final int index = position - 1;
            String deviceType = mDeviceTypeList.get(index);
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            Map<String, DeviceTypeStyles> deviceTypeMap = config.getDeviceType();
            DeviceTypeStyles deviceTypeStyles = deviceTypeMap.get(deviceType);
            Map<String, MergeTypeStyles> mergeType = config.getMergeType();
            MergeTypeStyles mergeTypeStyles = mergeType.get(deviceTypeStyles.getMergeType());
            String name = mergeTypeStyles.getName();
            String image = mergeTypeStyles.getImage();
            deviceTypeModel.name = name;
            ArrayList<String> strs = new ArrayList<>();
            strs.add(deviceType);
            deviceTypeModel.deviceTypes = strs;
        }
//        DeviceTypeModel deviceTypeModel = new DeviceTypeModel("", 0, "", "");
//        return mDeviceTypeList.get(position);
        return deviceTypeModel;
    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    public void updateDeviceTypList(List<String> list) {
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

    private void changeIconColor(TypeSelectHolder holder, boolean isWhite, Drawable drawable) {
        holder.itemPopSelectLlRoot.setBackgroundResource(isWhite ? 0 : R.drawable.shape_bg_corner_29c_shadow);
        holder.itemPopSelectTvTypeName.setTextColor(isWhite ? mContext.getResources().getColor(R.color.c_252525) : Color.WHITE);
        int color = isWhite ? mContext.getResources().getColor(R.color.c_b6b6b6) : Color.WHITE;
        holder.itemPopSelectImvTypeIcon.setImageDrawable(drawable);
        holder.itemPopSelectImvTypeIcon.setColorFilter(color);
    }

    @Override
    public int getItemCount() {
        return mDeviceTypeList.size() + 1;
    }

    public List<String> getDataList() {
        return mDeviceTypeList;
    }


    class TypeSelectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_pop_select_imv_type_icon)
        ImageView itemPopSelectImvTypeIcon;
        @BindView(R.id.item_pop_select_tv_type_name)
        TextView itemPopSelectTvTypeName;
        @BindView(R.id.item_pop_select_ll_root)
        LinearLayout itemPopSelectLlRoot;

        TypeSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
