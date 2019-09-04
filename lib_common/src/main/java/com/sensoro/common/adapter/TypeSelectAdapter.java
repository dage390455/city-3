package com.sensoro.common.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sensoro.common.R;
import com.sensoro.common.R2;
import com.sensoro.common.callback.RecycleViewItemClickListener;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.DeviceTypeModel;
import com.sensoro.common.server.bean.DeviceTypeStyles;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.utils.WidgetUtil;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeSelectAdapter extends RecyclerView.Adapter<TypeSelectAdapter.TypeSelectHolder> {
    private final Context mContext;

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    private int selectPosition = 0;
    private int oldSelectPosition = 0;
    private RecycleViewItemClickListener mListener;
    private List<String> mDeviceTypeList = new ArrayList<>();

    private int typeStyle = 1;

    public TypeSelectAdapter(Context context) {
        mContext = context;
    }

    public void setTypeStyle(int style) {
        typeStyle = style;
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
            holder.itemPopSelectTvTypeName.setText(R.string.all_types);
            changeIconColor(holder, position != selectPosition, mContext.getResources().getDrawable(R.drawable.type_all_test));
        } else {
            final int index = position - 1;
            String name = null;
            String image = null;
            switch (typeStyle) {
                case 1:
                    //巡检部分为deviceType
                    String deviceType = mDeviceTypeList.get(index);
                    DeviceTypeStyles deviceTypeStyles = PreferencesHelper.getInstance().getConfigDeviceType(deviceType);
                    if (deviceTypeStyles != null) {
                        String category = deviceTypeStyles.getCategory();
                        String mergeType = deviceTypeStyles.getMergeType();
                        MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
                        if (mergeTypeStyles != null) {
                            name = mergeTypeStyles.getName();
                            if (!TextUtils.isEmpty(category)) {
                                name = name + category;
                            }
                            image = mergeTypeStyles.getImage();
                        }

                    }
                    break;
                case 2:
                    //设备首页字段为mergeType
                    String mergeType = mDeviceTypeList.get(index);
                    MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
                    if (mergeTypeStyles != null) {
                        name = mergeTypeStyles.getName();
                        image = mergeTypeStyles.getImage();
                    }
                    break;
            }

            if (TextUtils.isEmpty(name)) {
                name = mContext.getString(R.string.unknown);
            }
            holder.itemPopSelectTvTypeName.setText(name);
            Glide.with(mContext)                             //配置上下文
                    .load(image)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop())//设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            changeIconColor(holder, position != selectPosition, resource);
                            return true;
                        }
                    }).into(holder.itemPopSelectImvTypeIcon);

        }
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
            deviceTypeModel.name = mContext.getString(R.string.all_types);
            deviceTypeModel.iconRes = R.mipmap.type_all;
        } else {
            final int index = position - 1;
            switch (typeStyle) {
                case 1:
                    String deviceType = mDeviceTypeList.get(index);
                    ArrayList<String> strs = new ArrayList<>();
                    deviceTypeModel.name = WidgetUtil.getInspectionDeviceName(deviceType);
                    strs.add(deviceType);
                    deviceTypeModel.deviceTypes = strs;
                    break;
                case 2:
                    String mergeType = mDeviceTypeList.get(index);
                    MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
                    if (mergeTypeStyles != null) {
                        deviceTypeModel.name = mergeTypeStyles.getName();
                        deviceTypeModel.deviceTypes = mergeTypeStyles.getDeviceTypes();
                        break;
                    }
                    deviceTypeModel.name = mContext.getString(R.string.unknown);
                    break;
            }

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
//        selectPosition=0;
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
        holder.itemPopSelectLlRoot.setBackgroundResource(isWhite ? 0 : R.drawable.shape_bg_inspectiontask_corner_29c_shadow);
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
        holder.itemPopSelectLlRoot.setBackgroundResource(isWhite ? 0 : R.drawable.shape_bg_corner_4_29c_shadow);
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
        @BindView(R2.id.item_pop_select_imv_type_icon)
        ImageView itemPopSelectImvTypeIcon;
        @BindView(R2.id.item_pop_select_tv_type_name)
        TextView itemPopSelectTvTypeName;
        @BindView(R2.id.item_pop_select_ll_root)
        LinearLayout itemPopSelectLlRoot;

        TypeSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
