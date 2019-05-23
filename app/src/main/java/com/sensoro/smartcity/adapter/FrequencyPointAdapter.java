package com.sensoro.smartcity.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FrequencyPointAdapter extends RecyclerView.Adapter<FrequencyPointAdapter.FrequencyPointHolder> {
    private final Context mContext;
    private List<CameraFilterModel.ListBean> mStateCountList = new ArrayList<>();

    public FrequencyPointAdapter(Context context) {
        mContext = context;
    }


    @Override
    public FrequencyPointHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_frequency_point, parent, false);
        FrequencyPointHolder holder = new FrequencyPointHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FrequencyPointHolder holder, int position) {
        Resources resources = mContext.getResources();


//        if (null != mStateCountList.get(position)) {
//            final CameraFilterModel.ListBean ic = mStateCountList.get(position);
//
//        }


    }


    public void updateDeviceTypList(List<CameraFilterModel.ListBean> list, boolean isMutilSelect) {
        mStateCountList.clear();
        mStateCountList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(FrequencyPointHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        onBindViewHolder(holder, position);
    }


    @Override
    public int getItemCount() {
        return 20;
    }


    class FrequencyPointHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_poit_tv)
        TextView itemPointTv;


        FrequencyPointHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
