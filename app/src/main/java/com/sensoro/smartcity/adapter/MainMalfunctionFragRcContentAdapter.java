package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.server.bean.MalfunctionListInfo;
import com.sensoro.common.server.bean.MalfunctionTypeStyles;
import com.sensoro.smartcity.R;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.common.callback.RecycleViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMalfunctionFragRcContentAdapter extends RecyclerView.Adapter<MainMalfunctionFragRcContentAdapter.MainMalfunctionFragViewHolder> {
    private final Context mContext;

    private List<MalfunctionListInfo> mList = new ArrayList<>();
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
                setTextColor(holder, R.color.c_1dbb99);
                break;
            case 2:
                holder.mainMalfunctionRcContentTvState.setText(mContext.getString(R.string.fg_malfunction_malfunctioning));
                setTextColor(holder, R.color.c_fdc83b);
                break;
        }
        holder.mainMalfunctionRcContentTvTime.setText(DateUtil.getStrTimeToday(mContext, malfunctionListInfo.getCreatedTime(), 0));

        String deviceType = malfunctionListInfo.getDeviceType();
        String deviceTypeStr = WidgetUtil.getDeviceMainTypeName(deviceType);
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
        MalfunctionTypeStyles malfunctionTypeStyles = PreferencesHelper.getInstance().getConfigMalfunctionMainTypes(malfunctionType);
        if (malfunctionTypeStyles != null) {
            String name = malfunctionTypeStyles.getName();
            if (!TextUtils.isEmpty(name)) {
                holder.mainMalfunctionRcContentTvReason.setText(name);
                try {
                    LogUtils.loge("localDevicesMergeTypes = " + name);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return;
            }
        }
        holder.mainMalfunctionRcContentTvReason.setText(mContext.getString(R.string.unknown));
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
