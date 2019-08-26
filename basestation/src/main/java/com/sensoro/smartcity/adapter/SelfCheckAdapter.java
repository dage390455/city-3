package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sensoro.basestation.R;
import com.sensoro.common.constant.Constants;
import com.sensoro.basestation.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 自检状态
 */
public class SelfCheckAdapter extends RecyclerView.Adapter<SelfCheckAdapter.SelfCheckHolder> implements Constants {


    private Context mContext;
    private final List<String> mData = new ArrayList<>();

    public SelfCheckAdapter(Context context) {
        mContext = context;
    }


    public void updateAdapter(List<String> data) {
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

        String deviceCameraInfo = mData.get(position);
        holder.itemTvSelfCheckState.setText(deviceCameraInfo);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class SelfCheckHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.item_iv_self_check)
        ImageView itemIvSelfCheck;
        @BindView(R2.id.item_tv_self_check_state)
        TextView itemTvSelfCheckState;

        SelfCheckHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
