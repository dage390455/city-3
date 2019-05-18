package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceCameraInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 自检状态
 */
public class SelfCheckAdapter extends RecyclerView.Adapter<SelfCheckAdapter.SelfCheckHolder> implements Constants {


    private Context mContext;
    private final List<DeviceCameraInfo> mData = new ArrayList<>();

    public SelfCheckAdapter(Context context) {
        mContext = context;
    }


    public void updateAdapter(List<DeviceCameraInfo> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public SelfCheckHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_self_check, parent, false);
        return new SelfCheckHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final SelfCheckHolder holder, final int position) {

//        DeviceCameraInfo deviceCameraInfo = mData.get(position);


    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class SelfCheckHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_iv_self_check)
        ImageView itemIvSelfCheck;
        @BindView(R.id.item_tv_self_check_state)
        TextView itemTvSelfCheckState;

        SelfCheckHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
