package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.DeviceTypeStyles;
import com.sensoro.smartcity.server.bean.MalfunctionDataBean;
import com.sensoro.smartcity.server.bean.MalfunctionListInfo;
import com.sensoro.smartcity.server.bean.MalfunctionTypeStyles;
import com.sensoro.smartcity.server.bean.MergeTypeStyles;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMalfunctionFragRcContentAdapter extends RecyclerView.Adapter<MainMalfunctionFragRcContentAdapter.MainMalfunctionFragViewHolder> {
    private final Context mContext;

    private List<MalfunctionListInfo> mList = new ArrayList<>();
    private DeviceMergeTypesInfo.DeviceMergeTypeConfig deviceMergeTypeConfig;
    private RecycleViewItemClickListener mListener;

    public MainMalfunctionFragRcContentAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public MainMalfunctionFragViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_adapter_main_malfunction_rc_content, parent, false);
        return new MainMalfunctionFragViewHolder(inflate);
    }

    public void setData(List<MalfunctionListInfo> list) {
        deviceMergeTypeConfig = PreferencesHelper.getInstance().getLocalDevicesMergeTypes().getConfig();
        this.mList.clear();
        this.mList.addAll(list);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public MalfunctionListInfo getItem(int position) {
        if (mList.size() > position) {
            return mList.get(position);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainMalfunctionFragViewHolder holder, int position) {
        MalfunctionListInfo malfunctionListInfo = mList.get(position);
        switch (malfunctionListInfo.getMalfunctionStatus()) {
            case 1:
                holder.mainMalfunctionRcContentTvState.setText(mContext.getString(R.string.fg_malfunction_back_to_normal));
                setTextColor(holder, R.color.c_29c093);
                break;
            case 2:
                holder.mainMalfunctionRcContentTvState.setText(mContext.getString(R.string.fg_malfunction_malfunctioning));
                setTextColor(holder, R.color.c_fdc83b);
                break;
        }
        holder.mainMalfunctionRcContentTvTime.setText(DateUtil.getStrTimeToday(mContext, malfunctionListInfo.getCreatedTime(), 0));

        String deviceType = malfunctionListInfo.getDeviceType();
        String deviceTypeStr;
        try {
            Map<String, DeviceTypeStyles> deviceTypeMap = deviceMergeTypeConfig.getDeviceType();
            DeviceTypeStyles deviceTypeStyles = deviceTypeMap.get(deviceType);
            String mergeType = deviceTypeStyles.getMergeType();
            Map<String, MergeTypeStyles> mergeTypeMap = deviceMergeTypeConfig.getMergeType();
            MergeTypeStyles mergeTypeStyles = mergeTypeMap.get(mergeType);
            deviceTypeStr = mergeTypeStyles.getName();
        } catch (Exception e) {
            e.printStackTrace();
            List<String> strings = new ArrayList<String>();
            strings.add(deviceType);
            deviceTypeStr = WidgetUtil.parseSensorTypes(mContext, strings);
        }
        String deviceName = malfunctionListInfo.getDeviceName();
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = malfunctionListInfo.getDeviceSN();
        }

        holder.mainMalfunctionRcContentTvContent.setText(String.format("%s %s", deviceTypeStr, deviceName));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(v, holder.getLayoutPosition());
                }
            }
        });
        String malfunctionType = malfunctionListInfo.getMalfunctionType();
        DeviceMergeTypesInfo localDevicesMergeTypes = PreferencesHelper.getInstance().getLocalDevicesMergeTypes();
        if (localDevicesMergeTypes != null) {
            DeviceMergeTypesInfo.DeviceMergeTypeConfig config = localDevicesMergeTypes.getConfig();
            if (config != null) {
                DeviceMergeTypesInfo.DeviceMergeTypeConfig.MalfunctionTypeBean malfunctionTypeBean = config.getMalfunctionType();
                if (malfunctionTypeBean != null) {
                    Map<String, MalfunctionTypeStyles> mainTypes = malfunctionTypeBean.getMainTypes();
                    if (mainTypes != null) {
                        MalfunctionTypeStyles malfunctionTypeStyles = mainTypes.get(malfunctionType);
                        if (malfunctionTypeStyles != null) {
                            String name = malfunctionTypeStyles.getName();
                            if (!TextUtils.isEmpty(name)) {
                                holder.mainMalfunctionRcContentTvReason.setText(name);
                                LogUtils.loge("localDevicesMergeTypes = " + name);
                                return;
                            }
                        }
                    }
                }
            }
        }
        Map<String, MalfunctionDataBean> malfunctionData = malfunctionListInfo.getMalfunctionData();
        if (malfunctionData.keySet().contains(malfunctionType)) {
            holder.mainMalfunctionRcContentTvReason.setText(malfunctionData.get(malfunctionType).getDescription());
        } else {
            holder.mainMalfunctionRcContentTvReason.setText(mContext.getString(R.string.unknown_malfunction));
        }
    }

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mListener = listener;
    }

    private void setTextColor(@NonNull MainMalfunctionFragViewHolder holder, int color) {
        holder.mainMalfunctionRcContentTvState.setTextColor(mContext.getResources().getColor(color));
        holder.mainMalfunctionRcContentTvReason.setTextColor(mContext.getResources().getColor(color));
        holder.mainMalfunctionRcContentView.setBackgroundColor(mContext.getResources().getColor(color));
    }

    class MainMalfunctionFragViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.main_malfunction_rc_content_tv_state)
        TextView mainMalfunctionRcContentTvState;
        @BindView(R.id.main_malfunction_rc_content_view)
        View mainMalfunctionRcContentView;
        @BindView(R.id.main_malfunction_rc_content_tv_reason)
        TextView mainMalfunctionRcContentTvReason;
        @BindView(R.id.main_malfunction_rc_content_tv_time)
        TextView mainMalfunctionRcContentTvTime;
        @BindView(R.id.main_malfunction_rc_content_tv_content)
        TextView mainMalfunctionRcContentTvContent;

        MainMalfunctionFragViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
